package net.swedz.extended_industrialization.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.swedz.extended_industrialization.network.EICustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record FarmerFertilizeBlockPacket(BlockPos pos) implements EICustomPacket
{
	public static final StreamCodec<ByteBuf, FarmerFertilizeBlockPacket> STREAM_CODEC = BlockPos.STREAM_CODEC
			.map(FarmerFertilizeBlockPacket::new, FarmerFertilizeBlockPacket::pos);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertClientbound();
		
		var level = context.getPlayer().level();
		
		ParticleUtils.spawnParticleInBlock(level, pos, 4, ParticleTypes.HAPPY_VILLAGER);
		level.playLocalSound(pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1, 1, false);
	}
}
