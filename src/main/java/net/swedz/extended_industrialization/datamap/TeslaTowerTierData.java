package net.swedz.extended_industrialization.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower.TeslaTowerTier;
import net.swedz.extended_industrialization.machines.tieredshapes.DataMultiblockTier;

public record TeslaTowerTierData(
		long maxTransfer, int maxDistance, long drain
) implements DataMultiblockTier<TeslaTowerTier>
{
	public static final Codec<TeslaTowerTierData> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.LONG.fieldOf("transfer").forGetter(TeslaTowerTierData::maxTransfer),
					Codec.INT.fieldOf("distance").forGetter(TeslaTowerTierData::maxDistance),
					Codec.LONG.fieldOf("drain").forGetter(TeslaTowerTierData::drain)
			)
			.apply(instance, TeslaTowerTierData::new));
	
	@Override
	public TeslaTowerTier wrap(ResourceKey<Block> key)
	{
		return new TeslaTowerTier(key.location(), maxTransfer, maxDistance, drain);
	}
}
