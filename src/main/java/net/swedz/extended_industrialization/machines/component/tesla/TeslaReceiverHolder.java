package net.swedz.extended_industrialization.machines.component.tesla;

import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiver;

import java.util.Collection;

/**
 * Injected into {@link net.minecraft.world.entity.player.Player}
 */
public interface TeslaReceiverHolder
{
	Collection<TeslaReceiver> getTeslaReceivers();
}
