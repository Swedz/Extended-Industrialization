package net.swedz.extended_industrialization.machines.blockentity.fluidharvesting;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.definition.FluidLike;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import net.swedz.extended_industrialization.machines.component.fluidharvesting.FluidHarvestingBehaviorCreator;
import net.swedz.tesseract.neoforge.compat.mi.helper.EuConsumerBehavior;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SteamFluidHarvestingMachineBlockEntity extends FluidHarvestingMachineBlockEntity
{
	private final MIInventory inventory;
	
	public SteamFluidHarvestingMachineBlockEntity(BEP bep, String blockName, long euCost, FluidHarvestingBehaviorCreator behaviorCreator, long capacity, FluidLike fluid)
	{
		super(bep, blockName, euCost, behaviorCreator);
		
		List<ConfigurableFluidStack> fluidStacks = Arrays.asList(
				ConfigurableFluidStack.lockedInputSlot(capacity, MIFluids.STEAM.asFluid()),
				ConfigurableFluidStack.lockedOutputSlot(capacity, fluid.asFluid())
		);
		SlotPositions fluidPositions = new SlotPositions.Builder().addSlot(21, 30).addSlot(OUTPUT_SLOT_X, OUTPUT_SLOT_Y).build();
		this.inventory = new MIInventory(Collections.emptyList(), fluidStacks, SlotPositions.empty(), fluidPositions);
		
		this.registerComponents(inventory);
	}
	
	@Override
	protected EuConsumerBehavior createEuConsumerBehavior()
	{
		return EuConsumerBehavior.steam(this);
	}
	
	@Override
	public MIInventory getInventory()
	{
		return inventory;
	}
}
