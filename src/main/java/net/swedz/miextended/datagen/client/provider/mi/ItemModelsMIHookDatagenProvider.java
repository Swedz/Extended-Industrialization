package net.swedz.miextended.datagen.client.provider.mi;

import aztech.modern_industrialization.MI;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.mi.hook.tracker.MIHookTracker;

import java.util.function.Consumer;

public final class ItemModelsMIHookDatagenProvider extends ItemModelProvider
{
	public ItemModelsMIHookDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), MI.ID, event.getExistingFileHelper());
	}
	
	@Override
	protected void registerModels()
	{
		for(Consumer<ItemModelProvider> action : MIHookTracker.ITEM_MODELS)
		{
			action.accept(this);
		}
	}
}
