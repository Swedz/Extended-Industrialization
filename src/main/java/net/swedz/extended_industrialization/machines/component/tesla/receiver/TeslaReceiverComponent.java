package net.swedz.extended_industrialization.machines.component.tesla.receiver;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.swedz.extended_industrialization.api.WorldPos;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetwork;
import net.swedz.tesseract.neoforge.proxy.Proxies;
import net.swedz.tesseract.neoforge.proxy.builtin.TesseractProxy;

import java.util.Optional;
import java.util.function.Supplier;

public class TeslaReceiverComponent implements IComponent, TeslaReceiver
{
	private final MachineBlockEntity  machine;
	private final MIEnergyStorage     insertable;
	private final Supplier<CableTier> cableTier;
	
	private Optional<WorldPos> networkKey = Optional.empty();
	
	public TeslaReceiverComponent(MachineBlockEntity machine, MIEnergyStorage energyInsertable, Supplier<Boolean> canOperate, Supplier<CableTier> cableTier)
	{
		this.machine = machine;
		this.insertable = energyInsertable;
		this.cableTier = cableTier;
	}
	
	@Override
	public TeslaReceiverState checkReceiveFrom(TeslaNetwork network)
	{
		if(this.getCableTier() != network.getCableTier())
		{
			return TeslaReceiverState.MISMATCHING_VOLTAGE;
		}
		else if(machine.hasLevel())
		{
			return TeslaReceiver.super.checkReceiveFrom(network);
		}
		else
		{
			return TeslaReceiverState.UNDEFINED;
		}
	}
	
	@Override
	public boolean hasNetwork()
	{
		return networkKey.isPresent();
	}
	
	@Override
	public WorldPos getNetworkKey()
	{
		return networkKey.orElseThrow();
	}
	
	@Override
	public void setNetwork(WorldPos key)
	{
		if(!Proxies.get(TesseractProxy.class).hasServer())
		{
			throw new IllegalStateException("Cannot set network of a receiver from the client");
		}
		
		this.removeFromNetwork();
		
		networkKey = Optional.ofNullable(key);
		
		this.addToNetwork();
	}
	
	@Override
	public WorldPos getPosition()
	{
		return new WorldPos(machine.getLevel(), machine.getBlockPos());
	}
	
	@Override
	public WorldPos getSourcePosition()
	{
		return this.getPosition();
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
			WorldPos key = this.getNetworkKey();
			WorldPos.CODEC.encodeStart(NbtOps.INSTANCE, key).result().ifPresent((t) -> tag.put("network_key", t));
		}
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
		if(tag.contains("network_key", Tag.TAG_COMPOUND))
		{
			CompoundTag keyTag = tag.getCompound("network_key");
			this.setNetwork(WorldPos.CODEC.parse(NbtOps.INSTANCE, keyTag).result().orElse(null));
		}
		else
		{
			this.setNetwork(null);
		}
	}
	
	@Override
	public void readClientNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		if(tag.contains("network_key", Tag.TAG_COMPOUND))
		{
			CompoundTag keyTag = tag.getCompound("network_key");
			networkKey = WorldPos.CODEC.parse(NbtOps.INSTANCE, keyTag).result();
		}
		else
		{
			networkKey = Optional.empty();
		}
	}
}
