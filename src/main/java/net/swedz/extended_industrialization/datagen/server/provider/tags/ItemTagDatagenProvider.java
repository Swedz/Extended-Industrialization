package net.swedz.extended_industrialization.datagen.server.provider.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EITags;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public final class ItemTagDatagenProvider extends ItemTagsProvider
{
	public ItemTagDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider(), CompletableFuture.completedFuture(TagLookup.empty()), EI.ID, event.getExistingFileHelper());
	}
	
	private void addFarmerVoidableDropTag()
	{
		this.tag(EITags.FARMER_VOIDABLE)
				.add(
						Items.STICK,
						Items.APPLE,
						Items.MOSS_CARPET,
						Items.MANGROVE_ROOTS
				)
				.addTags(
						ItemTags.SAPLINGS
				);
	}
	
	private void addFarmerStandardPlantableTag()
	{
		this.tag(EITags.FARMER_PLANTABLE)
				.addTags(
						Tags.Items.SEEDS,
						Tags.Items.CROPS,
						ItemTags.SAPLINGS
				)
				.remove(
						Items.BEETROOT,
						Items.WHEAT
				);
	}
	
	@Override
	protected void addTags(HolderLookup.Provider provider)
	{
		for(ItemHolder<?> item : EIItems.values().stream().sorted(Comparator.comparing((item) -> item.identifier().id())).toList())
		{
			for(TagKey<Item> tag : item.tags())
			{
				this.tag(tag).add(item.asItem());
			}
		}
		
		this.addFarmerVoidableDropTag();
		this.addFarmerStandardPlantableTag();
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
