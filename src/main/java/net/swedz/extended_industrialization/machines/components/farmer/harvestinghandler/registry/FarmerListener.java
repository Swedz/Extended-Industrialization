package net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.registry;

import net.neoforged.bus.api.Event;
import net.swedz.extended_industrialization.api.isolatedlistener.IsolatedListener;

public record FarmerListener<E extends Event>(Class<E> eventClass, IsolatedListener<E> listener)
{
}
