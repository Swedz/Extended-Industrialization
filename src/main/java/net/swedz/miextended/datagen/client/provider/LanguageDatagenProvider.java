package net.swedz.miextended.datagen.client.provider;

import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.registry.blocks.BlockHolder;
import net.swedz.miextended.registry.blocks.MIEBlocks;
import net.swedz.miextended.registry.items.ItemHolder;
import net.swedz.miextended.registry.items.MIEItems;
import net.swedz.miextended.text.MIEText;

public final class LanguageDatagenProvider extends LanguageProvider
{
	public LanguageDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), MIExtended.ID, "en_us");
	}
	
	@Override
	protected void addTranslations()
	{
		for(MIEText text : MIEText.values())
		{
			this.add(text.getTranslationKey(), text.englishText());
		}
		
		for(ItemHolder item : MIEItems.values())
		{
			this.add(item.asItem(), item.identifier().englishName());
		}
		
		for(BlockHolder block : MIEBlocks.values())
		{
			this.add(block.get(), block.identifier().englishName());
		}
		
		this.add("itemGroup.%s.%s".formatted(MIExtended.ID, MIExtended.ID), "MI Extended");
	}
}
