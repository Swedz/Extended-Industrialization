package net.swedz.intothetwilight.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.intothetwilight.datagen.client.DatagenDelegatorClient;
import net.swedz.intothetwilight.datagen.server.DatagenDelegatorServer;

public final class DatagenDelegator
{
	@SubscribeEvent
	public void gatherData(GatherDataEvent event)
	{
		DatagenDelegatorClient.configure(event);
		DatagenDelegatorServer.configure(event);
	}
}
