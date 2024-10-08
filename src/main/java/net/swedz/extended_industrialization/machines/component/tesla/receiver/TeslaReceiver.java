package net.swedz.extended_industrialization.machines.component.tesla.receiver;

import aztech.modern_industrialization.api.energy.CableTier;
import net.minecraft.util.Mth;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetwork;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkPart;
import net.swedz.extended_industrialization.api.WorldPos;
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
			return transmitter.isInterdimensional() ?
					ReceiveCheckResult.success(network.getMaxLoss()) :
					ReceiveCheckResult.failure(ReceiveCheckResult.Type.TOO_FAR);
		}
		
		double distanceSqr = transmitterPos.distanceSqr(receiverPos);
		int maxDistanceSqr = Mth.square(network.getMaxDistance());
		if(distanceSqr > maxDistanceSqr)
		{
			// TODO check for global upgrade
			return transmitter.isInterdimensional() ?
					ReceiveCheckResult.success(network.getMaxLoss()) :
					ReceiveCheckResult.failure(ReceiveCheckResult.Type.TOO_FAR);
		}
		
		float loss = ((float) distanceSqr / maxDistanceSqr) * network.getMaxLoss();
		return ReceiveCheckResult.success(loss);
	}
	
	long receiveEnergy(long maxReceive, boolean simulate);
	
	long getStoredEnergy();
	
	long getEnergyCapacity();
	
	record ReceiveCheckResult(Type type, float loss)
	{
		public static ReceiveCheckResult success(float loss)
		{
			return new ReceiveCheckResult(Type.SUCCESS, loss);
		}
		
		public static ReceiveCheckResult failure(Type type)
		{
			if(type == Type.SUCCESS)
			{
				throw new IllegalArgumentException("Cannot create failure result with success type");
			}
			return new ReceiveCheckResult(type, 0f);
		}
		
		public boolean isSuccess()
		{
			return type == Type.SUCCESS;
		}
		
		public boolean isFailure()
		{
			return !this.isSuccess();
		}
		
		public enum Type
		{
			SUCCESS,
			MISMATCHING_VOLTAGE,
			TOO_FAR,
			UNDEFINED
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
