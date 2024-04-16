package net.swedz.intothetwilight.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.intothetwilight.datagen.models.ITTModernIndustrializationModelProvider;
import net.swedz.intothetwilight.datagen.lang.ITTModernIndustrializationLanguageProvider;

public final class DatagenListener
{
	@SubscribeEvent
	public void gatherData(GatherDataEvent event)
	{
		PackOutput output = event.getGenerator().getPackOutput();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		
		event.getGenerator().addProvider(event.includeClient(), new ITTModernIndustrializationLanguageProvider(output));
		event.getGenerator().addProvider(event.includeClient(), new ITTModernIndustrializationModelProvider(output, existingFileHelper));
	}
}
