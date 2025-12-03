package net.swedz.extended_industrialization.machines.component.chainer.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerLinks;
import net.swedz.extended_industrialization.machines.component.chainer.wrapper.SlotInventoryWrapper;

import java.util.Collections;
import java.util.List;

public final class ChainerItemHandler extends SlotChainerHandler<IItemHandler> implements IItemHandler
{
	public ChainerItemHandler(ChainerLinks chainerLinks)
	{
		super(chainerLinks);
	}
	
	@Override
	public void invalidate()
	{
		List<SlotInventoryWrapper<IItemHandler>> wrappers = Lists.newArrayList();
		int slots = 0;
		
		for(var handler : this.getMachineLinks().itemHandlers())
		{
			int handlerSlots = handler.getSlots();
			wrappers.add(new SlotInventoryWrapper<>(handler, slots, handlerSlots));
			slots += handlerSlots;
		}
		
		this.wrappers = Collections.unmodifiableList(wrappers);
		this.wrappersSlotMap = Maps.newConcurrentMap();
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
		var wrapper = this.getWrapper(slot);
		return wrapper != null ? wrapper.handler().getStackInSlot(wrapper.toLocalSlot(slot)) : ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		if(!chainerLinks.doesAllowOperation())
		{
			return stack;
		}
		var wrapper = this.getWrapper(slot);
		return wrapper != null ? wrapper.handler().insertItem(wrapper.toLocalSlot(slot), stack, simulate) : stack;
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		if(!chainerLinks.doesAllowOperation())
		{
			return ItemStack.EMPTY;
		}
		var wrapper = this.getWrapper(slot);
		return wrapper != null ? wrapper.handler().extractItem(wrapper.toLocalSlot(slot), amount, simulate) : ItemStack.EMPTY;
	}
	
	@Override
	public int getSlotLimit(int slot)
	{
		var wrapper = this.getWrapper(slot);
		return wrapper != null ? wrapper.handler().getSlotLimit(wrapper.toLocalSlot(slot)) : 0;
	}
	
	@Override
	public boolean isItemValid(int slot, ItemStack stack)
	{
		var wrapper = this.getWrapper(slot);
		return wrapper != null && wrapper.handler().isItemValid(wrapper.toLocalSlot(slot), stack);
	}
}
