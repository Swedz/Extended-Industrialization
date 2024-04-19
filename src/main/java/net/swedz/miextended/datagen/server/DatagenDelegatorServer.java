package net.swedz.miextended.datagen.server;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.datagen.server.provider.recipes.AlloySmelterRecipesServerDatagenProvider;
import net.swedz.miextended.datagen.server.provider.recipes.BendingMachineRecipesServerDatagenProvider;
import net.swedz.miextended.datagen.server.provider.recipes.CanningMachineRecipesServerDatagenProvider;
import net.swedz.miextended.datagen.server.provider.recipes.VanillaCompatRecipesServerDatagenProvider;

public final class DatagenDelegatorServer
{
	public static void configure(GatherDataEvent event)
	{
		event.getGenerator().addProvider(event.includeServer(), new AlloySmelterRecipesServerDatagenProvider(event));
		event.getGenerator().addProvider(event.includeServer(), new BendingMachineRecipesServerDatagenProvider(event));
		event.getGenerator().addProvider(event.includeServer(), new CanningMachineRecipesServerDatagenProvider(event));
		event.getGenerator().addProvider(event.includeServer(), new VanillaCompatRecipesServerDatagenProvider(event));
	}
}
