package net.swedz.intothetwilight.datagen.client;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.intothetwilight.datagen.client.provider.ClientDatagenProvider;
import net.swedz.intothetwilight.datagen.client.provider.ITTMIClientDatagenProvider;

public final class DatagenDelegatorClient
{
	public static void configure(GatherDataEvent event)
	{
		event.getGenerator().addProvider(event.includeClient(), new ClientDatagenProvider(event.getGenerator()));
		event.getGenerator().addProvider(event.includeClient(), new ITTMIClientDatagenProvider(event.getGenerator()));
	}
}
