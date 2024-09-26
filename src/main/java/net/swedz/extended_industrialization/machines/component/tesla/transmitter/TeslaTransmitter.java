package net.swedz.extended_industrialization.machines.component.tesla.transmitter;

import aztech.modern_industrialization.api.energy.CableTier;
import net.minecraft.core.BlockPos;
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
		default BlockPos getPosition()
		{
			return this.getDelegateTransmitter().getPosition();
		}
		
		@Override
		default CableTier getCableTier()
		{
			return this.getDelegateTransmitter().getCableTier();
		}
		
		@Override
		default long transmitEnergy(long maxTransmit)
		{
			return this.getDelegateTransmitter().transmitEnergy(maxTransmit);
		}
	}
}
