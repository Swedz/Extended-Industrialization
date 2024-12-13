package net.swedz.extended_industrialization.datagen.server.provider.loottable;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.swedz.extended_industrialization.EIBlocks;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;

import java.util.Set;

public final class BlockLootTableDatagenProvider extends BlockLootSubProvider
{
	public BlockLootTableDatagenProvider(HolderLookup.Provider registries)
	{
		super(Set.of(), FeatureFlags.VANILLA_SET, registries);
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
			if(block.hasLootTable())
			{
				this.add(block.get(), block.buildLootTable(this));
			}
		}
	}
}
