package net.swedz.extended_industrialization.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.datagen.client.DatagenDelegatorClient;
import net.swedz.extended_industrialization.datagen.server.DatagenDelegatorServer;

public final class DatagenDelegator
{
	@SubscribeEvent
	public void gatherData(GatherDataEvent event)
	{
		DatagenDelegatorClient.configure(event);
		DatagenDelegatorServer.configure(event);
	}
}
