package net.swedz.miextended.datagen.client.provider.models;

import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.items.MIEItemWrapper;
import net.swedz.miextended.items.MIEItems;

public final class ItemModelsDatagenProvider extends ItemModelProvider
{
	public ItemModelsDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), MIExtended.ID, event.getExistingFileHelper());
	}
	
	@Override
	protected void registerModels()
	{
		for(MIEItemWrapper item : MIEItems.all())
		{
			ItemModelBuilder itemModelBuilder = this.getBuilder("item/%s".formatted(item.identifiable().id()));
			item.modelBuilder().accept(itemModelBuilder);
		}
	}
}
