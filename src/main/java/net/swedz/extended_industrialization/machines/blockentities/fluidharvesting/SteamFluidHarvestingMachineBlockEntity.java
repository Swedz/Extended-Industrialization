package net.swedz.extended_industrialization.machines.blockentities.fluidharvesting;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.definition.FluidLike;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class SteamFluidHarvestingMachineBlockEntity extends FluidHarvestingMachineBlockEntity
{
	protected final MIInventory inventory;
	
	protected final boolean bronze;
	
	public SteamFluidHarvestingMachineBlockEntity(BEP bep, String blockName, long euCost, boolean bronze, long capacity, FluidLike fluid)
	{
		super(bep, blockName, euCost);
		
		List<ConfigurableFluidStack> fluidStacks = Arrays.asList(
				ConfigurableFluidStack.lockedInputSlot(capacity, MIFluids.STEAM.asFluid()),
				ConfigurableFluidStack.lockedOutputSlot(capacity, fluid.asFluid())
		);
		SlotPositions fluidPositions = new SlotPositions.Builder().addSlot(21, 30).addSlot(OUTPUT_SLOT_X, OUTPUT_SLOT_Y).build();
		this.inventory = new MIInventory(Collections.emptyList(), fluidStacks, SlotPositions.empty(), fluidPositions);
		
		this.bronze = bronze;
	}
	
	@Override
	public MIInventory getInventory()
	{
		return inventory;
	}
}
