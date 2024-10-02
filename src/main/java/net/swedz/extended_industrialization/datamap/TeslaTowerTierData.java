package net.swedz.extended_industrialization.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.swedz.extended_industrialization.EIDataMaps;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower.TeslaTowerTier;
import net.swedz.extended_industrialization.machines.tieredshapes.DataMultiblockTier;

import java.util.Map;

public record TeslaTowerTierData(long maxTransfer, int maxDistance, float maxLoss) implements DataMultiblockTier<TeslaTowerTier>
{
	public static final Codec<TeslaTowerTierData> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.LONG.fieldOf("transfer").forGetter(TeslaTowerTierData::maxTransfer),
					Codec.INT.fieldOf("distance").forGetter(TeslaTowerTierData::maxDistance),
					Codec.FLOAT.fieldOf("loss").forGetter(TeslaTowerTierData::maxLoss)
			)
			.apply(instance, TeslaTowerTierData::new));
	
	@Override
	public TeslaTowerTier wrap(ResourceKey<Block> key)
	{
		return new TeslaTowerTier(key.location(), maxTransfer, maxDistance, maxLoss);
	}
	
	public static TeslaTowerTierData getFor(Block block)
	{
		return BuiltInRegistries.BLOCK.getData(EIDataMaps.TESLA_TOWER_TIER, BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
	}
	
	public static Map<ResourceKey<Block>, TeslaTowerTierData> getAll()
	{
		return BuiltInRegistries.BLOCK.getDataMap(EIDataMaps.TESLA_TOWER_TIER);
	}
}
