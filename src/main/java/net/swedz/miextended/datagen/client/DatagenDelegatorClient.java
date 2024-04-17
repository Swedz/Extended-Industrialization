package net.swedz.miextended.datagen.client;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.datagen.client.provider.ClientDatagenProvider;
import net.swedz.miextended.datagen.client.provider.MIMachineHookClientDatagenProvider;

public final class DatagenDelegatorClient
{
	public static void configure(GatherDataEvent event)
	{
		event.getGenerator().addProvider(event.includeClient(), new ClientDatagenProvider(event.getGenerator()));
		event.getGenerator().addProvider(event.includeClient(), new MIMachineHookClientDatagenProvider(event.getGenerator()));
	}
}
