package net.swedz.extended_industrialization.machines.components.craft.multiplied;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.api.machine.component.InventoryAccess;
import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.components.craft.ModularCrafterAccess;
import net.swedz.extended_industrialization.machines.components.craft.ModularCrafterAccessBehavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static aztech.modern_industrialization.util.Simulation.*;

/**
 * Most of the code here was directly copied from {@link CrafterComponent}, please understand.
 * <br><br>
 * Aside from formatting, here are the changes I made:
 * <ul>
 *     <li>Instead of using <code>behavior.recipeType()</code> to get the recipe type, it is fetched from <code>recipeTypeGetter</code></li>
 *     <li>Recipe inputs and outputs are multiplied by the <code>recipeMultiplier</code> variable</li>
 * </ul>
 */
public final class MultipliedCrafterComponent implements IComponent.ServerOnly, ModularCrafterAccess
{
	private final MachineProcessCondition.Context conditionContext;
	
	private final MultiblockInventoryComponent inventory;
	private final ModularCrafterAccessBehavior behavior;
	
	private final Supplier<MachineRecipeType> recipeTypeGetter;
	private final Supplier<Integer>           maxMultiplierGetter;
	private final Supplier<EuCostTransformer> euCostTransformer;
	
	private RecipeHolder<MachineRecipe> activeRecipe = null;
	private ResourceLocation            delayedActiveRecipe;
	
	private int recipeMultiplier = 1;
	
	private long usedEnergy;
	private long recipeEnergy;
	private long recipeMaxEu;
	
	private int efficiencyTicks;
	private int maxEfficiencyTicks;
	
	private long previousBaseEu = -1;
	private long previousMaxEu  = -1;
	
	private int lastInvHash    = 0;
	private int lastForcedTick = 0;
	
	public MultipliedCrafterComponent(MachineBlockEntity blockEntity, MultiblockInventoryComponent inventory, ModularCrafterAccessBehavior behavior,
									  Supplier<MachineRecipeType> recipeTypeGetter, Supplier<Integer> maxMultiplierGetter, Supplier<EuCostTransformer> euCostTransformer)
	{
		this.inventory = inventory;
		this.behavior = behavior;
		this.conditionContext = () -> blockEntity;
		this.recipeTypeGetter = recipeTypeGetter;
		this.maxMultiplierGetter = maxMultiplierGetter;
		this.euCostTransformer = euCostTransformer;
	}
	
	public MachineRecipeType getRecipeType()
	{
		return recipeTypeGetter.get();
	}
	
	public int getMaxMultiplier()
	{
		return maxMultiplierGetter.get();
	}
	
	public long transformEuCost(long eu)
	{
		return euCostTransformer.get().transform(this, eu);
	}
	
	@Override
	public InventoryAccess getInventory()
	{
		return inventory;
	}
	
	@Override
	public ModularCrafterAccessBehavior getBehavior()
	{
		return behavior;
	}
	
	public int getRecipeMultiplier()
	{
		return recipeMultiplier;
	}
	
	@Override
	public float getProgress()
	{
		return (float) usedEnergy / recipeEnergy;
	}
	
	@Override
	public int getEfficiencyTicks()
	{
		return efficiencyTicks;
	}
	
	@Override
	public int getMaxEfficiencyTicks()
	{
		return maxEfficiencyTicks;
	}
	
	@Override
	public boolean hasActiveRecipe()
	{
		return activeRecipe != null;
	}
	
	@Override
	public long getBaseRecipeEu()
	{
		return activeRecipe.value().eu;
	}
	
	@Override
	public long getCurrentRecipeEu()
	{
		return recipeMaxEu;
	}
	
	@Override
	public void decreaseEfficiencyTicks()
	{
		efficiencyTicks = Math.max(efficiencyTicks - 1, 0);
		this.clearActiveRecipeIfPossible();
	}
	
