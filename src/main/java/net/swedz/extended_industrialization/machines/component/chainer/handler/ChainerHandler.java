package net.swedz.extended_industrialization.machines.component.chainer.handler;

import net.swedz.extended_industrialization.machines.component.chainer.ChainerElement;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerLinks;
import net.swedz.extended_industrialization.machines.component.chainer.wrapper.InventoryWrapper;

import java.util.List;

public abstract class ChainerHandler<H, W extends InventoryWrapper<H>> implements ChainerElement
{
	protected final ChainerLinks chainerLinks;
	
	protected List<W> wrappers = List.of();
	
	protected int slots;
	
	public ChainerHandler(ChainerLinks chainerLinks)
	{
		this.chainerLinks = chainerLinks;
	}
	
	public ChainerLinks getMachineLinks()
	{
		return chainerLinks;
	}
	
	@Override
	public void clear()
	{
		wrappers = List.of();
		slots = 0;
	}
}
