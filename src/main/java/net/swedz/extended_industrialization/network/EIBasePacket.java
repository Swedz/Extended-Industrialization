package net.swedz.extended_industrialization.network;

import aztech.modern_industrialization.network.BasePacket;
import net.minecraft.resources.ResourceLocation;

public interface EIBasePacket extends BasePacket
{
	@Override
	default ResourceLocation id()
	{
		return EIPackets.getId(this.getClass());
	}
}
