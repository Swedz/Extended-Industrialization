package net.swedz.extended_industrialization.machines.tieredshapes;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public interface DataMultiblockTier<T extends MultiblockTier>
{
	T wrap(ResourceKey<Block> key);
}
