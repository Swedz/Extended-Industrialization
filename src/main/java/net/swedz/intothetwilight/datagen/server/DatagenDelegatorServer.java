package net.swedz.intothetwilight.datagen.server;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.intothetwilight.datagen.server.provider.ITTMIServerDatagenProvider;

public final class DatagenDelegatorServer
{
	public static void configure(GatherDataEvent event)
	{
		event.getGenerator().addProvider(event.includeServer(), new ITTMIServerDatagenProvider(event.getGenerator()));
	}
}
