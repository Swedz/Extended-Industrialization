package net.swedz.miextended.datagen.client.provider;

import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.items.MIEItemWrapper;
import net.swedz.miextended.items.MIEItems;
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
		
		for(MIEItemWrapper item : MIEItems.all())
		{
			this.add(item.asItem(), item.identifiable().englishName());
		}
	}
}
