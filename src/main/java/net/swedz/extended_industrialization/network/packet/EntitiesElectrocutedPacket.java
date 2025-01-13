package net.swedz.extended_industrialization.network.packet;

import aztech.modern_industrialization.machines.MachineBlockEntity;
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
import net.swedz.extended_industrialization.EIClient;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaBehavior;
import net.swedz.extended_industrialization.network.EICustomPacket;
import net.swedz.extended_industrialization.proxy.EIProxy;
import net.swedz.tesseract.neoforge.packet.PacketContext;
import net.swedz.tesseract.neoforge.proxy.Proxies;

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
		var blockEntity = level.getBlockEntity(origin);
		
		if(entityIds.isEmpty() ||
		   !(blockEntity instanceof MachineBlockEntity) ||
		   !(blockEntity instanceof TeslaBehavior))
		{
			return;
		}
		
		if(EIClient.config().renderTeslaAnimations())
		{
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
					Proxies.get(EIProxy.class).createTeslaArc(origin, pos);
				}
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
