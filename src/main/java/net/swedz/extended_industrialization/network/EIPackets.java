package net.swedz.extended_industrialization.network;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.network.packet.ModifyElectricToolSpeedPacket;

import java.util.Map;
import java.util.Set;

public final class EIPackets
{
	public static final class Registry
	{
		private static final Set<PacketRegistration<EIBasePacket>>                                      PACKET_REGISTRATIONS = Sets.newHashSet();
		private static final Map<Class<? extends EIBasePacket>, CustomPacketPayload.Type<EIBasePacket>> PACKET_TYPES         = Maps.newHashMap();
		
		private record PacketRegistration<P extends EIBasePacket>(
				CustomPacketPayload.Type<P> packetType,
				Class<P> packetClass,
				StreamCodec<? super RegistryFriendlyByteBuf, P> packetCodec
		)
		{
		}
		
		private static void init(RegisterPayloadHandlersEvent event)
		{
			PayloadRegistrar registrar = event.registrar(EI.ID);
			
			for(PacketRegistration<EIBasePacket> packetRegistration : Registry.PACKET_REGISTRATIONS)
			{
				registrar.playBidirectional(packetRegistration.packetType(), packetRegistration.packetCodec(), (packet, context) ->
						packet.handle(new EIBasePacket.Context(packetRegistration.packetClass(), context)));
			}
		}
		
		public static CustomPacketPayload.Type<EIBasePacket> getType(Class<? extends EIBasePacket> packetClass)
		{
			return PACKET_TYPES.get(packetClass);
		}
	}
	
	public static void init(RegisterPayloadHandlersEvent event)
	{
		Registry.init(event);
	}
	
	static
	{
		register("modify_electric_tool_speed", ModifyElectricToolSpeedPacket.class, ModifyElectricToolSpeedPacket.STREAM_CODEC);
	}
	
	private static <P extends EIBasePacket> void register(String path, Class<P> packetClass, StreamCodec<? super RegistryFriendlyByteBuf, P> packetCodec)
	{
		CustomPacketPayload.Type type = new CustomPacketPayload.Type<>(EI.id(path));
		Registry.PACKET_REGISTRATIONS.add(new Registry.PacketRegistration<>(type, packetClass, packetCodec));
		Registry.PACKET_TYPES.put(packetClass, type);
	}
}
