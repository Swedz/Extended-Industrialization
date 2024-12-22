package net.swedz.extended_industrialization.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.network.EICustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record CatHammerPacket(int entityId) implements EICustomPacket
{
	public static final StreamCodec<ByteBuf, CatHammerPacket> STREAM_CODEC = ByteBufCodecs.VAR_INT
			.map(CatHammerPacket::new, CatHammerPacket::entityId);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertClientbound();
		
		var level = context.getPlayer().level();
		var entity = level.getEntity(entityId);
		
		if(entity != null)
		{
			Vec3 pos = entity.getEyePosition();
			Vec3 direction = entity.getViewVector(1).scale(0.8);
			pos = pos.add(direction);
			pos = new Vec3(pos.x, entity.getY() + 0.1, pos.z);
			
			flash(level, pos);
			for(int i = 0; i < 4; i++)
			{
				spark(level, pos);
			}
		}
	}
	
	private static void flash(LevelAccessor level, Vec3 pos)
	{
		level.addParticle(ParticleTypes.FLASH, pos.x, pos.y, pos.z, 0, 0, 0);
	}
	
	private static float sparkSpeed(LevelAccessor level, boolean y)
	{
		var random = level.getRandom();
		return random.nextFloat() * 0.2f * (y ? 1 : (random.nextBoolean() ? -1 : 1));
	}
	
	private static void spark(LevelAccessor level, Vec3 pos)
	{
		level.addParticle(
				ParticleTypes.FIREWORK,
				pos.x, pos.y, pos.z,
				sparkSpeed(level, false), sparkSpeed(level, true), sparkSpeed(level, false)
		);
	}
}
