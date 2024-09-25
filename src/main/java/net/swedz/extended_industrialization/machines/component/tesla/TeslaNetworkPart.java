package net.swedz.extended_industrialization.machines.component.tesla;

public interface TeslaNetworkPart
{
	boolean hasNetwork();
	
	TeslaNetworkKey getNetworkKey();
	
	void setNetwork(TeslaNetworkKey key);
}
