package net.swedz.extended_industrialization.machines.component.tesla;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import com.google.common.collect.Sets;
import net.swedz.extended_industrialization.api.WorldPos;
import net.swedz.extended_industrialization.machines.component.tesla.receiver.TeslaReceiver;
import net.swedz.extended_industrialization.machines.component.tesla.transmitter.TeslaTransmitter;

import java.util.Optional;
import java.util.Set;

public final class TeslaNetwork implements MIEnergyStorage.NoExtract
{
	private final TeslaNetworkCache cache;
	private final WorldPos          key;
	
	private final Set<TeslaReceiver> loadedReceivers = Sets.newHashSet();
	private final Set<TeslaReceiver> receivers       = Sets.newHashSet();
	
	private Optional<TeslaTransmitter> transmitter = Optional.empty();
	
	public TeslaNetwork(TeslaNetworkCache cache, WorldPos key)
	{
		this.cache = cache;
		this.key = key;
	}
	
	private void maybeForget()
	{
		if(transmitter.isEmpty() && loadedReceivers.isEmpty())
		{
			cache.forget(this);
		}
	}
	
	public WorldPos key()
	{
		return key;
	}
	
	public boolean isTransmitterLoaded()
	{
		return transmitter.isPresent() && transmitter.get().getPosition().isTicking();
	}
	
	public void loadTransmitter(TeslaTransmitter transmitter)
	{
		this.transmitter = Optional.of(transmitter);
		this.updateAll();
	}
	
	public void unloadTransmitter()
	{
		transmitter = Optional.empty();
		this.maybeForget();
	}
	
	public TeslaTransmitter getTransmitter()
	{
		return transmitter.orElseThrow();
	}
	
	public CableTier getCableTier()
	{
		if(!this.isTransmitterLoaded())
		{
			throw new IllegalStateException("Cannot get cable tier from network without a transmitter");
		}
		return this.getTransmitter().getCableTier();
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
		if(this.isTransmitterLoaded() && receiver.canReceiveFrom(this).isSuccess())
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
		this.maybeForget();
	}
	
	public int receiverCount()
	{
		return receivers.size();
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
		return this.getCableTier() == cableTier;
	}
}
