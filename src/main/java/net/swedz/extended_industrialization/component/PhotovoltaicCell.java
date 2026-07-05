package net.swedz.extended_industrialization.component;

import aztech.modern_industrialization.api.energy.CableTier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.tesseract.api.Assert;
import net.swedz.tesseract.neoforge.compat.mi.serialization.MICodecs;
import net.swedz.tesseract.neoforge.compat.mi.serialization.MIStreamCodecs;

public record PhotovoltaicCell(
		CableTier tier,
		int euPerTick,
		int lifetimeTicks,
		float minimumEfficiency
)
{
	public static final Codec<PhotovoltaicCell> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					MICodecs.CABLE_TIER.fieldOf("tier").forGetter(PhotovoltaicCell::tier),
					Codec.INT.fieldOf("eu_per_tick").forGetter(PhotovoltaicCell::euPerTick),
					Codec.INT.fieldOf("lifetime_ticks").forGetter(PhotovoltaicCell::lifetimeTicks),
					Codec.FLOAT.fieldOf("minimum_efficiency").forGetter(PhotovoltaicCell::minimumEfficiency)
			)
			.apply(instance, PhotovoltaicCell::new));
	
	public static final StreamCodec<ByteBuf, PhotovoltaicCell> STREAM_CODEC = StreamCodec.composite(
			MIStreamCodecs.CABLE_TIER,
			PhotovoltaicCell::tier,
			ByteBufCodecs.INT,
			PhotovoltaicCell::euPerTick,
			ByteBufCodecs.INT,
			PhotovoltaicCell::lifetimeTicks,
			ByteBufCodecs.FLOAT,
			PhotovoltaicCell::minimumEfficiency,
			PhotovoltaicCell::new
	);
	
	public PhotovoltaicCell
	{
		Assert.that(euPerTick > 0, "EU per tick must be > 0");
		Assert.that(lifetimeTicks >= 0, "Lifetime ticks must be positive");
		Assert.that(minimumEfficiency >= 0 && minimumEfficiency <= 1, "Minimum efficiency must be between 0 and 1");
	}
	
	public boolean lastsForever()
	{
		return lifetimeTicks == 0;
	}
}
