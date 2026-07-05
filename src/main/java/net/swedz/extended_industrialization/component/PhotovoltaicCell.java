package net.swedz.extended_industrialization.component;

import aztech.modern_industrialization.api.energy.CableTier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.tesseract.neoforge.compat.mi.serialization.MICodecs;
import net.swedz.tesseract.neoforge.compat.mi.serialization.MIStreamCodecs;

public record PhotovoltaicCell(
		CableTier tier,
		int euPerTick,
		int durationTicks
)
{
	public static final Codec<PhotovoltaicCell> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					MICodecs.CABLE_TIER.fieldOf("tier").forGetter(PhotovoltaicCell::tier),
					Codec.INT.fieldOf("eu_per_tick").forGetter(PhotovoltaicCell::euPerTick),
					Codec.INT.fieldOf("duration_ticks").forGetter(PhotovoltaicCell::durationTicks)
			)
			.apply(instance, PhotovoltaicCell::new));
	
	public static final StreamCodec<ByteBuf, PhotovoltaicCell> STREAM_CODEC = StreamCodec.composite(
			MIStreamCodecs.CABLE_TIER,
			PhotovoltaicCell::tier,
			ByteBufCodecs.INT,
			PhotovoltaicCell::euPerTick,
			ByteBufCodecs.INT,
			PhotovoltaicCell::durationTicks,
			PhotovoltaicCell::new
	);
	
	public boolean lastsForever()
	{
		return durationTicks == 0;
	}
}
