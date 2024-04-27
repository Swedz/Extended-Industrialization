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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.api.MachineInventoryHelper;
import net.swedz.extended_industrialization.machines.components.craft.ModularCrafterAccessBehavior;
import net.swedz.extended_industrialization.machines.components.craft.ModularCrafterAccess;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;

import java.util.List;
import java.util.Optional;

public final class PotionCrafterComponent implements IComponent.ServerOnly, ModularCrafterAccess
{
	private final Params                    params;
	private final MachineInventoryComponent    inventory;
	private final ModularCrafterAccessBehavior behavior;
	
	private PotionRecipe activeRecipe;
	
	private long usedEnergy;
	private long recipeMaxEu;
	
	private int efficiencyTicks;
	private int maxEfficiencyTicks;
	
	private long previousBaseEu = -1;
	private long previousMaxEu  = -1;
	
	public PotionCrafterComponent(Params params, MachineInventoryComponent inventory, ModularCrafterAccessBehavior behavior)
	{
		this.params = params;
		this.inventory = inventory;
		this.behavior = behavior;
	}
	
	@Override
	public ModularCrafterAccessBehavior getBehavior()
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
		return activeRecipe != null;
	}
	
	@Override
	public float getProgress()
	{
		return activeRecipe != null ? (float) usedEnergy / activeRecipe.totalEuCost() : 0;
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
		return activeRecipe.euCost();
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
			activeRecipe = null;
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
			if(this.takeItemInputs(recipe, flags, true) && this.takeFluidInputs(recipe, flags, true) && this.putItemOutputs(recipe, true))
			{
				// The flags should come out to be exactly the same... but just in case
				flags = new RollingRecipeFlags();
				
				this.takeItemInputs(recipe, flags, false);
				this.takeFluidInputs(recipe, flags, false);
				
				return Optional.of(recipe);
			}
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
	
	private List<StorageView<ItemVariant>> truncate(MIItemStorage storage)
	{
		List<StorageView<ItemVariant>> items = Lists.newArrayList(storage.iterator());
		items.removeIf((item) -> MachineInventoryHelper.isActuallyJustAir((ConfigurableItemStack) item));
		return items;
	}
	
	private boolean takeItemInputs(PotionRecipe recipe, RollingRecipeFlags flags, boolean simulate)
	{
		MIItemStorage bottleStorage = new MIItemStorage(params.bottle().slots(inventory.getItemInputs()));
		MIItemStorage reagentStorage = new MIItemStorage(params.reagent().slots(inventory.getItemInputs()));
		List<StorageView<ItemVariant>> truncatedReagentItems = this.truncate(reagentStorage);
		
		List<PotionRecipe> subchain = recipe.subchain(truncatedReagentItems);
		if(subchain.size() == 0)
		{
			return false;
		}
		
		try (Transaction transaction = Transaction.openOuter())
		{
			boolean usedBottles = this.takeBottleItemInputs(recipe, flags, transaction, subchain);
			
			boolean usedReagents = this.takeReagentItemInputs(recipe, flags, transaction, reagentStorage, truncatedReagentItems);
			
			if(!simulate)
			{
				transaction.commit();
			}
			return usedBottles && usedReagents;
		}
	}
	
	private boolean takeBottleItemInputs(PotionRecipe recipe, RollingRecipeFlags flags, Transaction transaction,
										 List<PotionRecipe> subchain)
	{
		MIItemStorage bottleStorage = new MIItemStorage(params.bottle().slots(inventory.getItemInputs()));
		
		PotionRecipe startRecipe = subchain.get(0);
		
		for(StorageView<ItemVariant> item : bottleStorage)
		{
			if(item.isResourceBlank())
			{
				continue;
			}
			ItemStack itemStack = item.getResource().toStack();
			
			// Make sure this bottle can be used at the starting point of this recipe chain
			if(!ItemStack.isSameItemSameTags(this.transform(itemStack), startRecipe.input()))
			{
				continue;
			}
			
			// Check that we have enough of this bottle item
			try (Transaction nested = Transaction.openNested(transaction))
			{
				int count = 0;
				for(StorageView<ItemVariant> otherItem : bottleStorage)
				{
					ItemStack otherItemStack = otherItem.getResource().toStack();
					if(ItemStack.isSameItemSameTags(itemStack, otherItemStack))
					{
						long extracted = bottleStorage.extractAllSlot(otherItem.getResource(), recipe.bottles() - count, nested);
						count += extracted;
						if(count == recipe.bottles())
						{
							flags.needsWater = itemStack.is(Items.GLASS_BOTTLE);
							nested.commit();
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	private boolean takeReagentItemInputs(PotionRecipe recipe, RollingRecipeFlags flags, Transaction transaction,
										  MIItemStorage reagentStorage, List<StorageView<ItemVariant>> truncatedReagentItems)
	{
		try (Transaction nested = Transaction.openNested(transaction))
		{
			for(StorageView<ItemVariant> item : truncatedReagentItems)
			{
				long extracted = reagentStorage.extractAllSlot(item.getResource(), 1, nested);
				if(extracted != 1)
				{
					return false;
				}
			}
			
			nested.commit();
			
			return true;
		}
	}
	
	private boolean takeFluidInputs(PotionRecipe recipe, RollingRecipeFlags flags, boolean simulate)
	{
		boolean usedBlazingEssence = recipe.blazingEssence() == 0 || MachineInventoryHelper.consumeFluid(inventory.getFluidInputs(), EIFluids.BLAZING_ESSENCE, recipe.blazingEssence(), simulate) == recipe.blazingEssence();
		
		boolean usedWater = !flags.needsWater || recipe.water() == 0 || MachineInventoryHelper.consumeFluid(inventory.getFluidInputs(), Fluids.WATER, recipe.water(), simulate) == recipe.water();
		
		return usedBlazingEssence && usedWater;
	}
	
	private boolean putItemOutputs(PotionRecipe recipe, boolean simulate)
	{
		MIItemStorage outputStorage = new MIItemStorage(params.output().slots(inventory.getItemOutputs()));
		
		try (Transaction transaction = Transaction.openOuter())
		{
			long inserted = outputStorage.insertAllSlot(ItemVariant.of(recipe.output()), recipe.bottles(), transaction);
			if(inserted == recipe.bottles())
			{
				if(!simulate)
				{
					transaction.commit();
				}
				return true;
			}
		}
		
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
			
			// Make sure we recalculate the max efficiency ticks if the recipe changes or if
			// the efficiency has reached 0 (the latter is to recalculate the efficiency for
			// 0.3.6 worlds without having to break and replace the machines)
			if(activeRecipe == null || activeRecipe != recipe || efficiencyTicks == 0)
			{
				maxEfficiencyTicks = this.getRecipeMaxEfficiencyTicks(recipe);
			}
			// Start the actual recipe
			activeRecipe = recipe;
			usedEnergy = 0;
			recipeMaxEu = this.getRecipeMaxEu(activeRecipe, efficiencyTicks);
			
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
		if(activeRecipe != null && (usedEnergy > 0 || started) && enabled)
		{
			recipeMaxEu = this.getRecipeMaxEu(activeRecipe, efficiencyTicks);
			long amountToConsume = Math.min(recipeMaxEu, activeRecipe.totalEuCost() - usedEnergy);
			eu = behavior.consumeEu(amountToConsume, Simulation.ACT);
			active = eu > 0;
			usedEnergy += eu;
			
			if(usedEnergy == activeRecipe.totalEuCost())
			{
				this.putItemOutputs(activeRecipe, false);
				
				usedEnergy = 0;
				finished = true;
			}
		}
		
		if(activeRecipe != null && (previousBaseEu != behavior.getBaseRecipeEu() || previousMaxEu != behavior.getMaxRecipeEu()))
		{
			previousBaseEu = behavior.getBaseRecipeEu();
			previousMaxEu = behavior.getMaxRecipeEu();
			maxEfficiencyTicks = this.getRecipeMaxEfficiencyTicks(activeRecipe);
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
		if(activeRecipe != null)
		{
			tag.putString("activeRecipe", activeRecipe.id().toString());
		}
		tag.putInt("efficiencyTicks", efficiencyTicks);
		tag.putInt("maxEfficiencyTicks", maxEfficiencyTicks);
	}
	
	@Override
	public void readNbt(CompoundTag tag, boolean isUpgradingMachine)
	{
		usedEnergy = tag.getInt("usedEnergy");
		recipeMaxEu = tag.getInt("recipeMaxEu");
		activeRecipe = tag.contains("activeRecipe") ? PotionRecipe.getRecipe(new ResourceLocation(tag.getString("activeRecipe"))) : null;
		if(activeRecipe == null && usedEnergy > 0)
		{
			usedEnergy = 0;
			EI.LOGGER.error("Had to set the usedEnergy of PotionCrafterComponent to 0, but that should never happen!");
		}
		efficiencyTicks = tag.getInt("efficiencyTicks");
		maxEfficiencyTicks = tag.getInt("maxEfficiencyTicks");
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
			SlotRange<ConfigurableItemStack> output,
			SlotRange<ConfigurableFluidStack> blazingEssence,
			SlotRange<ConfigurableFluidStack> water
	)
	{
	}
}
