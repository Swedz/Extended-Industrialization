package net.swedz.extended_industrialization.machines.component.tesla.receiver;

import aztech.modern_industrialization.api.energy.CableTier;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetwork;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkPart;
import net.swedz.extended_industrialization.api.WorldPos;

public interface TeslaReceiver extends TeslaNetworkPart
{
	ReceiveCheckResult checkReceiveFrom(TeslaNetwork network);
	
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