	@Override
	public void increaseEfficiencyTicks(int increment)
	{
		efficiencyTicks = Math.min(efficiencyTicks + increment, maxEfficiencyTicks);
	}
	
	/**
	 * Attempt to re-lock hatches to continue the active recipe.
	 *
	 * @return True if there is no current recipe or if the hatches could be locked
	 * for it, false otherwise.
	 */
	public boolean tryContinueRecipe()
	{
		this.loadDelayedActiveRecipe();
		
		if(activeRecipe != null && recipeMultiplier != 0)
		{
			if(this.putItemOutputs(activeRecipe.value(), recipeMultiplier, true, false) && this.putFluidOutputs(activeRecipe.value(), recipeMultiplier, true, false))
			{
				// Relock stacks
				this.putItemOutputs(activeRecipe.value(), recipeMultiplier, true, true);
				this.putFluidOutputs(activeRecipe.value(), recipeMultiplier, true, true);
			}
			else
			{
				return false;
			}
		}
		
		return true;
	}
	
	private void loadDelayedActiveRecipe()
	{
		if(delayedActiveRecipe != null)
		{
			activeRecipe = this.getRecipeType().getRecipe(behavior.getCrafterWorld(), delayedActiveRecipe);
			delayedActiveRecipe = null;
			if(activeRecipe == null)
			{
				// If a recipe got removed, we need to reset the efficiency and the used energy
				// to allow the machine to resume processing.
				efficiencyTicks = 0;
				usedEnergy = 0;
			}
		}
	}
	
	private boolean updateActiveRecipe()
	{
		// Only then can we run the iteration over the recipes
		for(RecipeHolder<MachineRecipe> recipe : this.getRecipes())
		{
			if(behavior.isRecipeBanned(recipe.value().eu))
			{
				continue;
			}
			if(this.tryStartRecipe(recipe.value()))
			{
				// Make sure we recalculate the max efficiency ticks if the recipe changes or if
				// the efficiency has reached 0 (the latter is to recalculate the efficiency for
				// 0.3.6 worlds without having to break and replace the machines)
				if(activeRecipe != recipe || efficiencyTicks == 0)
				{
					maxEfficiencyTicks = this.getRecipeMaxEfficiencyTicks(recipe.value());
				}
				activeRecipe = recipe;
				usedEnergy = 0;
				recipeEnergy = this.transformEuCost(recipe.value().getTotalEu());
				recipeMaxEu = this.getRecipeMaxEu(recipe.value().eu, recipeEnergy, efficiencyTicks);
				return true;
			}
		}
		return false;
	}
	
	private Iterable<RecipeHolder<MachineRecipe>> getRecipes()
	{
		if(this.getRecipeType() == null)
		{
			return Collections.emptyList();
		}
		else if(efficiencyTicks > 0)
		{
			return Collections.singletonList(activeRecipe);
		}
		else
		{
			int currentHash = inventory.hash();
			if(currentHash == lastInvHash)
			{
				if(lastForcedTick == 0)
				{
					lastForcedTick = 100;
				}
				else
				{
					--lastForcedTick;
					return Collections.emptyList();
				}
			}
			else
			{
				lastInvHash = currentHash;
			}
			
			ServerLevel serverWorld = (ServerLevel) behavior.getCrafterWorld();
			MachineRecipeType recipeType = this.getRecipeType();
			List<RecipeHolder<MachineRecipe>> recipes = new ArrayList<>(recipeType.getFluidOnlyRecipes(serverWorld));
			for(ConfigurableItemStack stack : inventory.getItemInputs())
			{
				if(!stack.isEmpty())
				{
					recipes.addAll(recipeType.getMatchingRecipes(serverWorld, stack.getResource().getItem()));
				}
			}
			return recipes;
		}
	}
	
