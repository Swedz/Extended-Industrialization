package net.swedz.extended_industrialization.machines.components.craft.potion;

import aztech.modern_industrialization.api.machine.component.InventoryAccess;
import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIItemStorage;
import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.components.MachineInventoryComponent;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.storage.StorageView;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.transaction.Transaction;
import aztech.modern_industrialization.util.Simulation;
import com.google.common.collect.Lists;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.api.MachineInventoryHelper;
import net.swedz.extended_industrialization.machines.components.craft.CrafterAccessBehavior;
import net.swedz.extended_industrialization.machines.components.craft.CrafterAccessWithBehavior;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class PotionCrafterComponent implements IComponent.ServerOnly, CrafterAccessWithBehavior
{
	private final Params                    params;
	private final MachineInventoryComponent inventory;
	private final CrafterAccessBehavior     behavior;
	
	private PotionRecipe            activeRecipe;
	private BrewingPickRecipeResult result;
	
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
		return result != null;
	}
	
	@Override
	public float getProgress()
	{
		return result != null ? (float) usedEnergy / result.recipe().totalEuCost() : 0;
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
		return result.recipe().euCost();
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
			result = null;
		}
	}
	
	private List<PotionRecipe> getRecipes()
	{
		if(efficiencyTicks > 0)
		{
			return List.of(activeRecipe);
		}
		else
		{
			return PotionRecipe.getRecipes();
		}
	}
	
	private static final class RollingRecipeFlags
	{
		private boolean needsWater;
	}
	
	private Optional<PotionRecipe> tryStartRecipe()
	{
		for(PotionRecipe recipe : this.getRecipes())
		{
			RollingRecipeFlags flags = new RollingRecipeFlags();
			if(this.takeItemInputs(recipe, flags, true) && this.takeFluidInputs(recipe, flags, true))
			{
				EI.LOGGER.info("found recipe: {}", BuiltInRegistries.POTION.getKey(recipe.potion()));
			}
			/*if(this.takeItemInputs(recipe, true) && this.takeFluidInputs(recipe, true) && this.putItemOutputs(recipe, true))
			{
				this.takeItemInputs(recipe, false);
				this.takeFluidInputs(recipe, false);
				return Optional.of(recipe);
			}*/
		}
		return Optional.empty();
	}
	
	private ItemStack transform(ItemStack stack)
	{
		if(stack.is(Items.GLASS_BOTTLE))
		{
			return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
		}
		return stack;
	}
	
	private List<StorageView<ItemVariant>> truncatedReagentList(MIItemStorage storage)
	{
		List<StorageView<ItemVariant>> storageList = Lists.newArrayList(storage.iterator());
		storageList.removeIf((item) -> MachineInventoryHelper.isActuallyJustAir((ConfigurableItemStack) item));
		return storageList;
	}
	
	private boolean takeItemInputs(PotionRecipe recipe, RollingRecipeFlags flags, boolean simulate)
	{
		MIItemStorage bottleStorage = new MIItemStorage(params.bottle().slots(inventory.getItemInputs()));
		MIItemStorage reagentStorage = new MIItemStorage(params.reagent().slots(inventory.getItemInputs()));
		
		List<PotionRecipe> subchain = recipe.subchain(reagentStorage);
		if(subchain.size() == 0)
		{
			return false;
		}
		EI.LOGGER.info("found recipe: {} with chain size of {}", BuiltInRegistries.POTION.getKey(recipe.potion()), subchain.size());
		
		try (Transaction transaction = Transaction.openOuter())
		{
			if(!simulate)
			{
				transaction.commit();
			}
			return false;
		}
	}
	
	private boolean takeFluidInputs(PotionRecipe recipe, RollingRecipeFlags flags, boolean simulate)
	{
		boolean usedBlazingEssence = recipe.blazingEssence() == 0 || MachineInventoryHelper.consumeFluid(inventory.getFluidInputs(), EIFluids.BLAZING_ESSENCE, recipe.blazingEssence(), simulate) == recipe.blazingEssence();
		
		boolean usedWater = !flags.needsWater || recipe.water() == 0 || MachineInventoryHelper.consumeFluid(inventory.getFluidInputs(), Fluids.WATER, recipe.water(), simulate) == recipe.water();
		
		return usedBlazingEssence && usedWater;
	}
	
	private boolean putItemOutputs(PotionRecipe recipe, RollingRecipeFlags flags, boolean simulate)
	{
		return false;
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
		Optional<PotionRecipe> found = this.tryStartRecipe();
		if(found.isPresent())
		{
			PotionRecipe recipe = found.get();
			
			EI.LOGGER.info("SUCCESS! Found recipe: {}", BuiltInRegistries.POTION.getKey(PotionUtils.getPotion(recipe.output())));
			
			// Make sure we recalculate the max efficiency ticks if the recipe changes or if
			// the efficiency has reached 0 (the latter is to recalculate the efficiency for
			// 0.3.6 worlds without having to break and replace the machines)
			/*if(result == null || result.recipe() != recipe.recipe() || efficiencyTicks == 0)
			{
				maxEfficiencyTicks = this.getRecipeMaxEfficiencyTicks(recipe.recipe());
			}
			// Start the actual recipe
			result = recipe;
			usedEnergy = 0;
			recipeMaxEu = this.getRecipeMaxEu(recipe.recipe(), efficiencyTicks);*/
			
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
		if(result != null && (usedEnergy > 0 || started) && enabled)
		{
			recipeMaxEu = this.getRecipeMaxEu(result.recipe(), efficiencyTicks);
			long amountToConsume = Math.min(recipeMaxEu, result.recipe().totalEuCost() - usedEnergy);
			eu = behavior.consumeEu(amountToConsume, Simulation.ACT);
			active = eu > 0;
			usedEnergy += eu;
			
			if(usedEnergy == result.recipe().totalEuCost())
			{
				// TODO output better
				result.push();
				
				usedEnergy = 0;
				finished = true;
			}
		}
		
		if(result != null && (previousBaseEu != behavior.getBaseRecipeEu() || previousMaxEu != behavior.getMaxRecipeEu()))
		{
			previousBaseEu = behavior.getBaseRecipeEu();
			previousMaxEu = behavior.getMaxRecipeEu();
			maxEfficiencyTicks = this.getRecipeMaxEfficiencyTicks(result.recipe());
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
	
	private long getRecipeMaxEu(PotionRecipe recipe, int efficiencyTicks)
	{
		long baseEu = Math.max(behavior.getBaseRecipeEu(), recipe.euCost());
		return Math.min(recipe.totalEuCost(), Math.min((int) Math.floor(baseEu * CrafterComponent.getEfficiencyOverclock(efficiencyTicks)), behavior.getMaxRecipeEu()));
	}
	
	private int getRecipeMaxEfficiencyTicks(PotionRecipe recipe)
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
			PotionRecipe recipe,
			boolean consumeWater,
			Map<Integer, Integer> bottleSlotsToRemoveFrom,
			List<ConfigurableItemStack> reagentSlotsToRemoveFrom,
			ItemStack output,
			List<ConfigurableItemStack> outputSlotsToAddTo
	)
	{
		public void push()
		{
			// TODO use PotionCrafterComponent.inventory.getItemOutputs() so that each result isnt forced to go to its own slot if they're stackable
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
