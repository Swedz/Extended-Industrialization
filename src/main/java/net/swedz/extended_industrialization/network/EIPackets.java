package net.swedz.extended_industrialization.network;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.network.packets.ConfigureMachinePacket;

import java.util.Map;
import java.util.Set;

public final class EIPackets
{
	public static final class Registry
	{
		private static final Set<PacketRegistration<?>>                           PACKET_REGISTRATIONS = Sets.newHashSet();
		private static final Map<Class<? extends EIBasePacket>, ResourceLocation> PACKET_IDS           = Maps.newHashMap();
		
		private record PacketRegistration<P extends EIBasePacket>(
				ResourceLocation resourceLocation,
				Class<P> packetClass,
				FriendlyByteBuf.Reader<P> packetConstructor
		)
		{
		}
		
		private static void init(RegisterPayloadHandlerEvent event)
		{
			IPayloadRegistrar registrar = event.registrar(EI.ID);
			for(PacketRegistration<?> packetRegistration : PACKET_REGISTRATIONS)
			{
				registrar.play(packetRegistration.resourceLocation(), packetRegistration.packetConstructor(), (p, context) ->
						context.workHandler().execute(() ->
								p.handle(new EIBasePacket.Context(packetRegistration.packetClass(), context))));
			}
		}
	}
	
	public static void init(RegisterPayloadHandlerEvent event)
	{
		Registry.init(event);
	}
	
	static
	{
		create("configure_machine", ConfigureMachinePacket.class, ConfigureMachinePacket::new);
	}
	
	public static ResourceLocation getId(Class<? extends EIBasePacket> packetClass)
	{
		return Registry.PACKET_IDS.get(packetClass);
	}
	
	public static <P extends EIBasePacket> void create(String path, Class<P> packetClass, FriendlyByteBuf.Reader<P> packetConstructor)
	{
		Registry.PACKET_REGISTRATIONS.add(new Registry.PacketRegistration<>(EI.id(path), packetClass, packetConstructor));
		Registry.PACKET_IDS.put(packetClass, EI.id(path));
	}
}
