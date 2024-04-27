package net.swedz.extended_industrialization.machines.blockentities.fluidharvesting;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.util.Simulation;
import net.swedz.extended_industrialization.api.EuConsumerBehavior;

import java.util.List;

public abstract class FluidHarvestingBehavior
{
	protected final MachineBlockEntity machine;
	
	protected final EuConsumerBehavior euBehavior;
	
	protected final int   totalPumpingTicks;
	protected final float outputMultiplier;
	
	protected FluidHarvestingBehavior(MachineBlockEntity machine, EuConsumerBehavior euBehavior, int totalPumpingTicks, float outputMultiplier)
	{
		this.machine = machine;
		this.euBehavior = euBehavior;
		this.totalPumpingTicks = totalPumpingTicks;
		this.outputMultiplier = outputMultiplier;
	}
	
	public MachineBlockEntity getMachineBlockEntity()
	{
		return machine;
	}
	
	public ConfigurableFluidStack getMachineBlockFluidStack()
	{
		List<ConfigurableFluidStack> fluidStacks = this.getMachineBlockEntity().getInventory().getFluidStacks();
		return fluidStacks.get(fluidStacks.size() - 1);
	}
	
	public int totalPumpingTicks()
	{
		return totalPumpingTicks;
	}
	
	public long consumeEu(long max)
	{
		return euBehavior.consumeEu(max, Simulation.ACT);
	}
	
	public float getOutputMultiplier()
	{
		return outputMultiplier;
	}
	
	public abstract boolean canOperate();
	
	public abstract void operate();
}
