package net.swedz.extended_industrialization.machines.component.tesla;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import com.google.common.collect.Sets;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiver;

import java.util.Set;

public final class TeslaNetwork implements MIEnergyStorage.NoExtract
{
	private final TeslaNetworkKey key;
	
	private final Set<TeslaReceiver> loadedReceivers = Sets.newHashSet();
	private final Set<TeslaReceiver> receivers       = Sets.newHashSet();
	
	private CableTier cableTier;
	
	public TeslaNetwork(TeslaNetworkKey key)
	{
		this.key = key;
	}
	
	public TeslaNetworkKey key()
	{
		return key;
	}
	
	public CableTier getCableTier()
	{
		return cableTier;
	}
	
	public void setCableTier(CableTier cableTier)
	{
		this.cableTier = cableTier;
		this.updateAll();
	}
	
	private void updateAll()
	{
		for(TeslaReceiver receiver : loadedReceivers)
		{
			this.update(receiver);
		}
	}
	
	private void update(TeslaReceiver receiver)
	{
		if(receiver.canReceiveFrom(this))
		{
			receivers.add(receiver);
		}
		else
		{
			receivers.remove(receiver);
		}
	}
	
	public void add(TeslaReceiver receiver)
	{
		loadedReceivers.add(receiver);
		this.update(receiver);
	}
	
	public void remove(TeslaReceiver receiver)
	{
		loadedReceivers.remove(receiver);
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
		return true;
	}
	
	@Override
	public long receive(long maxReceive, boolean simulate)
	{
		long amountReceived = 0;
		long remaining = maxReceive;
		int index = 0;
		for(TeslaReceiver receiver : receivers)
		{
			int remainingStorages = receivers.size() - index;
			long amountToReceive = remainingStorages == 1 ? remaining : remaining / remainingStorages;
			long received = receiver.receiveEnergy(amountToReceive, simulate);
			amountReceived += received;
			remaining -= received;
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
		return this.cableTier == cableTier;
	}
}
