package net.swedz.extended_industrialization.network.packet;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.EIClient;
import net.swedz.extended_industrialization.network.EICustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record EntitiesElectrocutedPacket(IntList entityIds) implements EICustomPacket
{
	public static final StreamCodec<FriendlyByteBuf, EntitiesElectrocutedPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()).map(IntArrayList::new, Lists::newArrayList),
			EntitiesElectrocutedPacket::entityIds,
			EntitiesElectrocutedPacket::new
	);
	
	private static final int SPARK_COUNT = 3;
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertClientbound();
		
		var level = context.getPlayer().level();
		
		if(entityIds.isEmpty() ||
		   !EIClient.config().renderTeslaAnimations())
		{
			return;
		}
		
		for(int entityId : entityIds)
		{
			var entity = level.getEntity(entityId);
			if(entity != null)
			{
				Vec3 pos = entity.getBoundingBox().getCenter();
				for(int i = 0; i < SPARK_COUNT; i++)
				{
					spark(level, pos);
				}
			}
		}
	}
	
	private static float sparkSpeed(LevelAccessor level)
	{
		var random = level.getRandom();
		return random.nextFloat() * 0.3f * (random.nextBoolean() ? -1 : 1);
	}
	
	private static void spark(LevelAccessor level, Vec3 pos)
	{
		level.addParticle(
				ParticleTypes.FIREWORK,
				pos.x, pos.y, pos.z,
				sparkSpeed(level), sparkSpeed(level), sparkSpeed(level)
		);
	}
}
