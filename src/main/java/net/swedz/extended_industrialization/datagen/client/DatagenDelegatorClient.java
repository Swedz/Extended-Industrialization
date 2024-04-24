package net.swedz.extended_industrialization.datagen.client;

import aztech.modern_industrialization.datagen.texture.MISpriteSourceProvider;
import net.minecraft.data.DataProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.datagen.client.provider.LanguageDatagenProvider;
import net.swedz.extended_industrialization.datagen.client.provider.mi.LanguageMIHookDatagenProvider;
import net.swedz.extended_industrialization.datagen.client.provider.mi.MachineCasingModelsMIHookDatagenProvider;
import net.swedz.extended_industrialization.datagen.client.provider.mi.TexturesMIHookDatagenProvider;
import net.swedz.extended_industrialization.datagen.client.provider.models.BlockModelsDatagenProvider;
import net.swedz.extended_industrialization.datagen.client.provider.models.ItemModelsDatagenProvider;

import java.util.function.Function;

public final class DatagenDelegatorClient
{
	public static void configure(GatherDataEvent event)
	{
		add(event, (__) -> new MISpriteSourceProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
		
		add(event, TexturesMIHookDatagenProvider::new);
		add(event, LanguageMIHookDatagenProvider::new);
		add(event, MachineCasingModelsMIHookDatagenProvider::new);
		
		add(event, BlockModelsDatagenProvider::new);
		add(event, ItemModelsDatagenProvider::new);
		add(event, LanguageDatagenProvider::new);
	}
	
	private static void add(GatherDataEvent event, Function<GatherDataEvent, DataProvider> providerCreator)
	{
		event.getGenerator().addProvider(event.includeClient(), providerCreator.apply(event));
	}
}