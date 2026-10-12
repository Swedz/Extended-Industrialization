package net.swedz.extended_industrialization.item.machineconfig.event;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class MachineConfigCardSaveEvent extends PlayerEvent
{
	private final MachineBlockEntity targetMachine;
	
	private final ItemStack item;
	
	public MachineConfigCardSaveEvent(Player player, MachineBlockEntity targetMachine, ItemStack item)
	{
		super(player);
		this.targetMachine = targetMachine;
		this.item = item;
	}
	
	public MachineBlockEntity getTargetMachine()
	{
		return targetMachine;
	}
	
	public ItemStack getItem()
	{
		return item;
	}
}
