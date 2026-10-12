package net.swedz.extended_industrialization.item.machineconfig.event;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class MachineConfigCardApplyEvent extends PlayerEvent implements ICancellableEvent
{
	private final MachineBlockEntity targetMachine;
	
	private final Simulation simulation;
	
	public MachineConfigCardApplyEvent(Player player, MachineBlockEntity targetMachine, Simulation simulation)
	{
		super(player);
		this.targetMachine = targetMachine;
		this.simulation = simulation;
	}
	
	public MachineBlockEntity getTargetMachine()
	{
		return targetMachine;
	}
	
	public boolean isSimulating()
	{
		return simulation.isSimulating();
	}
	
	public boolean isActing()
	{
		return simulation.isActing();
	}
}
