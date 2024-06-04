package net.swedz.extended_industrialization.datagen.server.provider.loottable;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.swedz.extended_industrialization.api.registry.holder.BlockHolder;
import net.swedz.extended_industrialization.EIBlocks;

import java.util.Set;

public final class BlockLootTableDatagenProvider extends BlockLootSubProvider
{
	public BlockLootTableDatagenProvider()
	{
		super(Set.of(), FeatureFlags.VANILLA_SET);
	}
	
	@Override
	protected Iterable<Block> getKnownBlocks()
	{
		return EIBlocks.values().stream()
				.filter(BlockHolder::hasLootTable)
				.map(BlockHolder::get)
				.toList();
	}
	
	@Override
	protected void generate()
	{
		for(BlockHolder<?> block : EIBlocks.values())
		{
			if(!block.hasLootTable())
			{
				continue;
			}
			
			this.add(block.get(), block.getLootTableBuilder().apply(this));
		}
	}
}
