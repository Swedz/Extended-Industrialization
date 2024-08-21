package net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler.registry;

import net.neoforged.bus.api.Event;
import net.swedz.tesseract.neoforge.localizedlistener.LocalizedListener;

public record FarmerListener<E extends Event>(Class<E> eventClass, LocalizedListener<E> listener)
{
}
