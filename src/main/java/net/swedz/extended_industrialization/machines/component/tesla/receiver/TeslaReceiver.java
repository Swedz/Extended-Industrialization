package net.swedz.extended_industrialization.machines.component.tesla.receiver;

import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkKey;

public interface TeslaReceiver
{
	boolean hasNetwork();
	
	TeslaNetworkKey getNetworkKey();
	
	void setNetwork(TeslaNetworkKey key);
	
	long receiveEnergy(long maxReceive, boolean simulate);
	
	long getStoredEnergy();
	
	long getEnergyCapacity();
	
	interface Delegate extends TeslaReceiver
	{
		TeslaReceiver getDelegateReceiver();
		
		@Override
		default boolean hasNetwork()
		{
			return this.getDelegateReceiver().hasNetwork();
		}
		
		@Override
		default TeslaNetworkKey getNetworkKey()
		{
			return this.getDelegateReceiver().getNetworkKey();
		}
		
		@Override
		default void setNetwork(TeslaNetworkKey key)
		{
			this.getDelegateReceiver().setNetwork(key);
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
