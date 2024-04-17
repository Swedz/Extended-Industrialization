package net.swedz.miextended.datagen.server;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.datagen.server.provider.MIHookServerDatagenProvider;

public final class DatagenDelegatorServer
{
	public static void configure(GatherDataEvent event)
	{
		event.getGenerator().addProvider(event.includeServer(), new MIHookServerDatagenProvider(event.getGenerator()));
	}
}
