package net.swedz.extended_industrialization.registry.api;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.level.storage.loot.LootTable;
import net.swedz.extended_industrialization.registry.blocks.BlockHolder;

import java.util.function.Function;

public final class CommonLootTableBuilders
{
	public static Function<BlockLootSubProvider, LootTable.Builder> self(BlockHolder block)
	{
		return (provider) -> provider.createSingleItemTable(block.get().asItem());
	}
}
