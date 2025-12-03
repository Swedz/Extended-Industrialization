package net.swedz.extended_industrialization.machines.component.tesla.network.transmitter;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.MachineComponent;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.util.Simulation;
import dev.technici4n.grandpower.api.EnergyStorageUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.swedz.extended_industrialization.machines.component.itemslot.TeslaTowerUpgradeComponent;
import net.swedz.extended_industrialization.machines.component.tesla.network.TeslaTransferLimits;
import net.swedz.tesseract.neoforge.api.WorldPos;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class TeslaTransmitterComponent implements MachineComponent.ServerOnly, TeslaTransmitter
{
	private final MachineBlockEntity machine;
	
	private final MIEnergyStorage energyStorage;
	
	private final Supplier<TeslaTransferLimits> limits;
	private final Supplier<WorldPos>            sourcePosition;
	
	private Optional<WorldPos> networkKey = Optional.empty();
	
	public TeslaTransmitterComponent(MachineBlockEntity machine, List<EnergyComponent> energyInputs, Supplier<TeslaTransferLimits> limits, Supplier<WorldPos> sourcePosition)
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
				long extracted = 0;
				for(var energyComponent : energyInputs)
				{
					long remaining = maxExtract - extracted;
					if(remaining == 0)
					{
						break;
					}
					extracted += energyComponent.consumeEu(remaining, simulate ? Simulation.SIMULATE : Simulation.ACT);
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
		
		this.limits = limits;
		this.sourcePosition = sourcePosition;
	}
	
	public TeslaTransmitterComponent(MachineBlockEntity machine, List<EnergyComponent> energyInputs, Supplier<TeslaTransferLimits> limits)
	{
		this(machine, energyInputs, limits, () -> new WorldPos(machine.getLevel(), machine.getBlockPos()));
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
	public WorldPos getSourcePosition()
	{
		return sourcePosition.get();
	}
	
	@Override
	public CableTier getCableTier()
	{
		return limits.get().getCableTier();
	}
	
	@Override
	public long getMaxTransfer()
	{
		return limits.get().getMaxTransfer();
	}
	
	@Override
	public int getMaxDistance()
	{
		return limits.get().getMaxDistance();
	}
	
	@Override
	public long getPassiveDrain()
	{
		return limits.get().getPassiveDrain();
	}
	
	@Override
	public boolean isInterdimensional()
	{
		return machine.components.mapOrDefault(TeslaTowerUpgradeComponent.class, TeslaTowerUpgradeComponent::isInterdimensional, false);
	}
	
	@Override
	public long transmitEnergy(long maxTransmit)
	{
		if(this.hasNetwork())
		{
			var network = this.getNetwork();
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