	private boolean tryStartRecipe(MachineRecipe recipe, int recipeMultiplier)
	{
		if(this.takeItemInputs(recipe, recipeMultiplier, true) && this.takeFluidInputs(recipe, recipeMultiplier, true) &&
		   this.putItemOutputs(recipe, recipeMultiplier, true, false) && this.putFluidOutputs(recipe, recipeMultiplier, true, false) &&
		   recipe.conditionsMatch(conditionContext))
		{
			this.takeItemInputs(recipe, recipeMultiplier, false);
			this.takeFluidInputs(recipe, recipeMultiplier, false);
			this.putItemOutputs(recipe, recipeMultiplier, true, true);
			this.putFluidOutputs(recipe, recipeMultiplier, true, true);
			return true;
		}
		return false;
	}
	
	private int calculateItemInputRecipeMultiplier(MachineRecipe recipe)
	{
		List<ItemStack> itemsInHatches = inventory.getItemInputs().stream()
				.map((item) -> item.getResource().toStack((int) item.getAmount()))
				.toList();
		
		int itemMultiplier = this.getMaxMultiplier();
		for(MachineRecipe.ItemInput input : recipe.itemInputs)
		{
			int countItemsInHatches = 0;
			for(ItemStack stack : itemsInHatches)
			{
				if(input.matches(stack))
				{
					countItemsInHatches += stack.getCount();
				}
			}
			
			int multiplier = countItemsInHatches / input.amount;
			if(multiplier < itemMultiplier)
			{
				itemMultiplier = multiplier;
			}
			if(itemMultiplier <= 1)
			{
				break;
			}
		}
		return itemMultiplier;
	}
	
	private int calculateItemOutputRecipeMultiplier(MachineRecipe recipe)
	{
		List<ItemStack> itemsInHatches = inventory.getItemOutputs().stream()
				.map((item) -> item.getResource().toStack((int) item.getAmount()))
				.toList();
		
		int itemMultiplier = this.getMaxMultiplier();
		for(MachineRecipe.ItemOutput output : recipe.itemOutputs)
		{
			if(output.probability < 1)
			{
				continue;
			}
			
			int maxOutputCount = output.amount * this.getMaxMultiplier();
			
			int outputSpace = 0;
			for(ConfigurableItemStack item : inventory.getItemOutputs())
			{
				ItemVariant key = item.getResource();
				if(key.getItem() == output.item || key.isBlank())
				{
					int remainingCapacity = (int) item.getRemainingCapacityFor(ItemVariant.of(output.item));
					outputSpace += remainingCapacity;
					if(outputSpace >= maxOutputCount)
					{
						outputSpace = maxOutputCount;
						break;
					}
				}
			}
			
			int multiplier = outputSpace / output.amount;
			if(multiplier < itemMultiplier)
			{
				itemMultiplier = multiplier;
			}
			if(itemMultiplier <= 1)
			{
				break;
			}
		}
		
		return itemMultiplier;
	}
	
	private int calculateFluidInputRecipeMultiplier(MachineRecipe recipe)
	{
		int fluidMultiplier = this.getMaxMultiplier();
		for(MachineRecipe.FluidInput input : recipe.fluidInputs)
		{
			long countFluidInHatches = 0;
			for(ConfigurableFluidStack stack : inventory.getFluidInputs())
			{
				if(stack.getResource().equals(FluidVariant.of(input.fluid)))
				{
					countFluidInHatches += stack.getAmount();
				}
			}
			
			int multiplier = (int) (countFluidInHatches / input.amount);
			if(multiplier < fluidMultiplier)
			{
				fluidMultiplier = multiplier;
			}
			if(fluidMultiplier <= 1)
			{
				break;
			}
		}
		return fluidMultiplier;
	}
	
