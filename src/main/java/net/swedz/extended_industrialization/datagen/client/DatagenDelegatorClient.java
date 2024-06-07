package net.swedz.extended_industrialization.datagen.client;

import aztech.modern_industrialization.datagen.texture.MISpriteSourceProvider;
import net.minecraft.data.DataProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.datagen.client.provider.LanguageDatagenProvider;
import net.swedz.extended_industrialization.datagen.client.provider.models.BlockModelsDatagenProvider;
import net.swedz.extended_industrialization.datagen.client.provider.models.ItemModelsDatagenProvider;
import net.swedz.tesseract.neoforge.datagen.client.mi.LanguageMIHookDatagenProvider;
import net.swedz.tesseract.neoforge.datagen.client.mi.MachineCasingModelsMIHookDatagenProvider;
import net.swedz.tesseract.neoforge.datagen.client.mi.TexturesMIHookDatagenProvider;

import java.util.function.Function;

public final class DatagenDelegatorClient
{
	public static void configure(GatherDataEvent event)
	{
		add(event, (__) -> new MISpriteSourceProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
		
		add(event, (e) -> new TexturesMIHookDatagenProvider(e, EI.ID, EIFluids.values()));
		add(event, (e) -> new LanguageMIHookDatagenProvider(e, EI.ID));
		add(event, (e) -> new MachineCasingModelsMIHookDatagenProvider(e, EI.ID));
		
		add(event, BlockModelsDatagenProvider::new);
		add(event, ItemModelsDatagenProvider::new);
		add(event, LanguageDatagenProvider::new);
	}
	
	private static void add(GatherDataEvent event, Function<GatherDataEvent, DataProvider> providerCreator)
	{
		event.getGenerator().addProvider(event.includeClient(), providerCreator.apply(event));
	}
}
