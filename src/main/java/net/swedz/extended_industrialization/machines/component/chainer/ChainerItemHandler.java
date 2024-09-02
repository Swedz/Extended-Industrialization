package net.swedz.extended_industrialization.machines.component.chainer;

import com.google.common.collect.Lists;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Collections;
import java.util.List;

public final class ChainerItemHandler extends ChainerHandler<IItemHandler> implements IItemHandler
{
	public ChainerItemHandler(MachineLinks machineLinks)
	{
		super(machineLinks);
	}
	
	@Override
	public void invalidate()
	{
		List<InventoryHandler<IItemHandler>> handlers = Lists.newArrayList();
		int slots = 0;
		
		for(IItemHandler handler : this.getMachineLinks().itemHandlers())
		{
			int handlerSlots = handler.getSlots();
			handlers.add(new InventoryHandler<>(slots, handlerSlots, handler));
			slots += handlerSlots;
		}
		
		this.handlers = Collections.unmodifiableList(handlers);
		this.slots = slots;
	}
	
	@Override
	public int getSlots()
	{
		return slots;
	}
	
	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return this.getHandler(slot).map((h) -> h.handler().getStackInSlot(h.toLocalSlot(slot))).orElse(ItemStack.EMPTY);
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		return this.getHandler(slot).map((h) -> h.handler().insertItem(h.toLocalSlot(slot), stack, simulate)).orElse(stack);
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return this.getHandler(slot).map((h) -> h.handler().extractItem(h.toLocalSlot(slot), amount, simulate)).orElse(ItemStack.EMPTY);
	}
	
	@Override
	public int getSlotLimit(int slot)
	{
		return this.getHandler(slot).map((h) -> h.handler().getSlotLimit(h.toLocalSlot(slot))).orElse(0);
	}
	
	@Override
	public boolean isItemValid(int slot, ItemStack stack)
	{
		return this.getHandler(slot).map((h) -> h.handler().isItemValid(h.toLocalSlot(slot), stack)).orElse(false);
	}
}
