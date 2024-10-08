package net.swedz.extended_industrialization.machines.component.tesla.receiver;

import aztech.modern_industrialization.api.energy.CableTier;
import net.minecraft.util.Mth;
import net.swedz.extended_industrialization.api.WorldPos;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetwork;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkPart;
import net.swedz.extended_industrialization.machines.component.tesla.transmitter.TeslaTransmitter;

public interface TeslaReceiver extends TeslaNetworkPart
{
	default ReceiveCheckResult checkReceiveFrom(TeslaNetwork network)
	{
		TeslaTransmitter transmitter = network.getTransmitter();
		WorldPos transmitterPos = transmitter.getSourcePosition();
		WorldPos receiverPos = this.getSourcePosition();
		
		if(!transmitterPos.isSameDimension(receiverPos))
		{
			return transmitter.isInterdimensional() ? ReceiveCheckResult.SUCCESS : ReceiveCheckResult.TOO_FAR;
		}
		
		double distanceSqr = transmitterPos.distanceSqr(receiverPos);
		int maxDistanceSqr = Mth.square(network.getMaxDistance());
		if(distanceSqr > maxDistanceSqr)
		{
			// TODO check for global upgrade
			return transmitter.isInterdimensional() ? ReceiveCheckResult.SUCCESS : ReceiveCheckResult.TOO_FAR;
		}
		
		return ReceiveCheckResult.SUCCESS;
	}
	
	long receiveEnergy(long maxReceive, boolean simulate);
	
	long getStoredEnergy();
	
	long getEnergyCapacity();
	
	enum ReceiveCheckResult
	{
		SUCCESS,
		MISMATCHING_VOLTAGE,
		TOO_FAR,
		UNDEFINED;
		
		public boolean isSuccess()
		{
			return this == ReceiveCheckResult.SUCCESS;
		}
		
		public boolean isFailure()
		{
			return !this.isSuccess();
		}
	}
	
	interface Delegate extends TeslaReceiver
	{
		TeslaReceiver getDelegateReceiver();
		
		@Override
		default ReceiveCheckResult checkReceiveFrom(TeslaNetwork network)
		{
			return this.getDelegateReceiver().checkReceiveFrom(network);
		}
		
		@Override
		default boolean hasNetwork()
		{
			return this.getDelegateReceiver().hasNetwork();
		}
		
		@Override
		default WorldPos getNetworkKey()
		{
			return this.getDelegateReceiver().getNetworkKey();
		}
		
		@Override
		default void setNetwork(WorldPos key)
		{
			this.getDelegateReceiver().setNetwork(key);
		}
		
		@Override
		default WorldPos getPosition()
		{
			return this.getDelegateReceiver().getPosition();
		}
		
		@Override
		default WorldPos getSourcePosition()
		{
			return this.getDelegateReceiver().getSourcePosition();
		}
		
		@Override
		default CableTier getCableTier()
		{
			return this.getDelegateReceiver().getCableTier();
		}
		
		@Override
		default long receiveEnergy(long maxReceive, boolean simulate)
		{
			return this.getDelegateReceiver().receiveEnergy(maxReceive, simulate);
		}
		
		@Override
		default long getStoredEnergy()
		{
			return this.getDelegateReceiver().getStoredEnergy();
		}
		
		@Override
		default long getEnergyCapacity()
		{
			return this.getDelegateReceiver().getEnergyCapacity();
		}
	}
}
