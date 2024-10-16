package net.swedz.extended_industrialization.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;
import net.swedz.extended_industrialization.EIDataMaps;

public record FertilizerPotency(int tickRate, int mbToConsumePerFertilizerTick)
{
	public static final Codec<FertilizerPotency> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					ExtraCodecs.POSITIVE_INT.fieldOf("tick_rate").forGetter(FertilizerPotency::tickRate),
					ExtraCodecs.POSITIVE_INT.fieldOf("mb_to_consume_per_fertilizer_tick").forGetter(FertilizerPotency::mbToConsumePerFertilizerTick)
			)
			.apply(instance, FertilizerPotency::new)
	);
	
	@SuppressWarnings("deprecation")
	public static FertilizerPotency getFor(Fluid fluid)
	{
		return fluid.builtInRegistryHolder().getData(EIDataMaps.FERTILIZER_POTENCY);
	}
}
