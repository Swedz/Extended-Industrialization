package net.swedz.extended_industrialization.api.capabilities;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.function.Consumer;

public final class CapabilitiesListeners
{
	private static final List<Consumer<RegisterCapabilitiesEvent>> listeners = Lists.newArrayList();
	
	public static void triggerAll(RegisterCapabilitiesEvent event)
	{
		listeners.forEach((action) -> action.accept(event));
	}
	
	public static void register(Consumer<RegisterCapabilitiesEvent> listener)
	{
		listeners.add(listener);
	}
}
