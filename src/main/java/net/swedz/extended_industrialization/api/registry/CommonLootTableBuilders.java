package net.swedz.extended_industrialization.api.registry;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.level.storage.loot.LootTable;
import net.swedz.extended_industrialization.api.registry.holder.BlockHolder;

import java.util.function.Function;

public final class CommonLootTableBuilders
{
	public static Function<BlockLootSubProvider, LootTable.Builder> self(BlockHolder block)
	{
		return (provider) -> provider.createSingleItemTable(block.get().asItem());
	}
}
