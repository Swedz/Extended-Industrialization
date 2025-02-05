package net.swedz.extended_industrialization.machines.component.chainer.handler;

import net.swedz.extended_industrialization.machines.component.chainer.ChainerLinks;
import net.swedz.extended_industrialization.machines.component.chainer.wrapper.SlotInventoryWrapper;

import java.util.Optional;

public abstract class SlotChainerHandler<H> extends ChainerHandler<H, SlotInventoryWrapper<H>>
{
	public SlotChainerHandler(ChainerLinks chainerLinks)
	{
		super(chainerLinks);
	}
	
	protected Optional<SlotInventoryWrapper<H>> getWrapper(int globalSlot)
	{
		for(var wrapper : wrappers)
		{
			if(wrapper.contains(globalSlot))
			{
				return Optional.of(wrapper);
			}
		}
		return Optional.empty();
	}
}
