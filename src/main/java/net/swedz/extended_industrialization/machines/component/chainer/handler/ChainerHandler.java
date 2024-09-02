package net.swedz.extended_industrialization.machines.component.chainer.handler;

import net.swedz.extended_industrialization.machines.component.chainer.ClearableInvalidatable;
import net.swedz.extended_industrialization.machines.component.chainer.MachineLinks;
import net.swedz.extended_industrialization.machines.component.chainer.wrapper.InventoryWrapper;

import java.util.List;

public abstract class ChainerHandler<H, W extends InventoryWrapper<H>> implements ClearableInvalidatable
{
	protected final MachineLinks machineLinks;
	
	protected List<W> wrappers = List.of();
	
	protected int slots;
	
	public ChainerHandler(MachineLinks machineLinks)
	{
		this.machineLinks = machineLinks;
	}
	
	public MachineLinks getMachineLinks()
	{
		return machineLinks;
	}
	
	@Override
	public void clear()
	{
		wrappers = List.of();
		slots = 0;
	}
}
