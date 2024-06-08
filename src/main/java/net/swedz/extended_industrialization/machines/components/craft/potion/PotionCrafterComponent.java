package net.swedz.extended_industrialization.machines.components.craft.potion;

import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIItemStorage;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.storage.StorageView;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.transaction.Transaction;
import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;
import net.swedz.tesseract.neoforge.compat.mi.component.craft.AbstractModularCrafterComponent;
import net.swedz.tesseract.neoforge.compat.mi.component.craft.ModularCrafterAccessBehavior;
import net.swedz.tesseract.neoforge.compat.mi.helper.MachineInventoryHelper;

import java.util.List;

public final class PotionCrafterComponent extends AbstractModularCrafterComponent<PotionRecipe>
{
	private final Params params;
	
	private int blazingEssence;
	
	private RollingRecipeFlags rollingRecipeFlags;
	
	public PotionCrafterComponent(
			Params params,
			MachineBlockEntity blockEntity,
			CrafterComponent.Inventory inventory,
			ModularCrafterAccessBehavior behavior
	)
	{
		super(blockEntity, inventory, behavior);
		this.params = params;
	}
	
	@Override
	public long getBaseRecipeEu()
	{
		return activeRecipe.euCost();
	}
	
	@Override
	protected long getRecipeEuCost(PotionRecipe recipe)
	{
		return recipe.euCost();
	}
	
	@Override
	protected long getRecipeTotalEuCost(PotionRecipe recipe)
	{
		return recipe.totalEuCost();
	}
	
	@Override
	protected boolean canContinueRecipe()
	{
		return true;
	}
	
	@Override
	protected ResourceLocation getRecipeId(PotionRecipe recipe)
	{
		return recipe.id();
	}
	
	@Override
	protected PotionRecipe getRecipeById(ResourceLocation resourceLocation)
	{
		return PotionRecipe.getRecipe(resourceLocation);
	}
	
	@Override
	protected boolean doConditionsMatchForRecipe(PotionRecipe recipe)
	{
		return true;
	}
	
	@Override
	protected void onTick()
	{
		this.doBlazeEssenceStuff();
	}
	
	private void doBlazeEssenceStuff()
	{
		ConfigurableItemStack slotPowder = params.blazePowder().slot(inventory.getItemInputs());
		if(slotPowder.getAmount() == 0)
		{
			return;
		}
		
		if(blazingEssence > 0)
		{
			return;
		}
		
		slotPowder.decrement(1);
		blazingEssence = 20;
	}
	
	@Override
	protected List<PotionRecipe> getRecipes()
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
	
	@Override
	protected boolean tryStartRecipe(PotionRecipe recipe)
	{
		rollingRecipeFlags = new RollingRecipeFlags();
		boolean success = super.tryStartRecipe(recipe);
		rollingRecipeFlags = null;
		return success;
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
	
	@Override
	protected boolean takeInputs(PotionRecipe recipe, boolean simulate)
	{
		return super.takeInputs(recipe, simulate) && this.takeBlazingEssenceInputs(recipe, simulate);
	}
	
	@Override
	protected boolean takeItemInputs(PotionRecipe recipe, boolean simulate)
	{
		MIItemStorage bottleStorage = new MIItemStorage(params.bottle().slots(inventory.getItemInputs()));
		MIItemStorage reagentStorage = new MIItemStorage(params.reagent().slots(inventory.getItemInputs()));
		List<StorageView<ItemVariant>> truncatedReagentItems = this.truncate(reagentStorage);
		
		List<PotionRecipe> subchain = recipe.subchain(truncatedReagentItems);
		if(subchain.isEmpty())
		{
			return false;
		}
		
		try (Transaction transaction = Transaction.openOuter())
		{
			boolean usedBottles = this.takeBottleItemInputs(recipe, transaction, subchain);
			
			boolean usedReagents = this.takeReagentItemInputs(recipe, transaction, reagentStorage, truncatedReagentItems);
			
			if(!simulate)
			{
				transaction.commit();
			}
			return usedBottles && usedReagents;
		}
	}
	
	private boolean takeBottleItemInputs(PotionRecipe recipe, Transaction transaction, List<PotionRecipe> subchain)
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
				long count = 0;
				for(StorageView<ItemVariant> otherItem : bottleStorage)
				{
					ItemStack otherItemStack = otherItem.getResource().toStack();
					if(ItemStack.isSameItemSameTags(itemStack, otherItemStack))
					{
						long extracted = bottleStorage.extractAllSlot(otherItem.getResource(), recipe.bottles() - count, nested);
						count += extracted;
						if(count == recipe.bottles())
						{
							rollingRecipeFlags.needsWater = itemStack.is(Items.GLASS_BOTTLE);
							nested.commit();
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	private boolean takeReagentItemInputs(PotionRecipe recipe, Transaction transaction,
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
	
	@Override
	protected boolean takeFluidInputs(PotionRecipe recipe, boolean simulate)
	{
		return !rollingRecipeFlags.needsWater || recipe.water() == 0 || MachineInventoryHelper.consumeFluid(inventory.getFluidInputs(), Fluids.WATER, recipe.water(), simulate) == recipe.water();
	}
	
	private boolean takeBlazingEssenceInputs(PotionRecipe recipe, boolean simulate)
	{
		if(blazingEssence < recipe.blazingEssence())
		{
			return false;
		}
		if(!simulate)
		{
			blazingEssence -= recipe.blazingEssence();
		}
		return true;
	}
	
	@Override
	protected boolean putItemOutputs(PotionRecipe recipe, boolean simulate, boolean toggleLock)
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
	
	@Override
	protected boolean putFluidOutputs(PotionRecipe recipe, boolean simulate, boolean toggleLock)
	{
		return true;
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
		super.writeNbt(tag);
		tag.putInt("blazingEssence", blazingEssence);
	}
	
	@Override
	public void readNbt(CompoundTag tag, boolean isUpgradingMachine)
	{
		super.readNbt(tag, isUpgradingMachine);
		blazingEssence = tag.getInt("blazingEssence");
	}
	
	@Override
	public void lockRecipe(ResourceLocation recipeId, Inventory inventory)
	{
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
			SlotRange<ConfigurableFluidStack> water
	)
	{
	}
}
