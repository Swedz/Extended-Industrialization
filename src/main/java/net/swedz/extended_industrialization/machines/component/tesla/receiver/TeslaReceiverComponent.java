package net.swedz.extended_industrialization.machines.component.tesla.receiver;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetwork;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkKey;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworks;
import net.swedz.tesseract.neoforge.helper.transfer.InputOutputDirectionalBlockCapabilityCache;
import net.swedz.tesseract.neoforge.proxy.Proxies;
import net.swedz.tesseract.neoforge.proxy.builtin.TesseractProxy;

import java.util.Optional;
import java.util.function.Supplier;

public class TeslaReceiverComponent implements IComponent.ServerOnly, TeslaReceiver
{
	private final MachineBlockEntity  machine;
	private final Supplier<CableTier> cableTier;
	
	private final InputOutputDirectionalBlockCapabilityCache<MIEnergyStorage> energyOutputCache;
	private final MIEnergyStorage                                             insertable;
	
	private Optional<TeslaNetworkKey> networkKey = Optional.empty();
	
	public TeslaReceiverComponent(MachineBlockEntity machine, Supplier<Boolean> canOperate, Supplier<CableTier> cableTier)
	{
		this.machine = machine;
		this.cableTier = cableTier;
		
		energyOutputCache = new InputOutputDirectionalBlockCapabilityCache<>(EnergyApi.SIDED);
		insertable = new MIEnergyStorage.NoExtract()
		{
			@Override
			public boolean canConnect(CableTier cableTier)
			{
				return false;
			}
			
			@Override
			public long receive(long maxReceive, boolean simulate)
			{
				if(!canOperate.get())
				{
					return 0;
				}
				MIEnergyStorage target = energyOutputCache.output(machine.getLevel(), machine.getBlockPos(), machine.orientation.outputDirection);
				return target != null && target.canConnect(TeslaReceiverComponent.this.getCableTier()) ? target.receive(maxReceive, simulate) : 0;
			}
			
			@Override
			public long getAmount()
			{
				MIEnergyStorage target = energyOutputCache.output(machine.getLevel(), machine.getBlockPos(), machine.orientation.outputDirection);
				return target != null ? target.getAmount() : 0;
			}
			
			@Override
			public long getCapacity()
			{
				MIEnergyStorage target = energyOutputCache.output(machine.getLevel(), machine.getBlockPos(), machine.orientation.outputDirection);
				return target != null ? target.getCapacity() : 0;
			}
			
			@Override
			public boolean canReceive()
			{
				return canOperate.get();
			}
		};
	}
	
	public MIEnergyStorage insertable()
	{
		return insertable;
	}
	
	@Override
	public boolean canReceiveFrom(TeslaNetwork network)
	{
		return this.getCableTier() == network.getCableTier();
	}
	
	@Override
	public boolean hasNetwork()
	{
		return networkKey.isPresent();
	}
	
	@Override
	public TeslaNetworkKey getNetworkKey()
	{
		return networkKey.orElseThrow();
	}
	
	@Override
	public void setNetwork(TeslaNetworkKey key)
	{
		TesseractProxy proxy = Proxies.get(TesseractProxy.class);
		if(!proxy.hasServer())
		{
			throw new IllegalStateException("Cannot set network of a receiver from the client");
		}
		TeslaNetworks networks = proxy.getServer().getTeslaNetworks();
		
		if(this.hasNetwork())
		{
			TeslaNetwork oldNetwork = networks.get(this.getNetworkKey());
			oldNetwork.remove(this);
			if(oldNetwork.isEmpty())
			{
				networks.forget(oldNetwork);
			}
		}
		
		networkKey = Optional.ofNullable(key);
		
		if(this.hasNetwork())
		{
			networks.get(this.getNetworkKey()).add(this);
		}
	}
	
	@Override
	public BlockPos getPosition()
	{
		return machine.getBlockPos();
	}
	
	@Override
	public CableTier getCableTier()
	{
		return cableTier.get();
	}
	
	@Override
	public long receiveEnergy(long maxReceive, boolean simulate)
	{
		return insertable.receive(maxReceive, simulate);
	}
	
	@Override
	public long getStoredEnergy()
	{
		return insertable.getAmount();
	}
	
	@Override
	public long getEnergyCapacity()
	{
		return insertable.getCapacity();
	}
	
	public void removeFromNetwork()
	{
		if(this.hasNetwork())
		{
			this.getNetwork().remove(this);
		}
	}
	
	public void addToNetwork()
	{
		if(this.hasNetwork())
		{
			this.getNetwork().add(this);
		}
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		if(this.hasNetwork())
		{
			TeslaNetworkKey key = this.getNetworkKey();
			TeslaNetworkKey.CODEC.encodeStart(NbtOps.INSTANCE, key).result().ifPresent((t) -> tag.put("network_key", t));
		}
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
		if(tag.contains("network_key", Tag.TAG_COMPOUND))
		{
			CompoundTag keyTag = tag.getCompound("network_key");
			this.setNetwork(TeslaNetworkKey.CODEC.parse(NbtOps.INSTANCE, keyTag).result().orElse(null));
		}
		else
		{
			this.setNetwork(null);
		}
	}
}
