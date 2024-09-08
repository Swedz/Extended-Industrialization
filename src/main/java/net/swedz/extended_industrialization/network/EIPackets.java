package net.swedz.extended_industrialization.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.network.packet.ModifyElectricToolSpeedPacket;
import net.swedz.extended_industrialization.network.packet.ToggleToggleableItemPacket;
import net.swedz.tesseract.neoforge.packet.PacketRegistry;

public final class EIPackets
{
	private static final PacketRegistry<EICustomPacket> REGISTRY = PacketRegistry.create(EI.ID);
	
	public static CustomPacketPayload.Type<EICustomPacket> getType(Class<? extends EICustomPacket> packetClass)
	{
		return REGISTRY.getType(packetClass);
	}
	
	public static void init(RegisterPayloadHandlersEvent event)
	{
		REGISTRY.registerAll(event);
	}
	
	static
	{
		create("modify_electric_tool_speed", ModifyElectricToolSpeedPacket.class, ModifyElectricToolSpeedPacket.STREAM_CODEC);
		create("toggle_nano_suit_ability", ToggleToggleableItemPacket.class, ToggleToggleableItemPacket.STREAM_CODEC);
	}
	
	private static <P extends EICustomPacket> void create(String id, Class<P> packetClass, StreamCodec<? super RegistryFriendlyByteBuf, P> packetCodec)
	{
		REGISTRY.create(id, packetClass, packetCodec);
	}
}
