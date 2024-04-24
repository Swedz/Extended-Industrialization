package net.swedz.extended_industrialization.datagen.server;

import net.minecraft.data.DataProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.datagen.server.provider.datamaps.DataMapDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.recipes.AlloySmelterRecipesServerDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.recipes.BendingMachineRecipesServerDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.recipes.CanningMachineRecipesServerDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.recipes.CommonRecipesServerDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.recipes.ComposterRecipesServerDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.recipes.MachineItemRecipesServerDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.recipes.VanillaCompatRecipesServerDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.tags.ItemTagDatagenProvider;

import java.util.function.Function;

public final class DatagenDelegatorServer
{
	public static void configure(GatherDataEvent event)
	{
		add(event, DataMapDatagenProvider::new);
		
		add(event, AlloySmelterRecipesServerDatagenProvider::new);
		add(event, BendingMachineRecipesServerDatagenProvider::new);
		add(event, CanningMachineRecipesServerDatagenProvider::new);
		add(event, CommonRecipesServerDatagenProvider::new);
		add(event, ComposterRecipesServerDatagenProvider::new);
		add(event, MachineItemRecipesServerDatagenProvider::new);
		add(event, VanillaCompatRecipesServerDatagenProvider::new);
		
		add(event, ItemTagDatagenProvider::new);
	}
	
	private static void add(GatherDataEvent event, Function<GatherDataEvent, DataProvider> providerCreator)
	{
		event.getGenerator().addProvider(event.includeServer(), providerCreator.apply(event));
	}
}
