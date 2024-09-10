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
		return wrappers.stream()
				.filter((wrapper) -> wrapper.contains(globalSlot))
				.findFirst();
	}
}
