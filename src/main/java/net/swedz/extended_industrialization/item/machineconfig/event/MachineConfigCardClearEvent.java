package net.swedz.extended_industrialization.item.machineconfig.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class MachineConfigCardClearEvent extends PlayerEvent
{
	private final ItemStack item;
	
	public MachineConfigCardClearEvent(Player player, ItemStack item)
	{
		super(player);
		this.item = item;
	}
	
	public ItemStack getItem()
	{
		return item;
	}
}
