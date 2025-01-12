package net.swedz.extended_industrialization.network.packet;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaArcBehaviorHolder;
import net.swedz.extended_industrialization.network.EICustomPacket;
import net.swedz.tesseract.neoforge.packet.PacketContext;

public record EntitiesElectrocutedPacket(BlockPos origin, IntList entityIds) implements EICustomPacket
{
	public static final StreamCodec<FriendlyByteBuf, EntitiesElectrocutedPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC,
			EntitiesElectrocutedPacket::origin,
			ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()).map(IntArrayList::new, Lists::newArrayList),
			EntitiesElectrocutedPacket::entityIds,
			EntitiesElectrocutedPacket::new
	);
	
	@Override
	public void handle(PacketContext context)
	{
		context.assertClientbound();
		
		var level = context.getPlayer().level();
		
		if(entityIds.isEmpty() ||
		   !(level.getBlockEntity(origin) instanceof TeslaArcBehaviorHolder blockEntity))
		{
			return;
		}
		
		var arcs = blockEntity.getTeslaArcBehavior().getArcs();
		
		for(int entityId : entityIds)
		{
			var entity = level.getEntity(entityId);
			if(entity != null)
			{
				Vec3 pos = entity.getBoundingBox().getCenter();
				for(int i = 0; i < 12; i++)
				{
					spark(level, pos);
				}
				arcs.createArc(pos);
			}
		}
		
		level.playLocalSound(origin, SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.BLOCKS, 1, 2, false);
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
