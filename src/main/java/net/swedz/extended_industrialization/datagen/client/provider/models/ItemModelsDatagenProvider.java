package net.swedz.extended_industrialization.datagen.client.provider.models;

import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.registry.items.EIItems;
import net.swedz.extended_industrialization.registry.items.ItemHolder;

public final class ItemModelsDatagenProvider extends ItemModelProvider
{
	public ItemModelsDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), EI.ID, event.getExistingFileHelper());
	}
	
	@Override
	protected void registerModels()
	{
		for(ItemHolder item : EIItems.values())
		{
			ItemModelBuilder itemModelBuilder = this.getBuilder("item/%s".formatted(item.identifier().id()));
			item.modelBuilder().accept(itemModelBuilder);
		}
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
