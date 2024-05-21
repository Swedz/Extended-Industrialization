package net.swedz.extended_industrialization.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;

public record PhotovoltaicCell(int euPerTick)
{
	public static final Codec<PhotovoltaicCell> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					ExtraCodecs.POSITIVE_INT.fieldOf("eu_per_tick").forGetter(PhotovoltaicCell::euPerTick)
			)
			.apply(instance, PhotovoltaicCell::new)
	);
	
	@SuppressWarnings("deprecation")
	public static PhotovoltaicCell getFor(Item item)
	{
		return item.builtInRegistryHolder().getData(EIDataMaps.PHOTOVOLTAIC_CELL);
	}
}
