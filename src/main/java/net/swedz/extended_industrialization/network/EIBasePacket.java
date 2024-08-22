package net.swedz.extended_industrialization.network;

import aztech.modern_industrialization.network.BasePacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface EIBasePacket extends BasePacket
{
	@Override
	default Type<? extends CustomPacketPayload> type()
	{
		return EIPackets.Registry.getType(this.getClass());
	}
}