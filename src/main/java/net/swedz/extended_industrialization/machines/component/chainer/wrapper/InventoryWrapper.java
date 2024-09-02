package net.swedz.extended_industrialization.machines.component.chainer.wrapper;

public class InventoryWrapper<H>
{
	protected final H handler;
	
	public InventoryWrapper(H handler)
	{
		this.handler = handler;
	}
	
	public H handler()
	{
		return handler;
	}
}
