package net.swedz.extended_industrialization.network;

import net.swedz.tesseract.neoforge.packet.CustomPacket;

public interface EICustomPacket extends CustomPacket
{
	@Override
	default Type<EICustomPacket> type()
	{
		return EIPackets.getType(this.getClass());
	}
}