package net.swedz.extended_industrialization.api;

import aztech.modern_industrialization.definition.FluidLike;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

public final class MachineInventoryHelper
{
	public static boolean isActuallyJustAir(ConfigurableItemStack item)
	{
		return item.isLockedTo(Items.AIR) || (item.getLockedInstance() == null && item.isEmpty());
	}
	
	public static ItemStack toActualItemStack(ConfigurableItemStack item)
	{
		return item.getLockedInstance() != null && item.isEmpty() ? item.getLockedInstance().getDefaultInstance() : item.getResource().toStack();
	}
	
	public static boolean hasFluid(List<ConfigurableFluidStack> fluids, FluidLike fluid, int amount)
	{
		return fluids.stream().anyMatch((f) -> f.getResource().getFluid() == fluid.asFluid() && f.getAmount() >= amount);
	}
	
	public static boolean hasFluid(List<ConfigurableFluidStack> fluids, Fluid fluid, int amount)
	{
		return hasFluid(fluids, () -> fluid, amount);
	}
	
	public static long consumeFluid(List<ConfigurableFluidStack> fluids, FluidLike fluid, long max, Simulation simulation)
	{
		if(max <= 0)
		{
			throw new IllegalArgumentException("May not consume 0 fluid");
		}
		
		long totalRemoved = 0;
		
		for(ConfigurableFluidStack fluidStack : fluids)
		{
			if(fluidStack.getResource().getFluid() == fluid.asFluid())
			{
				long amount = fluidStack.getAmount();
				long remove = Math.min(max, amount);
				if(simulation.isActing())
				{
					fluidStack.decrement(remove);
				}
				max -= remove;
				totalRemoved += remove;
			}
		}
		
		return totalRemoved;
	}
	
	public static long consumeFluid(List<ConfigurableFluidStack> fluids, FluidLike fluid, long max, boolean simulation)
	{
		return consumeFluid(fluids, fluid, max, simulation ? Simulation.SIMULATE : Simulation.ACT);
	}
	
	public static long consumeFluid(List<ConfigurableFluidStack> fluids, Fluid fluid, long max, Simulation simulation)
	{
		return consumeFluid(fluids, () -> fluid, max, simulation);
	}
	
	public static long consumeFluid(List<ConfigurableFluidStack> fluids, Fluid fluid, long max, boolean simulate)
	{
		return consumeFluid(fluids, fluid, max, simulate ? Simulation.SIMULATE : Simulation.ACT);
	}
}
