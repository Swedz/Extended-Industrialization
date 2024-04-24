package net.swedz.extended_industrialization.datagen.client.provider;

import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.registry.fluids.FluidHolder;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;
import net.swedz.extended_industrialization.registry.items.ItemHolder;
import net.swedz.extended_industrialization.registry.items.EIItems;
import net.swedz.extended_industrialization.text.EIText;

public final class LanguageDatagenProvider extends LanguageProvider
{
	public LanguageDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), EI.ID, "en_us");
	}
	
	@Override
	protected void addTranslations()
	{
		for(EIText text : EIText.values())
		{
			this.add(text.getTranslationKey(), text.englishText());
		}
		
		for(ItemHolder item : EIItems.values())
		{
			this.add(item.asItem(), item.identifier().englishName());
		}
		
		for(FluidHolder fluid : EIFluids.values())
		{
			this.add(fluid.block().get(), fluid.identifier().englishName());
		}
		
		this.add("itemGroup.%s.%s".formatted(EI.ID, EI.ID), EI.NAME);
	}
}
