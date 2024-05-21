package net.swedz.extended_industrialization.machines.components;

import aztech.modern_industrialization.inventory.MIFluidStorage;
import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.storage.StorageView;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.transaction.Transaction;
import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import it.unimi.dsi.fastutil.objects.Reference2LongOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.List;
import java.util.function.Supplier;

public final class SteamProductionComponent implements IComponent.ServerOnly
{
	private final MIFluidStorage fluidStorage;
	
	private final List<Fluid>  waterTypes;
	private final FluidVariant steamKey;
	
	private final Supplier<Long>    maxEuProduction;
	private final Supplier<Integer> waterToSteam;
	
	private final Reference2LongMap<Fluid> steamBuffer = new Reference2LongOpenHashMap<>();
	
	public SteamProductionComponent(MIFluidStorage fluidStorage, List<Fluid> waterTypes, FluidVariant steamKey, Supplier<Long> maxEuProduction, Supplier<Integer> waterToSteam)
	{
		this.fluidStorage = fluidStorage;
		this.waterTypes = waterTypes;
		this.steamKey = steamKey;
		this.maxEuProduction = maxEuProduction;
		this.waterToSteam = waterToSteam;
	}
	
	public FluidVariant findWater()
	{
		for(StorageView<FluidVariant> fluidStorage : fluidStorage)
		{
			if(fluidStorage.isResourceBlank())
			{
				continue;
			}
			FluidVariant fluid = fluidStorage.getResource();
			for(Fluid waterType : waterTypes)
			{
				if(fluid.isOf(waterType))
				{
					return fluid;
				}
			}
		}
		return FluidVariant.blank();
	}
	
	public FluidVariant tryMakeSteam()
	{
		int waterToSteamRate = this.waterToSteam.get();
		
		FluidVariant waterFluid = this.findWater();
		if(waterFluid.isBlank())
		{
			return waterFluid;
		}
		
		long steamToProduce = maxEuProduction.get();
		try (Transaction transaction = Transaction.openOuter())
		{
			long steamProducedSimulation;
			try (Transaction simulation = Transaction.openNested(transaction))
			{
				steamProducedSimulation = fluidStorage.insertAllSlot(steamKey, steamToProduce, simulation);
			}
			
			if(steamProducedSimulation > 0)
			{
				long waterToConsume = (steamProducedSimulation - steamBuffer.getLong(steamKey.getFluid()) + waterToSteamRate - 1) / waterToSteamRate;
				long waterConsumed = fluidStorage.extractAllSlot(waterFluid, waterToConsume, transaction);
				steamBuffer.mergeLong(steamKey.getFluid(), waterConsumed * waterToSteamRate, Long::sum);
				
				long steamProduced = fluidStorage.insertAllSlot(FluidVariant.of(steamKey.getFluid()), Math.min(steamToProduce, steamBuffer.getLong(steamKey.getFluid())), transaction);
				steamBuffer.mergeLong(steamKey.getFluid(), -steamProduced, Long::sum);
				
				transaction.commit();
			}
		}
		
		return waterFluid;
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
		CompoundTag buffer = new CompoundTag();
		for(Reference2LongMap.Entry<Fluid> entry : steamBuffer.reference2LongEntrySet())
		{
			if(entry.getLongValue() != 0)
			{
				buffer.putLong(entry.getKey().toString(), entry.getLongValue());
			}
		}
		tag.put("steamBuffer", buffer);
	}
	
	@Override
	public void readNbt(CompoundTag tag, boolean isUpgradingMachine)
	{
		CompoundTag buffer = tag.getCompound("steamBuffer");
		for(String key : buffer.getAllKeys())
		{
			Fluid fluid = BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(key));
			if(fluid != Fluids.EMPTY)
			{
				steamBuffer.put(fluid, buffer.getLong(key));
			}
		}
	}
}
