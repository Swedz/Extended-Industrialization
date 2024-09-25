package net.swedz.extended_industrialization.machines.component.tesla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.swedz.tesseract.neoforge.proxy.Proxies;
import net.swedz.tesseract.neoforge.proxy.builtin.TesseractProxy;

public record TeslaNetworkKey(ResourceKey<Level> dimension, BlockPos pos)
{
	public static final MapCodec<TeslaNetworkKey>             MAP_CODEC    = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(TeslaNetworkKey::dimension),
					BlockPos.CODEC.fieldOf("pos").forGetter(TeslaNetworkKey::pos)
			)
			.apply(instance, TeslaNetworkKey::new));
	public static final Codec<TeslaNetworkKey>                CODEC        = MAP_CODEC.codec();
	public static final StreamCodec<ByteBuf, TeslaNetworkKey> STREAM_CODEC = StreamCodec.composite(
			ResourceKey.streamCodec(Registries.DIMENSION),
			TeslaNetworkKey::dimension,
			BlockPos.STREAM_CODEC,
			TeslaNetworkKey::pos,
			TeslaNetworkKey::new
	);
	
	public TeslaNetworkKey(Level level, BlockPos pos)
	{
		this(level.dimension(), pos);
	}
	
	public ServerLevel level()
	{
		TesseractProxy proxy = Proxies.get(TesseractProxy.class);
		return proxy.hasServer() ? proxy.getServer().getLevel(dimension) : null;
	}
	
	@Override
	public int hashCode()
	{
		int dimensionHashCode = (31 * dimension.registry().hashCode() + dimension.location().hashCode());
		return 31 * dimensionHashCode + pos.hashCode();
	}
}
