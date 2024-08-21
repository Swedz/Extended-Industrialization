package net.swedz.extended_industrialization.item.machineconfig;

import aztech.modern_industrialization.util.Simulation;
import net.minecraft.world.entity.player.Player;

public interface MachineConfigApplicable<T>
{
	boolean matches(T target);
	
	boolean apply(Player player, T target, Simulation simulation);
}
