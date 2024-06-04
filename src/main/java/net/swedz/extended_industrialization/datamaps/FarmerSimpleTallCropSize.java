package net.swedz.extended_industrialization.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.swedz.extended_industrialization.EIDataMaps;

public record FarmerSimpleTallCropSize(int maxHeight)
{
	public static final Codec<FarmerSimpleTallCropSize> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					ExtraCodecs.POSITIVE_INT.fieldOf("max_height").forGetter(FarmerSimpleTallCropSize::maxHeight)
			)
			.apply(instance, FarmerSimpleTallCropSize::new)
	);
	
	@SuppressWarnings("deprecation")
	public static FarmerSimpleTallCropSize getFor(Block block)
	{
		return block.builtInRegistryHolder().getData(EIDataMaps.FARMER_SIMPLE_TALL_CROP_SIZE);
	}
}