	private int calculateFluidOutputRecipeMultiplier(MachineRecipe recipe)
	{
		int fluidMultiplier = this.getMaxMultiplier();
		for(int i = 0; i < Math.min(recipe.fluidOutputs.size(), behavior.getMaxFluidOutputs()); ++i)
		{
			MachineRecipe.FluidOutput output = recipe.fluidOutputs.get(i);
			
			if(output.probability < 1)
			{
				continue;
			}
			
			long maxOutputCount = output.amount * this.getMaxMultiplier();
			
			outer:
			for(int tries = 0; tries < 2; tries++)
			{
				for(ConfigurableFluidStack stack : inventory.getFluidOutputs())
				{
					FluidVariant outputKey = FluidVariant.of(output.fluid);
					if(stack.isResourceAllowedByLock(outputKey) && ((tries == 1 && stack.isResourceBlank()) || stack.getResource().equals(outputKey)))
					{
						long outputSpace = Math.min(stack.getRemainingSpace(), maxOutputCount);
						
						int multiplier = (int) (outputSpace / output.amount);
						if(multiplier < fluidMultiplier)
						{
							fluidMultiplier = multiplier;
						}
						if(fluidMultiplier <= 1)
						{
							break outer;
						}
					}
				}
			}
		}
		
		return fluidMultiplier;
	}
	
	private boolean tryStartRecipe(MachineRecipe recipe)
	{
		if(this.getMaxMultiplier() > 1)
		{
			int itemInputMultiplier = this.calculateItemInputRecipeMultiplier(recipe);
			if(itemInputMultiplier > 1)
			{
				int itemOutputMultiplier = this.calculateItemOutputRecipeMultiplier(recipe);
				if(itemOutputMultiplier > 1)
				{
					int itemMultiplier = Math.min(itemInputMultiplier, itemOutputMultiplier);
					
					int fluidInputMultiplier = this.calculateFluidInputRecipeMultiplier(recipe);
					if(fluidInputMultiplier > 1)
					{
						int fluidOutputMultiplier = this.calculateFluidOutputRecipeMultiplier(recipe);
						if(fluidOutputMultiplier > 1)
						{
							int fluidMultiplier = Math.min(fluidInputMultiplier, fluidOutputMultiplier);
							
							int multiplier = Math.min(itemMultiplier, fluidMultiplier);
							if(this.tryStartRecipe(recipe, multiplier))
							{
								recipeMultiplier = multiplier;
								return true;
							}
						}
					}
				}
			}
		}
		
		recipeMultiplier = 1;
		return this.tryStartRecipe(recipe, 1);
	}
	
	private boolean takeItemInputs(MachineRecipe recipe, int recipeMultiplier, boolean simulate)
	{
		List<ConfigurableItemStack> baseList = inventory.getItemInputs();
		List<ConfigurableItemStack> stacks = simulate ? ConfigurableItemStack.copyList(baseList) : baseList;
		
		boolean ok = true;
		for(MachineRecipe.ItemInput input : recipe.itemInputs)
		{
			// if we are not simulating, there is a chance we don't need to take this output
			if(!simulate && input.probability < 1)
			{
				if(ThreadLocalRandom.current().nextFloat() >= input.probability)
				{
					continue;
				}
			}
			int remainingAmount = input.amount * recipeMultiplier;
			for(ConfigurableItemStack stack : stacks)
			{
				// TODO: ItemStack creation slow?
				if(stack.getAmount() > 0 && input.matches(stack.getResource().toStack()))
				{
					int taken = Math.min((int) stack.getAmount(), remainingAmount);
					if(taken > 0 && !simulate)
					{
						behavior.getStatsOrDummy().addUsedItems(stack.getResource().getItem(), taken);
					}
					stack.decrement(taken);
					remainingAmount -= taken;
					if(remainingAmount == 0)
					{
						break;
					}
				}
			}
			if(remainingAmount > 0)
			{
				ok = false;
			}
		}
		
		return ok;
	}
	
