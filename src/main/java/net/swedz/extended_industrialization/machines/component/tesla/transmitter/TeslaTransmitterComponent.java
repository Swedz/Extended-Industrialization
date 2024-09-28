package net.swedz.extended_industrialization.machines.component.tesla.transmitter;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.util.Simulation;
import dev.technici4n.grandpower.api.EnergyStorageUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.swedz.extended_industrialization.api.WorldPos;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetwork;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class TeslaTransmitterComponent implements IComponent.ServerOnly, TeslaTransmitter
{
	private final MachineBlockEntity machine;
	
	private final MIEnergyStorage energyStorage;
	
	private final Supplier<CableTier> cableTier;
	
	private Optional<WorldPos> networkKey = Optional.empty();
	
	public TeslaTransmitterComponent(MachineBlockEntity machine, List<EnergyComponent> energyInputs, Supplier<CableTier> cableTier)
	{
		this.machine = machine;
		
		// TODO some kind of helper method for this would be nice
		energyStorage = new MIEnergyStorage.NoInsert()
		{
			@Override
			public boolean canConnect(CableTier cableTier)
			{
				return false;
			}
			
			@Override
			public long extract(long maxExtract, boolean simulate)
			{
				long remaining = maxExtract;
				long extracted = 0;
				for(EnergyComponent energyComponent : energyInputs)
				{
					if(remaining == 0)
					{
						break;
					}
					extracted += energyComponent.consumeEu(remaining, simulate ? Simulation.SIMULATE : Simulation.ACT);
					remaining -= extracted;
				}
				return extracted;
			}
			
			@Override
			public long getAmount()
			{
				return energyInputs.stream().mapToLong(EnergyComponent::getEu).sum();
			}
			
			@Override
			public long getCapacity()
			{
				return energyInputs.stream().mapToLong(EnergyComponent::getCapacity).sum();
			}
			
			@Override
			public boolean canExtract()
			{
				return true;
			}
		};
		
		this.cableTier = cableTier;
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
		networkKey = Optional.ofNullable(key);
	}
	
	@Override
	public WorldPos getPosition()
	{
		return new WorldPos(machine.getLevel(), machine.getBlockPos());
	}
	
	@Override
	public CableTier getCableTier()
	{
		return cableTier.get();
	}
	
	@Override
	public long transmitEnergy(long maxTransmit)
	{
		if(this.hasNetwork())
		{
			TeslaNetwork network = this.getNetwork();
			return EnergyStorageUtil.move(energyStorage, network, maxTransmit);
		}
		return 0;
	}
	
	@Override
	public long extractEnergy(long maxExtract, boolean simulate)
	{
		return energyStorage.extract(maxExtract, simulate);
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
	}
}
