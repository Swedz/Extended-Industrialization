package net.swedz.extended_industrialization.machines.blockentities.fluidharvesting;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.machines.MachineBlockEntity;

import java.util.List;

public interface FluidHarvestingBehavior
{
	MachineBlockEntity getMachineBlockEntity();
	
	default ConfigurableFluidStack getMachineBlockFluidStack()
	{
		List<ConfigurableFluidStack> fluidStacks = this.getMachineBlockEntity().getInventory().getFluidStacks();
		return fluidStacks.get(fluidStacks.size() - 1);
	}
	
	int totalPumpingTicks();
	
	long consumeEu(long max);
	
	float getOutputMultiplier();
	
	boolean canOperate();
	
	void operate();
}