	private boolean takeFluidInputs(MachineRecipe recipe, int recipeMultiplier, boolean simulate)
	{
		List<ConfigurableFluidStack> baseList = inventory.getFluidInputs();
		List<ConfigurableFluidStack> stacks = simulate ? ConfigurableFluidStack.copyList(baseList) : baseList;
		
		boolean ok = true;
		for(MachineRecipe.FluidInput input : recipe.fluidInputs)
		{
			// if we are not simulating, there is a chance we don't need to take this output
			if(!simulate && input.probability < 1)
			{
				if(ThreadLocalRandom.current().nextFloat() >= input.probability)
				{
					continue;
				}
			}
			long remainingAmount = input.amount * recipeMultiplier;
			for(ConfigurableFluidStack stack : stacks)
			{
				if(stack.getResource().equals(FluidVariant.of(input.fluid)))
				{
					long taken = Math.min(remainingAmount, stack.getAmount());
					if(taken > 0 && !simulate)
					{
						behavior.getStatsOrDummy().addUsedFluids(stack.getResource().getFluid(), taken);
					}
					stack.decrement(taken);
					remainingAmount -= taken;
					if(remainingAmount == 0)
					{
						break;
					}
				}
			}
			if(remainingAmount > 0)
			{
				ok = false;
			}
		}
		return ok;
	}
	
	@SuppressWarnings("deprecation")
	private boolean putItemOutputs(MachineRecipe recipe, int recipeMultiplier, boolean simulate, boolean toggleLock)
	{
		List<ConfigurableItemStack> baseList = inventory.getItemOutputs();
		List<ConfigurableItemStack> stacks = simulate ? ConfigurableItemStack.copyList(baseList) : baseList;
		
		List<Integer> locksToToggle = new ArrayList<>();
		List<Item> lockItems = new ArrayList<>();
		
		boolean ok = true;
		for(MachineRecipe.ItemOutput output : recipe.itemOutputs)
		{
			if(output.probability < 1)
			{
				if(simulate)
				{
					continue; // don't check output space for probabilistic recipes
				}
				float randFloat = ThreadLocalRandom.current().nextFloat();
				if(randFloat > output.probability)
				{
					continue;
				}
			}
			int remainingAmount = output.amount * recipeMultiplier;
			// Try to insert in non-empty stacks or locked first, then also allow insertion
			// in empty stacks.
			for(int loopRun = 0; loopRun < 2; loopRun++)
			{
				int stackId = 0;
				for(ConfigurableItemStack stack : stacks)
				{
					stackId++;
					ItemVariant key = stack.getResource();
					if(key.getItem() == output.item || key.isBlank())
					{
						// If simulating or chanced output, respect the adjusted capacity.
						// If putting the output, don't respect the adjusted capacity in case it was
						// reduced during the processing.
						int remainingCapacity = simulate || output.probability < 1 ? (int) stack.getRemainingCapacityFor(ItemVariant.of(output.item))
								: output.item.getMaxStackSize() - (int) stack.getAmount();
						int ins = Math.min(remainingAmount, remainingCapacity);
						if(key.isBlank())
						{
							if((stack.isMachineLocked() || stack.isPlayerLocked() || loopRun == 1) && stack.isValid(new ItemStack(output.item)))
							{
								stack.setAmount(ins);
								stack.setKey(ItemVariant.of(output.item));
							}
							else
							{
								ins = 0;
							}
						}
						else
						{
							stack.increment(ins);
						}
						remainingAmount -= ins;
						if(ins > 0)
						{
							locksToToggle.add(stackId - 1);
							lockItems.add(output.item);
							if(!simulate)
							{
								behavior.getStatsOrDummy().addProducedItems(behavior.getCrafterWorld(), output.item, ins);
							}
						}
						if(remainingAmount == 0)
						{
							break;
						}
					}
				}
			}
			if(remainingAmount > 0)
			{
				ok = false;
			}
		}
		
		if(toggleLock)
		{
			for(int i = 0; i < locksToToggle.size(); i++)
			{
				baseList.get(locksToToggle.get(i)).enableMachineLock(lockItems.get(i));
			}
		}
		return ok;
	}
	
