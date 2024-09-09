package net.swedz.extended_industrialization.machines.component;

import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.util.TransferHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public final class BufferedItemInventoryComponent implements IComponent
{
	private final IItemHandler itemHandler;
	
	private BlockCapabilityCache<IItemHandler, Direction> outputCache;
	
	public BufferedItemInventoryComponent(IItemHandler parent)
	{
		this.itemHandler = this.buildItemHandler(parent);
	}
	
	private IItemHandler buildItemHandler(IItemHandler parent)
	{
		return new IItemHandler()
		{
			@Override
			public int getSlots()
			{
				return parent.getSlots();
			}
			
			@Override
			public ItemStack getStackInSlot(int slot)
			{
				return parent.getStackInSlot(slot);
			}
			
			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
			{
				return parent.insertItem(slot, stack, simulate);
			}
			
			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate)
			{
				ItemStack stack = this.getStackInSlot(slot);
				int stackCount = stack.getCount();
				int limit = this.getSlotLimit(slot);
				return stackCount == limit && stackCount <= amount ?
						parent.extractItem(slot, amount, simulate) :
						ItemStack.EMPTY;
			}
			
			@Override
			public int getSlotLimit(int slot)
			{
				return parent.getSlotLimit(slot);
			}
			
			@Override
			public boolean isItemValid(int slot, ItemStack stack)
			{
				return parent.isItemValid(slot, stack);
			}
		};
	}
	
	public IItemHandler itemHandler()
	{
		return itemHandler;
	}
	
	public void autoExtractItems(Level level, BlockPos pos, Direction direction)
	{
		boolean updateCache = outputCache == null || outputCache.context() != direction.getOpposite();
		
		if(updateCache)
		{
			outputCache = BlockCapabilityCache.create(
					Capabilities.ItemHandler.BLOCK,
					(ServerLevel) level, pos.relative(direction),
					direction.getOpposite()
			);
		}
		
		IItemHandler target = outputCache.getCapability();
		if(target != null)
		{
			TransferHelper.moveAll(itemHandler, target, true);
		}
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
	}
}
