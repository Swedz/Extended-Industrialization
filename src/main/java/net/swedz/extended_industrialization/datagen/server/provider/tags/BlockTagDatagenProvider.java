package net.swedz.extended_industrialization.datagen.server.provider.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIBlocks;
import net.swedz.extended_industrialization.EITags;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;

import java.util.Comparator;

public final class BlockTagDatagenProvider extends BlockTagsProvider
{
	public BlockTagDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider(), EI.ID, event.getExistingFileHelper());
	}
	
	private void addFarmerDirtTags()
	{
		this.tag(EITags.FARMER_DIRT)
				.add(
						Blocks.SOUL_SAND
				)
				.addTags(
						BlockTags.DIRT,
						Tags.Blocks.SANDS,
						Tags.Blocks.VILLAGER_FARMLANDS
				);
	}
	
	@Override
	protected void addTags(HolderLookup.Provider provider)
	{
		for(BlockHolder<?> block : EIBlocks.values().stream().sorted(Comparator.comparing((block) -> block.identifier().id())).toList())
		{
			for(TagKey<Block> tag : block.tags())
			{
				this.tag(tag).add(block.get());
			}
		}
		
		this.addFarmerDirtTags();
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