	private boolean putFluidOutputs(MachineRecipe recipe, int recipeMultiplier, boolean simulate, boolean toggleLock)
	{
		List<ConfigurableFluidStack> baseList = inventory.getFluidOutputs();
		List<ConfigurableFluidStack> stacks = simulate ? ConfigurableFluidStack.copyList(baseList) : baseList;
		
		List<Integer> locksToToggle = new ArrayList<>();
		List<Fluid> lockFluids = new ArrayList<>();
		
		boolean ok = true;
		for(int i = 0; i < Math.min(recipe.fluidOutputs.size(), behavior.getMaxFluidOutputs()); ++i)
		{
			MachineRecipe.FluidOutput output = recipe.fluidOutputs.get(i);
			if(output.probability < 1)
			{
				if(simulate)
				{
					continue; // don't check output space for probabilistic recipes
				}
				float randFloat = ThreadLocalRandom.current().nextFloat();
				if(randFloat > output.probability)
				{
					continue;
				}
			}
			// First, try to find a slot that contains the fluid. If we couldn't find one,
			// we insert in any stack
			outer:
			for(int tries = 0; tries < 2; ++tries)
			{
				for(int j = 0; j < stacks.size(); j++)
				{
					ConfigurableFluidStack stack = stacks.get(j);
					FluidVariant outputKey = FluidVariant.of(output.fluid);
					if(stack.isResourceAllowedByLock(outputKey)
					   && ((tries == 1 && stack.isResourceBlank()) || stack.getResource().equals(outputKey)))
					{
						long inserted = Math.min(output.amount * recipeMultiplier, stack.getRemainingSpace());
						if(inserted > 0)
						{
							stack.setKey(outputKey);
							stack.increment(inserted);
							locksToToggle.add(j);
							lockFluids.add(output.fluid);
							if(!simulate)
							{
								behavior.getStatsOrDummy().addProducedFluids(output.fluid, inserted);
							}
						}
						if(inserted < output.amount * recipeMultiplier)
						{
							ok = false;
						}
						break outer;
					}
				}
				if(tries == 1)
				{
					ok = false;
				}
			}
		}
		
		if(toggleLock)
		{
			for(int i = 0; i < locksToToggle.size(); i++)
			{
				baseList.get(locksToToggle.get(i)).enableMachineLock(lockFluids.get(i));
			}
		}
		return ok;
	}
	
	public boolean tickRecipe()
	{
		if(behavior.getCrafterWorld().isClientSide())
		{
			throw new IllegalStateException("May not call client side.");
		}
		
		boolean active = false;
		boolean enabled = behavior.isEnabled();
		
		boolean started = false;
		if(usedEnergy == 0 && enabled)
		{
			if(behavior.canConsumeEu(1))
			{
				started = this.updateActiveRecipe();
			}
		}
		
		long eu = 0;
		boolean finished = false;
		if(activeRecipe != null && (usedEnergy > 0 || started) && enabled)
		{
			recipeMaxEu = this.getRecipeMaxEu(activeRecipe.value().eu, recipeEnergy, efficiencyTicks);
			eu = activeRecipe.value().conditionsMatch(conditionContext) ? behavior.consumeEu(Math.min(recipeMaxEu, recipeEnergy - usedEnergy), ACT) : 0;
			active = eu > 0;
			usedEnergy += eu;
			
			if(usedEnergy == recipeEnergy)
			{
				this.putItemOutputs(activeRecipe.value(), recipeMultiplier, false, false);
				this.putFluidOutputs(activeRecipe.value(), recipeMultiplier, false, false);
				
				this.clearLocks();
				
				usedEnergy = 0;
				finished = true;
			}
		}
		
		if(activeRecipe != null && (previousBaseEu != behavior.getBaseRecipeEu() || previousMaxEu != behavior.getMaxRecipeEu()))
		{
			previousBaseEu = behavior.getBaseRecipeEu();
			previousMaxEu = behavior.getMaxRecipeEu();
			maxEfficiencyTicks = this.getRecipeMaxEfficiencyTicks(activeRecipe.value());
			efficiencyTicks = Math.min(efficiencyTicks, maxEfficiencyTicks);
		}
		
		// If we finished a recipe, we can add an efficiency tick
		if(finished)
		{
			if(efficiencyTicks < maxEfficiencyTicks)
			{
				efficiencyTicks++;
			}
		}
		// If we didn't use the max energy this tick and the recipe is still ongoing, remove one efficiency tick
		else if(eu < recipeMaxEu)
		{
			if(efficiencyTicks > 0)
			{
				efficiencyTicks--;
			}
		}
		
		// If the recipe is done, allow starting another one when the efficiency reaches 0
		this.clearActiveRecipeIfPossible();
		
		return active;
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
		tag.putLong("usedEnergy", usedEnergy);
		tag.putLong("recipeEnergy", recipeEnergy);
		tag.putLong("recipeMaxEu", recipeMaxEu);
		if(activeRecipe != null)
		{
			tag.putString("activeRecipe", activeRecipe.id().toString());
		}
		else if(delayedActiveRecipe != null)
		{
			tag.putString("activeRecipe", delayedActiveRecipe.toString());
		}
		tag.putInt("recipeMultiplier", recipeMultiplier);
		tag.putInt("efficiencyTicks", efficiencyTicks);
		tag.putInt("maxEfficiencyTicks", maxEfficiencyTicks);
	}
	
