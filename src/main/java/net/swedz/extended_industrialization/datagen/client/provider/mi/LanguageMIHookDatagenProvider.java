package net.swedz.extended_industrialization.datagen.client.provider.mi;

import aztech.modern_industrialization.MI;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.hook.mi.tracker.MIHookTracker;

import java.util.function.Consumer;

public final class LanguageMIHookDatagenProvider extends LanguageProvider
{
	public LanguageMIHookDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), MI.ID, "en_us");
	}
	
	@Override
	protected void addTranslations()
	{
		for(Consumer<LanguageProvider> action : MIHookTracker.LANGUAGE)
		{
			action.accept(this);
		}
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
