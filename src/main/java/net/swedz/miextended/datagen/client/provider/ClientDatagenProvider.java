package net.swedz.miextended.datagen.client.provider;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.datagen.api.DatagenOutputTarget;
import net.swedz.miextended.datagen.api.DatagenProvider;
import net.swedz.miextended.datagen.api.object.DatagenLanguageWrapper;
import net.swedz.miextended.items.MIEItemWrapper;
import net.swedz.miextended.items.MIEItems;
import net.swedz.miextended.text.MIEText;

public final class ClientDatagenProvider extends DatagenProvider
{
	public ClientDatagenProvider(GatherDataEvent event)
	{
		super(event, "MI Extended Datagen/Client", MIExtended.ID);
	}
	
	@Override
	public void run()
	{
		final DatagenLanguageWrapper lang = new DatagenLanguageWrapper(this);
		
		for(MIEText text : MIEText.values())
		{
			lang.add(text.getTranslationKey(), text.englishText());
		}
		
		for(MIEItemWrapper item : MIEItems.all())
		{
			lang.add("item.%s.%s".formatted(item.modId(), item.id(false)), item.identifiable().englishName());
			
			if(item.modelBuilder() != null)
			{
				ResourceLocation output = new ResourceLocation(item.modId(), "models/item/%s".formatted(item.id(false)));
				ItemModelBuilder itemModelBuilder = new ItemModelBuilder(output, existingFileHelper);
				item.modelBuilder().accept(itemModelBuilder);
				this.writeJsonIfNotExist(
						DatagenOutputTarget.RESOURCE_PACK,
						(p) -> p.resolve("models").resolve("item").resolve(item.id(false) + ".json"),
						itemModelBuilder.toJson()
				);
			}
		}
		
		lang.write();
	}
}
