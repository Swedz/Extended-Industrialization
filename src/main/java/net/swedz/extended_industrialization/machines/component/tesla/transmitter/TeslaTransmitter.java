package net.swedz.extended_industrialization.machines.component.tesla.transmitter;

import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkKey;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkPart;

public interface TeslaTransmitter extends TeslaNetworkPart
{
	long transmitEnergy(long maxTransmit);
	
	interface Delegate extends TeslaTransmitter
	{
		TeslaTransmitter getDelegateTransmitter();
		
		@Override
		default boolean hasNetwork()
		{
			return this.getDelegateTransmitter().hasNetwork();
		}
		
		@Override
		default TeslaNetworkKey getNetworkKey()
		{
			return this.getDelegateTransmitter().getNetworkKey();
		}
		
		@Override
		default void setNetwork(TeslaNetworkKey key)
		{
			this.getDelegateTransmitter().setNetwork(key);
		}
		
		@Override
		default long transmitEnergy(long maxTransmit)
		{
			return this.getDelegateTransmitter().transmitEnergy(maxTransmit);
		}
	}
}
