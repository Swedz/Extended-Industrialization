package net.swedz.miextended.datagen.server.provider.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.items.MIEItemWrapper;
import net.swedz.miextended.items.MIEItems;

import java.util.concurrent.CompletableFuture;

public final class ItemTagDatagenProvider extends ItemTagsProvider
{
	public ItemTagDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider(), CompletableFuture.completedFuture(TagLookup.empty()), MIExtended.ID, event.getExistingFileHelper());
	}
	
	@Override
	protected void addTags(HolderLookup.Provider provider)
	{
		for(MIEItemWrapper<?> item : MIEItems.all())
		{
			for(TagKey<Item> tag : item.getTags())
			{
				this.tag(tag).add(item.asItem());
			}
		}
	}
}
