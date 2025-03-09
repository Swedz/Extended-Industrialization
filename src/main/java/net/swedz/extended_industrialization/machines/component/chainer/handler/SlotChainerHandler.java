package net.swedz.extended_industrialization.machines.component.chainer.handler;

import net.swedz.extended_industrialization.machines.component.chainer.ChainerLinks;
import net.swedz.extended_industrialization.machines.component.chainer.wrapper.SlotInventoryWrapper;

public abstract class SlotChainerHandler<H> extends ChainerHandler<H, SlotInventoryWrapper<H>>
{
	public SlotChainerHandler(ChainerLinks chainerLinks)
	{
		super(chainerLinks);
	}
	
	protected final SlotInventoryWrapper<H> getWrapper(int globalSlot)
	{
		var cached = wrappersSlotMap.get(globalSlot);
		if(cached != null)
		{
			return cached;
		}
		for(var wrapper : wrappers)
		{
			if(wrapper.contains(globalSlot))
			{
				wrappersSlotMap.put(globalSlot, wrapper);
				return wrapper;
			}
		}
		return null;
	}
}
