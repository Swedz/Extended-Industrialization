package net.swedz.extended_industrialization.machines.component.tesla.network;

import net.swedz.extended_industrialization.machines.component.tesla.network.receiver.TeslaReceiver;

import java.util.Collection;

/**
 * Injected into {@link net.minecraft.world.entity.player.Player}
 */
public interface TeslaReceiverHolder
{
	Collection<TeslaReceiver> getTeslaReceivers();
}
