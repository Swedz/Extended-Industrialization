package net.swedz.extended_industrialization.machines.component.chainer;

import java.util.List;
import java.util.Optional;

public abstract class ChainerHandler<I> implements ClearableInvalidatable
{
	protected final MachineLinks machineLinks;
	
	protected List<InventoryHandler<I>> handlers = List.of();
	
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
		handlers = List.of();
		slots = 0;
	}
	
	protected Optional<InventoryHandler<I>> getHandler(int globalSlot)
	{
		for(InventoryHandler<I> handler : handlers)
		{
			if(handler.contains(globalSlot))
			{
				return Optional.of(handler);
			}
		}
		return Optional.empty();
	}
}
