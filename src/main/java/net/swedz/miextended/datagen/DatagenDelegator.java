package net.swedz.miextended.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.datagen.client.DatagenDelegatorClient;
import net.swedz.miextended.datagen.server.DatagenDelegatorServer;

public final class DatagenDelegator
{
	@SubscribeEvent
	public void gatherData(GatherDataEvent event)
	{
		DatagenDelegatorClient.configure(event);
		DatagenDelegatorServer.configure(event);
	}
}
