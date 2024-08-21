package net.swedz.extended_industrialization.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.swedz.extended_industrialization.EIDataMaps;

import java.util.Map;

public record LargeElectricFurnaceTier(int batchSize, float euCostMultiplier)
{
	public static final Codec<LargeElectricFurnaceTier> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					ExtraCodecs.POSITIVE_INT.fieldOf("batch_size").forGetter(LargeElectricFurnaceTier::batchSize),
					ExtraCodecs.POSITIVE_FLOAT.fieldOf("eu_cost_multiplier").forGetter(LargeElectricFurnaceTier::euCostMultiplier)
			)
			.apply(instance, LargeElectricFurnaceTier::new)
	);
	
	@SuppressWarnings("deprecation")
	public static LargeElectricFurnaceTier getFor(Block block)
	{
		return block.builtInRegistryHolder().getData(EIDataMaps.LARGE_ELECTRIC_FURNACE_TIER);
	}
	
	public static Map<ResourceKey<Block>, LargeElectricFurnaceTier> getAll()
	{
		return BuiltInRegistries.BLOCK.getDataMap(EIDataMaps.LARGE_ELECTRIC_FURNACE_TIER);
	}
}
