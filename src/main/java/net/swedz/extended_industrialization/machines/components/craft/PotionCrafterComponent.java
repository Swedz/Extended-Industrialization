package net.swedz.extended_industrialization.machines.components.craft;

import aztech.modern_industrialization.api.machine.component.InventoryAccess;
import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.components.MachineInventoryComponent;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.util.Simulation;
import com.google.common.collect.Maps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.api.MachineInventoryHelper;
import net.swedz.extended_industrialization.datamaps.PotionBrewing;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class PotionCrafterComponent implements IComponent.ServerOnly, CrafterAccessWithBehavior
{
	private final Params params;
	private final MachineInventoryComponent inventory;
	private final CrafterAccessBehavior behavior;
	
	private BrewingPickRecipeResult processResult;
	
	private long usedEnergy;
	private long recipeMaxEu;
	
	private int efficiencyTicks;
	private int maxEfficiencyTicks;
	
	private long previousBaseEu = -1;
	private long previousMaxEu  = -1;
	
	public PotionCrafterComponent(Params params, MachineInventoryComponent inventory, CrafterAccessBehavior behavior)
	{
		this.params = params;
		this.inventory = inventory;
		this.behavior = behavior;
	}
	
	@Override
	public CrafterAccessBehavior getBehavior()
	{
		return behavior;
	}
	
	@Override
	public InventoryAccess getInventory()
	{
		return inventory;
	}
	
	@Override
	public boolean hasActiveRecipe()
	{
		return processResult != null;
	}
	
	@Override
	public float getProgress()
	{
		return processResult != null ? (float) usedEnergy / processResult.recipe().totalEuCost() : 0;
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
	
	public void decreaseEfficiencyTicks()
	{
		efficiencyTicks = Math.max(efficiencyTicks - 1, 0);
		this.clearActiveRecipeIfPossible();
	}
	
	public void increaseEfficiencyTicks(int increment)
	{
		efficiencyTicks = Math.min(efficiencyTicks + increment, maxEfficiencyTicks);
	}
	
	@Override
	public long getBaseRecipeEu()
	{
		return processResult.recipe().euCost();
	}
	
	@Override
	public long getCurrentRecipeEu()
	{
		return recipeMaxEu;
	}
	
	private void clearActiveRecipeIfPossible()
	{
		if(efficiencyTicks == 0 && usedEnergy == 0)
		{
			processResult = null;
		}
	}
	
	private Optional<BrewingPickRecipeResult> findRecipe()
	{
		// Check if there's a recipe for these reagents and capture all of the recipe steps
		List<IBrewingRecipe> recipeSteps = Lists.newArrayList();
		List<ItemStack> recipeReagentStacks = Lists.newArrayList();
		List<ConfigurableItemStack> resultReagentSlotsToRemoveFrom = Lists.newArrayList();
		for(ConfigurableItemStack slotReagent : params.reagent().slots(inventory.getItemInputs()))
		{
			ItemStack slotReagentStack = slotReagent.toStack();
			if(slotReagent.isEmpty() || slotReagentStack.isEmpty())
			{
				continue;
			}
			for(IBrewingRecipe otherRecipe : BrewingRecipeRegistry.getRecipes())
			{
				if(otherRecipe.isIngredient(slotReagentStack))
				{
					recipeSteps.add(otherRecipe);
					recipeReagentStacks.add(slotReagentStack);
					resultReagentSlotsToRemoveFrom.add(slotReagent);
					break;
				}
			}
		}
		if(recipeSteps.size() == 0)
		{
			return Optional.empty();
		}
		
		// Find the slots that can be used for outputting
		List<ConfigurableItemStack> resultOutputSlotsToAddTo = Lists.newArrayList();
		for(ConfigurableItemStack slot : params.output().slots(inventory.getItemOutputs()))
		{
			if(slot.isEmpty())
			{
				resultOutputSlotsToAddTo.add(slot);
			}
		}
		
		// Check all of the slots
		outer:
		for(ConfigurableItemStack slot : params.bottle().slots(inventory.getItemInputs()))
		{
			// Get this bottle input
			ItemStack slotStack = slot.toStack();
			if(slot.isEmpty() || slotStack.isEmpty())
			{
				continue;
			}
			
			// Transform this bottle stack and check if we need to consume water to do so
			ItemStack slotStackConverted;
			boolean resultConsumeWater = false;
			if(slotStack.is(Items.GLASS_BOTTLE))
			{
				slotStackConverted = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
				resultConsumeWater = true;
			}
			else
			{
				slotStackConverted = slotStack.copy();
			}
			
			// Check if there's a recipe for the reagents given this bottle input
			// If there are multiple reagents, it will iterate over them and find the final one and generate the output itemstack for it
			// If there is a reagent that does not have a valid recipe with this bottle input in the correct sequential order, the recipe will fail to be picked
			ItemStack resultOutput = slotStackConverted.copy();
			int recipeIndex = 0;
			for(IBrewingRecipe recipe : recipeSteps)
			{
				ItemStack slotReagentStack = recipeReagentStacks.get(recipeIndex);
				if(recipe.isInput(resultOutput))
				{
					resultOutput = recipe.getOutput(resultOutput, slotReagentStack);
				}
				else
				{
					continue outer;
				}
				recipeIndex++;
			}
			Potion potion = PotionUtils.getPotion(resultOutput);
			PotionBrewing potionBrewingData = PotionBrewing.getFor(potion);
			if(potionBrewingData == null)
			{
				potionBrewingData = PotionBrewing.getFor(Potions.EMPTY);
				if(potionBrewingData == null)
				{
					EI.LOGGER.warn("Failed to fetch potion brewing data from datamap for default potion! Perhaps the default was overridden with nothing? (operating on potion {})", BuiltInRegistries.POTION.getKey(potion));
					continue;
				}
			}
			
			// Make sure there are enough output slots for this recipe
			if(resultOutputSlotsToAddTo.size() < potionBrewingData.bottles())
			{
				continue;
			}
			
			// Make sure there is enough water for this recipe
			if(resultConsumeWater && potionBrewingData.water() > 0 && !MachineInventoryHelper.hasFluid(inventory.getFluidInputs(), Fluids.WATER, potionBrewingData.water()))
			{
				continue;
			}
			
			// Make sure there is enough blazing essence in the machine
			if(potionBrewingData.blazingEssence() > 0 && !MachineInventoryHelper.hasFluid(inventory.getFluidInputs(), EIFluids.BLAZING_ESSENCE, potionBrewingData.blazingEssence()))
			{
				continue;
			}
			
			// Make sure there's enough of the bottles in this input
			Map<Integer, Integer> resultBottleSlotsToRemoveFrom = Maps.newHashMap();
			int count = 0;
			boolean hasEnoughBottles = false;
			int bottleIndex = 0;
			for(ConfigurableItemStack otherSlot : params.bottle().slots(inventory.getItemInputs()))
			{
				ItemStack otherSlotStack = otherSlot.toStack();
				// We compare with the original stack so that other stacks are converted as well if need be
				if(ItemStack.isSameItemSameTags(slotStack, otherSlotStack))
				{
					int amountToRemove = Math.min((int) otherSlot.getAmount(), potionBrewingData.bottles() - count);
					count += amountToRemove;
					if(amountToRemove > 0)
					{
						resultBottleSlotsToRemoveFrom.put(bottleIndex, amountToRemove);
					}
					if(count >= potionBrewingData.bottles())
					{
						hasEnoughBottles = true;
						break;
					}
				}
				bottleIndex++;
			}
			if(!hasEnoughBottles)
			{
				continue;
			}
			
			return Optional.of(new BrewingPickRecipeResult(
					potion, potionBrewingData,
					resultConsumeWater,
					resultBottleSlotsToRemoveFrom, resultReagentSlotsToRemoveFrom,
					resultOutput,
					resultOutputSlotsToAddTo
			));
		}
		
		return Optional.empty();
	}
	
	private void doBlazeEssenceStuff()
	{
		ConfigurableItemStack slotPowder = params.blazePowder().slot(inventory.getItemInputs());
		if(slotPowder.getAmount() == 0)
		{
			return;
		}
		
		ConfigurableFluidStack slotEssence = params.blazingEssence().slot(inventory.getFluidInputs());
		if(slotEssence.getAmount() > 0)
		{
			return;
		}
		
		slotPowder.decrement(1);
		
		slotEssence.setKey(FluidVariant.of(EIFluids.BLAZING_ESSENCE.asFluid()));
		slotEssence.increment(20);
	}
	
	private boolean updateActiveRecipe()
	{
		Optional<BrewingPickRecipeResult> found = this.findRecipe();
		if(found.isPresent())
		{
			BrewingPickRecipeResult recipe = found.get();
			
			// Consume the inputs
			if(recipe.recipe().blazingEssence() > 0)
			{
				MachineInventoryHelper.consumeFluid(inventory.getFluidInputs(), EIFluids.BLAZING_ESSENCE, recipe.recipe().blazingEssence(), Simulation.ACT);
			}
			if(recipe.consumeWater())
			{
				MachineInventoryHelper.consumeFluid(inventory.getFluidInputs(), Fluids.WATER, recipe.recipe().water(), Simulation.ACT);
			}
			List<ConfigurableItemStack> bottleSlots = params.bottle().slots(inventory.getItemInputs());
			for(Map.Entry<Integer, Integer> entry : recipe.bottleSlotsToRemoveFrom().entrySet())
			{
				bottleSlots.get(entry.getKey()).decrement(entry.getValue());
			}
			for(ConfigurableItemStack slot : recipe.reagentSlotsToRemoveFrom())
			{
				slot.decrement(1);
			}
			
			// Make sure we recalculate the max efficiency ticks if the recipe changes or if
			// the efficiency has reached 0 (the latter is to recalculate the efficiency for
			// 0.3.6 worlds without having to break and replace the machines)
			if(processResult == null || processResult.potion() != recipe.potion() || efficiencyTicks == 0)
			{
				maxEfficiencyTicks = this.getRecipeMaxEfficiencyTicks(recipe.recipe());
			}
			// Start the actual recipe
			processResult = recipe;
			usedEnergy = 0;
			recipeMaxEu = this.getRecipeMaxEu(recipe.recipe(), efficiencyTicks);
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean tickRecipe()
	{
		this.doBlazeEssenceStuff();
		
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
		if(processResult != null && (usedEnergy > 0 || started) && enabled)
		{
			recipeMaxEu = this.getRecipeMaxEu(processResult.recipe(), efficiencyTicks);
			long amountToConsume = Math.min(recipeMaxEu, processResult.recipe().totalEuCost() - usedEnergy);
			eu = behavior.consumeEu(amountToConsume, Simulation.ACT);
			active = eu > 0;
			usedEnergy += eu;
			
			if(usedEnergy == processResult.recipe().totalEuCost())
			{
				// TODO output better
				processResult.push();
				
				usedEnergy = 0;
				finished = true;
			}
		}
		
		if(processResult != null && (previousBaseEu != behavior.getBaseRecipeEu() || previousMaxEu != behavior.getMaxRecipeEu()))
		{
			previousBaseEu = behavior.getBaseRecipeEu();
			previousMaxEu = behavior.getMaxRecipeEu();
			maxEfficiencyTicks = this.getRecipeMaxEfficiencyTicks(processResult.recipe());
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
	
	private long getRecipeMaxEu(PotionBrewing recipe, int efficiencyTicks)
	{
		long baseEu = Math.max(behavior.getBaseRecipeEu(), recipe.euCost());
		return Math.min(recipe.totalEuCost(), Math.min((int) Math.floor(baseEu * CrafterComponent.getEfficiencyOverclock(efficiencyTicks)), behavior.getMaxRecipeEu()));
	}
	
	private int getRecipeMaxEfficiencyTicks(PotionBrewing recipe)
	{
		for(int ticks = 0; true; ++ticks)
		{
			if(this.getRecipeMaxEu(recipe, ticks) == Math.min(behavior.getMaxRecipeEu(), recipe.totalEuCost()))
			{
				return ticks;
			}
		}
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
		tag.putLong("usedEnergy", usedEnergy);
		tag.putLong("recipeMaxEu", recipeMaxEu);
		// TODO store active recipe
		/*if(activeRecipe != null)
		{
			tag.putString("activeRecipe", this.activeRecipe.id().toString());
		}
		else if(delayedActiveRecipe != null)
		{
			tag.putString("activeRecipe", this.delayedActiveRecipe.toString());
		}*/
		tag.putInt("efficiencyTicks", efficiencyTicks);
		tag.putInt("maxEfficiencyTicks", maxEfficiencyTicks);
	}
	
	@Override
	public void readNbt(CompoundTag tag, boolean isUpgradingMachine)
	{
		usedEnergy = tag.getInt("usedEnergy");
		recipeMaxEu = tag.getInt("recipeMaxEu");
		// TODO read active recipe
		/*this.delayedActiveRecipe = tag.contains("activeRecipe") ? new ResourceLocation(tag.getString("activeRecipe")) : null;
		if(delayedActiveRecipe == null && usedEnergy > 0)
		{
			usedEnergy = 0;
			MI.LOGGER.error("Had to set the usedEnergy of CrafterComponent to 0, but that should never happen!");
		}*/
		efficiencyTicks = tag.getInt("efficiencyTicks");
		maxEfficiencyTicks = tag.getInt("maxEfficiencyTicks");
	}
	
	private record BrewingPickRecipeResult(
			Potion potion, PotionBrewing recipe,
			boolean consumeWater,
			Map<Integer, Integer> bottleSlotsToRemoveFrom,
			List<ConfigurableItemStack> reagentSlotsToRemoveFrom,
			ItemStack output,
			List<ConfigurableItemStack> outputSlotsToAddTo
	)
	{
		public void push()
		{
			// TODO use PotionCrafterComponent.inventory.getItemOutputs() so that each potion isnt forced to go to its own slot if they're stackable
			for(int i = 0; i < recipe.bottles(); i++)
			{
				ConfigurableItemStack slot = outputSlotsToAddTo.get(i);
				slot.setKey(ItemVariant.of(output.copy()));
				slot.setAmount(1);
			}
		}
	}
	
	public record SlotRange<T extends AbstractConfigurableStack>(int start, int end)
	{
		public static SlotRange<ConfigurableItemStack> item(int start, int end)
		{
			return new SlotRange<>(start, end);
		}
		
		public static SlotRange<ConfigurableItemStack> item(int slot)
		{
			return new SlotRange<>(slot);
		}
		
		public static SlotRange<ConfigurableFluidStack> fluid(int start, int end)
		{
			return new SlotRange<>(start, end);
		}
		
		public static SlotRange<ConfigurableFluidStack> fluid(int slot)
		{
			return new SlotRange<>(slot);
		}
		
		public SlotRange(int slot)
		{
			this(slot, slot);
		}
		
		public List<T> slots(List<T> slots)
		{
			return slots.subList(start, end + 1);
		}
		
		public T slot(List<T> slots)
		{
			return this.slots(slots).get(0);
		}
	}
	
	public record Params(
			SlotRange<ConfigurableItemStack> blazePowder,
			SlotRange<ConfigurableItemStack> bottle,
			SlotRange<ConfigurableItemStack> reagent,
			// TODO replace with inventory.getItemOutputs()
			SlotRange<ConfigurableItemStack> output,
			SlotRange<ConfigurableFluidStack> blazingEssence,
			SlotRange<ConfigurableFluidStack> water
	)
	{
	}
}
