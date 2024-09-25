package net.swedz.extended_industrialization.machines.component.tesla;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import com.google.common.collect.Sets;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiver;

import java.util.Set;

public final class TeslaNetwork implements MIEnergyStorage.NoExtract
{
	private final TeslaNetworkKey key;
	
	private final Set<TeslaReceiver> receivers = Sets.newHashSet();
	
	public TeslaNetwork(TeslaNetworkKey key)
	{
		this.key = key;
	}
	
	public TeslaNetworkKey key()
	{
		return key;
	}
	
	public void add(TeslaReceiver receiver)
	{
		receivers.add(receiver);
	}
	
	public void remove(TeslaReceiver receiver)
	{
		receivers.remove(receiver);
	}
	
	public int size()
	{
		return receivers.size();
	}
	
	public boolean isEmpty()
	{
		return receivers.isEmpty();
	}
	
	@Override
	public boolean canReceive()
	{
		return !receivers.isEmpty();
	}
	
	@Override
	public long receive(long maxReceive, boolean simulate)
	{
		long amountReceived = 0;
		int index = 0;
		for(TeslaReceiver receiver : receivers)
		{
			int remainingStorages = receivers.size() - index;
			long remainingAmountToReceive = maxReceive - amountReceived;
			long amountToReceive = remainingAmountToReceive / remainingStorages;
			amountReceived += receiver.receiveEnergy(amountToReceive, simulate);
			index++;
		}
		return amountReceived;
	}
	
	@Override
	public long getAmount()
	{
		return receivers.stream().mapToLong(TeslaReceiver::getStoredEnergy).sum();
	}
	
	@Override
	public long getCapacity()
	{
		return receivers.stream().mapToLong(TeslaReceiver::getEnergyCapacity).sum();
	}
	
	@Override
	public boolean canConnect(CableTier cableTier)
	{
		return false;
	}
}
