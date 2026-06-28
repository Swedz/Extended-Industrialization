package net.swedz.extended_industrialization.machines.component.chainer.handler;

import aztech.modern_industrialization.inventory.WhitelistedItemStorage;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.bridge.SlotItemHandler;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerLinks;
import net.swedz.extended_industrialization.machines.component.chainer.wrapper.SlotInventoryWrapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class ChainerItemHandler extends SlotChainerHandler<IItemHandler> implements IItemHandler, WhitelistedItemStorage
{
	private boolean   currentlyWhitelisted = false;
	private Set<Item> whitelistedItems     = Set.of();
	
	public ChainerItemHandler(ChainerLinks chainerLinks)
	{
		super(chainerLinks);
	}
	
	@Override
	public void invalidate()
	{
		List<SlotInventoryWrapper<IItemHandler>> wrappers = Lists.newArrayList();
		int slots = 0;
		boolean currentlyWhitelisted = true;
		Set<Item> whitelistedItems = Sets.newHashSet();
		
		for(var handler : this.getMachineLinks().itemHandlers())
		{
			int handlerSlots = handler.getSlots();
			wrappers.add(new SlotInventoryWrapper<>(handler, slots, handlerSlots));
			slots += handlerSlots;
			
			if(currentlyWhitelisted)
			{
				if(handler instanceof WhitelistedItemStorage whitelisted &&
				   whitelisted.currentlyWhitelisted())
				{
					whitelisted.getWhitelistedItems(whitelistedItems);
				}
				else if(handler instanceof SlotItemHandler slotItemHandler &&
						!slotItemHandler.storage().isResourceBlank())
				{
					whitelistedItems.add(slotItemHandler.storage().getResource().getItem());
				}
				else
				{
					currentlyWhitelisted = false;
					whitelistedItems = Sets.newHashSet();
				}
			}
		}
		
		this.wrappers = Collections.unmodifiableList(wrappers);
		this.wrappersSlotMap = Maps.newConcurrentMap();
		this.slots = slots;
		this.currentlyWhitelisted = currentlyWhitelisted;
		this.whitelistedItems = Collections.unmodifiableSet(whitelistedItems);
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
	
	@Override
	public boolean currentlyWhitelisted()
	{
		return currentlyWhitelisted;
	}
	
	@Override
	public void getWhitelistedItems(Set<Item> items)
	{
		items.addAll(whitelistedItems);
	}
}
