package net.swedz.extended_industrialization.machines.component.tesla.receiver;

import net.minecraft.core.BlockPos;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetwork;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkKey;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkPart;

public interface TeslaReceiver extends TeslaNetworkPart
{
	boolean canReceiveFrom(TeslaNetwork network);
	
	long receiveEnergy(long maxReceive, boolean simulate);
	
	long getStoredEnergy();
	
	long getEnergyCapacity();
	
	interface Delegate extends TeslaReceiver
	{
		TeslaReceiver getDelegateReceiver();
		
		@Override
		default boolean canReceiveFrom(TeslaNetwork network)
		{
			return this.getDelegateReceiver().canReceiveFrom(network);
		}
		
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
		default BlockPos getPosition()
		{
			return this.getDelegateReceiver().getPosition();
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