	@Override
	public void readNbt(CompoundTag tag, boolean isUpgradingMachine)
	{
		usedEnergy = tag.getInt("usedEnergy");
		recipeEnergy = tag.getInt("recipeEnergy");
		recipeMaxEu = tag.getInt("recipeMaxEu");
		delayedActiveRecipe = tag.contains("activeRecipe") ? new ResourceLocation(tag.getString("activeRecipe")) : null;
		if(delayedActiveRecipe == null && usedEnergy > 0)
		{
			usedEnergy = 0;
			EI.LOGGER.error("Had to set the usedEnergy of MultipliedCrafterComponent to 0, but that should never happen!");
		}
		recipeMultiplier = tag.getInt("recipeMultiplier");
		efficiencyTicks = tag.getInt("efficiencyTicks");
		maxEfficiencyTicks = tag.getInt("maxEfficiencyTicks");
	}
	
	private void clearActiveRecipeIfPossible()
	{
		if(efficiencyTicks == 0 && usedEnergy == 0)
		{
			activeRecipe = null;
			recipeMultiplier = 1;
		}
	}
	
	private long getRecipeMaxEu(long recipeEu, long totalEu, int efficiencyTicks)
	{
		long baseEu = Math.max(this.transformEuCost(behavior.getBaseRecipeEu()), this.transformEuCost(recipeEu));
		return Math.min(totalEu, Math.min((int) Math.floor(baseEu * CrafterComponent.getEfficiencyOverclock(efficiencyTicks)), this.transformEuCost(behavior.getMaxRecipeEu())));
	}
	
	private int getRecipeMaxEfficiencyTicks(MachineRecipe recipe)
	{
		long eu = recipe.eu;
		long totalEu = this.transformEuCost(recipe.getTotalEu());
		for(int ticks = 0; true; ++ticks)
		{
			if(this.getRecipeMaxEu(eu, totalEu, ticks) == Math.min(this.transformEuCost(behavior.getMaxRecipeEu()), totalEu))
			{
				return ticks;
			}
		}
	}
	
	private void clearLocks()
	{
		for(ConfigurableItemStack stack : inventory.getItemOutputs())
		{
			if(stack.isMachineLocked())
			{
				stack.disableMachineLock();
			}
		}
		for(ConfigurableFluidStack stack : inventory.getFluidOutputs())
		{
			if(stack.isMachineLocked())
			{
				stack.disableMachineLock();
			}
		}
	}
	
