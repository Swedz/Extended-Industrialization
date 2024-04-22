package net.swedz.miextended.datagen.client;

import aztech.modern_industrialization.datagen.texture.MISpriteSourceProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.datagen.client.provider.LanguageDatagenProvider;
import net.swedz.miextended.datagen.client.provider.mi.ItemModelsMIHookDatagenProvider;
import net.swedz.miextended.datagen.client.provider.mi.LanguageMIHookDatagenProvider;
import net.swedz.miextended.datagen.client.provider.mi.MachineCasingModelsMIHookDatagenProvider;
import net.swedz.miextended.datagen.client.provider.mi.MachineModelsMIHookDatagenProvider;
import net.swedz.miextended.datagen.client.provider.mi.TexturesMIHookDatagenProvider;
import net.swedz.miextended.datagen.client.provider.models.ItemModelsDatagenProvider;

public final class DatagenDelegatorClient
{
	public static void configure(GatherDataEvent event)
	{
		event.getGenerator().addProvider(event.includeClient(), new MISpriteSourceProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
		event.getGenerator().addProvider(event.includeClient(), new TexturesMIHookDatagenProvider(event));
		event.getGenerator().addProvider(event.includeClient(), new ItemModelsMIHookDatagenProvider(event));
		event.getGenerator().addProvider(event.includeClient(), new LanguageMIHookDatagenProvider(event));
		event.getGenerator().addProvider(event.includeClient(), new MachineCasingModelsMIHookDatagenProvider(event));
		event.getGenerator().addProvider(event.includeClient(), new MachineModelsMIHookDatagenProvider(event));
		
		event.getGenerator().addProvider(event.includeClient(), new ItemModelsDatagenProvider(event));
		event.getGenerator().addProvider(event.includeClient(), new LanguageDatagenProvider(event));
	}
}
