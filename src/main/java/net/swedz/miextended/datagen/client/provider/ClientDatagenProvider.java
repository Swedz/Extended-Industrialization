package net.swedz.miextended.datagen.client.provider;

import net.minecraft.data.DataGenerator;
import net.swedz.miextended.text.MIEText;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.datagen.api.DatagenProvider;
import net.swedz.miextended.datagen.api.object.DatagenLanguageWrapper;

public final class ClientDatagenProvider extends DatagenProvider
{
	public ClientDatagenProvider(DataGenerator generator)
	{
		super(generator, "MI Extended Datagen/Client", MIExtended.ID);
	}
	
	@Override
	public void run()
	{
		final DatagenLanguageWrapper lang = new DatagenLanguageWrapper(this);
		for(MIEText text : MIEText.values())
		{
			lang.add(text.getTranslationKey(), text.englishText());
		}
		lang.write();
	}
}