	public void lockRecipe(ResourceLocation recipeId, net.minecraft.world.entity.player.Inventory inventory)
	{
		// Find MachineRecipe
		MachineRecipeType recipeType = this.getRecipeType();
		if(recipeType == null)
		{
			return;
		}
		Optional<RecipeHolder<MachineRecipe>> optionalMachineRecipe = recipeType.getRecipes(behavior.getCrafterWorld()).stream()
				.filter((recipe) -> recipe.id().equals(recipeId)).findFirst();
		if(optionalMachineRecipe.isEmpty())
		{
			return;
		}
		RecipeHolder<MachineRecipe> recipe = optionalMachineRecipe.get();
		// ITEM INPUTS
		outer:
		for(MachineRecipe.ItemInput input : recipe.value().itemInputs)
		{
			for(ConfigurableItemStack stack : this.inventory.getItemInputs())
			{
				if(stack.getLockedInstance() != null && input.matches(new ItemStack(stack.getLockedInstance())))
				{
					continue outer;
				}
			}
			Item targetItem = null;
			// Find the first match in the player inventory (useful for logs for example)
			for(int i = 0; i < inventory.getContainerSize(); i++)
			{
				ItemStack playerStack = inventory.getItem(i);
				if(!playerStack.isEmpty() && input.matches(new ItemStack(playerStack.getItem())))
				{
					targetItem = playerStack.getItem();
					break;
				}
			}
			if(targetItem == null)
			{
				// Find the first match that is an item from MI (useful for ingots for example)
				for(Item item : input.getInputItems())
				{
					ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
					if(id.getNamespace().equals(MI.ID))
					{
						targetItem = item;
						break;
					}
				}
			}
			if(targetItem == null)
			{
				// If there is only one value in the tag, pick that one
				if(input.getInputItems().size() == 1)
				{
					targetItem = input.getInputItems().get(0);
				}
			}
			
			if(targetItem != null)
			{
				AbstractConfigurableStack.playerLockNoOverride(targetItem, this.inventory.getItemInputs());
			}
		}
		// ITEM OUTPUTS
		outer:
		for(MachineRecipe.ItemOutput output : recipe.value().itemOutputs)
		{
			for(ConfigurableItemStack stack : this.inventory.getItemOutputs())
			{
				if(stack.getLockedInstance() == output.item)
					continue outer;
			}
			AbstractConfigurableStack.playerLockNoOverride(output.item, this.inventory.getItemOutputs());
		}
		
		// FLUID INPUTS
		outer:
		for(MachineRecipe.FluidInput input : recipe.value().fluidInputs)
		{
			for(ConfigurableFluidStack stack : this.inventory.getFluidInputs())
			{
				if(stack.isLockedTo(input.fluid))
				{
					continue outer;
				}
			}
			AbstractConfigurableStack.playerLockNoOverride(input.fluid, this.inventory.getFluidInputs());
		}
		// FLUID OUTPUTS
		outer:
		for(MachineRecipe.FluidOutput output : recipe.value().fluidOutputs)
		{
			for(ConfigurableFluidStack stack : this.inventory.getFluidOutputs())
			{
				if(stack.isLockedTo(output.fluid))
					continue outer;
			}
			AbstractConfigurableStack.playerLockNoOverride(output.fluid, this.inventory.getFluidOutputs());
		}
		
		// LOCK ITEMS
		if(recipe.value().itemInputs.size() > 0 || recipe.value().itemOutputs.size() > 0)
		{
			lockAll(this.inventory.getItemInputs());
			lockAll(this.inventory.getItemOutputs());
		}
		// LOCK FLUIDS
		if(recipe.value().fluidInputs.size() > 0 || recipe.value().fluidOutputs.size() > 0)
		{
			lockAll(this.inventory.getFluidInputs());
			lockAll(this.inventory.getFluidOutputs());
		}
	}
	
	private static void lockAll(List<? extends AbstractConfigurableStack<?, ?>> stacks)
	{
		for(AbstractConfigurableStack stack : stacks)
		{
			if(stack.isEmpty() && stack.getLockedInstance() == null)
			{
				stack.togglePlayerLock();
			}
		}
	}
}
