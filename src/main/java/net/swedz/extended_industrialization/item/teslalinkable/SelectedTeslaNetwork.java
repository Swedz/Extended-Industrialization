package net.swedz.extended_industrialization.item.teslalinkable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.swedz.tesseract.neoforge.api.WorldPos;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

public record SelectedTeslaNetwork(WorldPos key, Block block)
{
	public static final Codec<SelectedTeslaNetwork> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					WorldPos.CODEC.fieldOf("key").forGetter(SelectedTeslaNetwork::key),
					CodecHelper.forRegistry(BuiltInRegistries.BLOCK).fieldOf("block").forGetter(SelectedTeslaNetwork::block)
			)
			.apply(instance, SelectedTeslaNetwork::new));
	
	public static final StreamCodec<RegistryFriendlyByteBuf, SelectedTeslaNetwork> STREAM_CODEC = StreamCodec.composite(
			WorldPos.STREAM_CODEC,
			SelectedTeslaNetwork::key,
			CodecHelper.forRegistryStream(BuiltInRegistries.BLOCK),
			SelectedTeslaNetwork::block,
			SelectedTeslaNetwork::new
	);
}
