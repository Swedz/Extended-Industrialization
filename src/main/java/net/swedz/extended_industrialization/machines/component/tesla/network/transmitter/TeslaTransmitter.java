package net.swedz.extended_industrialization.machines.component.tesla.network.transmitter;

import aztech.modern_industrialization.api.energy.CableTier;
import net.swedz.extended_industrialization.machines.component.tesla.network.TeslaNetworkPart;
import net.swedz.extended_industrialization.machines.component.tesla.network.TeslaTransferLimits;
import net.swedz.tesseract.neoforge.api.WorldPos;

public interface TeslaTransmitter extends TeslaNetworkPart, TeslaTransferLimits
{
	boolean isInterdimensional();
	
	long transmitEnergy(long maxTransmit);
	
	long extractEnergy(long maxExtract, boolean simulate);
	
	interface Delegate extends TeslaTransmitter
	{
		TeslaTransmitter getDelegateTransmitter();
		
		@Override
		default boolean hasNetwork()
		{
			return this.getDelegateTransmitter().hasNetwork();
		}
		
		@Override
		default WorldPos getNetworkKey()
		{
			return this.getDelegateTransmitter().getNetworkKey();
		}
		
		@Override
		default void setNetwork(WorldPos key)
		{
			this.getDelegateTransmitter().setNetwork(key);
		}
		
		@Override
		default WorldPos getPosition()
		{
			return this.getDelegateTransmitter().getPosition();
		}
		
		@Override
		default WorldPos getSourcePosition()
		{
			return this.getDelegateTransmitter().getSourcePosition();
		}
		
		@Override
		default CableTier getCableTier()
		{
			return this.getDelegateTransmitter().getCableTier();
		}
		
		@Override
		default long getMaxTransfer()
		{
			return this.getDelegateTransmitter().getMaxTransfer();
		}
		
		@Override
		default int getMaxDistance()
		{
			return this.getDelegateTransmitter().getMaxDistance();
		}
		
		@Override
		default long getPassiveDrain()
		{
			return this.getDelegateTransmitter().getPassiveDrain();
		}
		
		@Override
		default boolean isInterdimensional()
		{
			return this.getDelegateTransmitter().isInterdimensional();
		}
		
		@Override
		default long transmitEnergy(long maxTransmit)
		{
			return this.getDelegateTransmitter().transmitEnergy(maxTransmit);
		}
		
		@Override
		default long extractEnergy(long maxExtract, boolean simulate)
		{
			return this.getDelegateTransmitter().extractEnergy(maxExtract, simulate);
		}
	}
}
