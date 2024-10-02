package net.swedz.extended_industrialization.api;

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

public record WorldPos(ResourceKey<Level> dimension, BlockPos pos)
{
	public static final MapCodec<WorldPos>             MAP_CODEC    = RecordCodecBuilder.mapCodec((instance) -> instance
			.group(
					Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(WorldPos::dimension),
					BlockPos.CODEC.fieldOf("pos").forGetter(WorldPos::pos)
			)
			.apply(instance, WorldPos::new));
	public static final Codec<WorldPos>                CODEC        = MAP_CODEC.codec();
	public static final StreamCodec<ByteBuf, WorldPos> STREAM_CODEC = StreamCodec.composite(
			ResourceKey.streamCodec(Registries.DIMENSION),
			WorldPos::dimension,
			BlockPos.STREAM_CODEC,
			WorldPos::pos,
			WorldPos::new
	);
	
	public WorldPos(Level level, BlockPos pos)
	{
		this(level.dimension(), pos);
	}
	
	public ServerLevel level()
	{
		TesseractProxy proxy = Proxies.get(TesseractProxy.class);
		if(!proxy.hasServer())
		{
			throw new IllegalStateException("Cannot get level from world position on the client");
		}
		return proxy.getServer().getLevel(dimension);
	}
	
	public boolean isLoaded()
	{
		return this.level().isLoaded(pos);
	}
	
	public boolean isTicking()
	{
		Level level = this.level();
		return level.tickRateManager().runsNormally() &&
			   level.shouldTickBlocksAt(pos);
	}
	
	public boolean isSameDimension(WorldPos other)
	{
		return dimension.equals(other.dimension());
	}
	
	public double distanceSqr(WorldPos other)
	{
		if(!this.isSameDimension(other))
		{
			throw new IllegalArgumentException("Mismatching dimensions in distance check");
		}
		return pos.distSqr(other.pos());
	}
	
	@Override
	public int hashCode()
	{
		int dimensionHashCode = (31 * dimension.registry().hashCode() + dimension.location().hashCode());
		return 31 * dimensionHashCode + pos.hashCode();
	}
}
