package net.swedz.extended_industrialization.machines.component;

import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.IComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;
import java.util.function.Supplier;

public class TeslaNetworkComponent implements IComponent.ServerOnly
{
	private final Optional<Supplier<MIEnergyStorage>> insertable;
	private final Optional<Supplier<MIEnergyStorage>> extractable;
	
	public TeslaNetworkComponent(Optional<Supplier<MIEnergyStorage>> insertable,
								 Optional<Supplier<MIEnergyStorage>> extractable)
	{
		this.insertable = insertable;
		this.extractable = extractable;
	}
	
	public boolean canInsert()
	{
		return insertable.isPresent();
	}
	
	public MIEnergyStorage insertable()
	{
		return insertable.orElseThrow().get();
	}
	
	public boolean canExtract()
	{
		return extractable.isPresent();
	}
	
	public MIEnergyStorage extractable()
	{
		return extractable.orElseThrow().get();
	}
	
	public void tick()
	{
		// TODO
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
