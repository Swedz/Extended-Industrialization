package net.swedz.extended_industrialization.datagen.client.provider.models;

import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

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
			if(item.hasModelProvider())
			{
				item.modelProvider().accept(this);
			}
		}
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
