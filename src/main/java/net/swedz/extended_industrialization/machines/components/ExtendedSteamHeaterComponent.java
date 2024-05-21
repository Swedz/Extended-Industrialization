package net.swedz.extended_industrialization.machines.components;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.MIFluidStorage;
import aztech.modern_industrialization.machines.components.TemperatureComponent;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant;
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

public final class ExtendedSteamHeaterComponent extends TemperatureComponent
{
	private static final int STEAM_TO_WATER = 16;
	
	public final double temperatureToWork;
	
	private final Supplier<Long> maxEuProduction;
	private final Supplier<Long> euPerDegree;
	
	public final boolean acceptHighPressure;
	public final boolean acceptLowPressure;
	
	public final boolean requiresContinuousOperation;
	
	public static final double INPUT_ENERGY_RATIO_FOR_STARTUP = 0.8;
	
	private final Reference2LongMap<Fluid> steamBuffer = new Reference2LongOpenHashMap<>();
	
	public ExtendedSteamHeaterComponent(double temperatureMax, double temperatureToWork, Supplier<Long> maxEuProduction, Supplier<Long> euPerDegree,
										boolean acceptLowPressure, boolean acceptHighPressure, boolean requiresContinuousOperation)
	{
		super(temperatureMax);
		this.temperatureToWork = temperatureToWork;
		this.maxEuProduction = maxEuProduction;
		this.euPerDegree = euPerDegree;
		this.acceptLowPressure = acceptLowPressure;
		this.acceptHighPressure = acceptHighPressure;
		this.requiresContinuousOperation = requiresContinuousOperation;
	}
	
	public ExtendedSteamHeaterComponent(double temperatureMax, double temperatureToWork, Supplier<Long> maxEuProduction, Supplier<Long> euPerDegree)
	{
		this(temperatureMax, temperatureToWork, maxEuProduction, euPerDegree, true, false, false);
	}
	
	public ExtendedSteamHeaterComponent(double temperatureMax, double temperatureToWork, Supplier<Long> maxEuProduction)
	{
		this(temperatureMax, temperatureToWork, maxEuProduction, () -> 0L);
	}
	
	public long getMaxEuProduction()
	{
		return maxEuProduction.get();
	}
	
	public long getEuPerDegree()
	{
		return euPerDegree.get();
	}
	
	public boolean isWorking()
	{
		return this.getTemperature() > temperatureToWork;
	}
	
	public float getWorkingTemperatureFullness()
	{
		return (float) ((this.getTemperature() - temperatureToWork) / (temperatureMax - temperatureToWork));
	}
	
	public double getTemperatureFullness()
	{
		return this.getTemperature() / temperatureMax;
	}
	
	public double tick(List<ConfigurableFluidStack> fluidInputs, List<ConfigurableFluidStack> fluidOutputs)
	{
		double euProducedLowPressure = 0;
		if(acceptLowPressure)
		{
			euProducedLowPressure = this.tryMakeSteam(
					fluidInputs, fluidOutputs,
					Fluids.WATER, MIFluids.STEAM.asFluid(),
					1
			);
			if(euProducedLowPressure == 0)
			{
				euProducedLowPressure = this.tryMakeSteam(
						fluidInputs, fluidOutputs,
						MIFluids.HEAVY_WATER.asFluid(), MIFluids.HEAVY_WATER_STEAM.asFluid(),
						1
				);
			}
		}
		
		double euProducedHighPressure = 0;
		if(acceptHighPressure)
		{
			euProducedHighPressure = this.tryMakeSteam(
					fluidInputs, fluidOutputs,
					MIFluids.HIGH_PRESSURE_WATER.asFluid(), MIFluids.HIGH_PRESSURE_STEAM.asFluid(),
					8
			);
			if(euProducedHighPressure == 0)
			{
				euProducedHighPressure = this.tryMakeSteam(
						fluidInputs, fluidOutputs,
						MIFluids.HIGH_PRESSURE_HEAVY_WATER.asFluid(), MIFluids.HIGH_PRESSURE_HEAVY_WATER_STEAM.asFluid(),
						8
				);
			}
		}
		
		double totalEuProduced = euProducedLowPressure + euProducedHighPressure;
		
		long euPerDegree = this.getEuPerDegree();
		if(requiresContinuousOperation && euPerDegree > 0)
		{
			this.decreaseTemperature(INPUT_ENERGY_RATIO_FOR_STARTUP * (this.getMaxEuProduction() - totalEuProduced) / euPerDegree);
		}
		
		return totalEuProduced;
	}
	
	private double tryMakeSteam(List<ConfigurableFluidStack> input, List<ConfigurableFluidStack> output, Fluid water, Fluid steam, int euPerSteamMb)
	{
		return this.tryMakeSteam(new MIFluidStorage(input), new MIFluidStorage(output), water, steam, euPerSteamMb);
	}
	
	private double tryMakeSteam(MIFluidStorage input, MIFluidStorage output, Fluid water, Fluid steam, int euPerSteamMb)
	{
		FluidVariant waterKey = FluidVariant.of(water);
		FluidVariant steamKey = FluidVariant.of(steam);
		
		if(this.isWorking())
		{
			long steamProduction = (long) Math.ceil(this.getWorkingTemperatureFullness() * this.getMaxEuProduction() / euPerSteamMb);
			
			try (Transaction transaction = Transaction.openOuter())
			{
				long inserted;
				try (Transaction simulation = Transaction.openNested(transaction))
				{
					inserted = output.insertAllSlot(steamKey, steamProduction, simulation);
				}
				if(inserted > 0)
				{
					long waterToUse = (inserted - steamBuffer.getLong(steam) + STEAM_TO_WATER - 1) / STEAM_TO_WATER;
					long extracted = input.extractAllSlot(waterKey, waterToUse, transaction);
					steamBuffer.mergeLong(steam, extracted * STEAM_TO_WATER, Long::sum);
					
					long producedSteam = output.insertAllSlot(steamKey, Math.min(steamProduction, steamBuffer.getLong(steam)), transaction);
					steamBuffer.mergeLong(steam, -producedSteam, Long::sum);
					
					double euProduced = producedSteam * euPerSteamMb;
					long euPerDegree = this.getEuPerDegree();
					if(euPerDegree > 0)
					{
						this.decreaseTemperature(euProduced / euPerDegree);
					}
					transaction.commit();
					return euProduced;
				}
			}
		}
		return 0;
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
		super.writeNbt(tag);
		
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
		super.readNbt(tag, isUpgradingMachine);
		
		CompoundTag steamBuffer = tag.getCompound("steamBuffer");
		for(String key : steamBuffer.getAllKeys())
		{
			Fluid fluid = BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(key));
			if(fluid != Fluids.EMPTY)
			{
				this.steamBuffer.put(fluid, steamBuffer.getLong(key));
			}
		}
	}
}