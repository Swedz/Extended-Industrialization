package net.swedz.intothetwilight.datagen.client.provider;

import net.minecraft.data.DataGenerator;
import net.swedz.intothetwilight.ITTText;
import net.swedz.intothetwilight.IntoTheTwilight;
import net.swedz.intothetwilight.datagen.api.DatagenProvider;
import net.swedz.intothetwilight.datagen.api.object.DatagenLanguageWrapper;

public final class ClientDatagenProvider extends DatagenProvider
{
	public ClientDatagenProvider(DataGenerator generator)
	{
		super(generator, "Into the Twilight Datagen/Client", IntoTheTwilight.ID);
	}
	
	@Override
	public void run()
	{
		final DatagenLanguageWrapper lang = new DatagenLanguageWrapper(this);
		for(ITTText text : ITTText.values())
		{
			lang.add(text.getTranslationKey(), text.englishText());
		}
		lang.write();
	}
}
