package net.swedz.extended_industrialization.machines.component.tesla;

import net.minecraft.core.BlockPos;

public interface TeslaNetworkPart
{
	boolean hasNetwork();
	
	TeslaNetworkKey getNetworkKey();
	
	void setNetwork(TeslaNetworkKey key);
	
	BlockPos getPosition();
}
