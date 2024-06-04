package net.swedz.extended_industrialization.datagen.server.provider.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.api.registry.holder.BlockHolder;
import net.swedz.extended_industrialization.EIBlocks;

import java.util.Comparator;

public final class BlockTagDatagenProvider extends BlockTagsProvider
{
	public BlockTagDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider(), EI.ID, event.getExistingFileHelper());
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
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
