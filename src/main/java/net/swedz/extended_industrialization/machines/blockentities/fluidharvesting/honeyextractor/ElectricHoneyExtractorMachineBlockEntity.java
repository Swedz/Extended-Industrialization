package net.swedz.extended_industrialization.machines.blockentities.fluidharvesting.honeyextractor;

import aztech.modern_industrialization.machines.BEP;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.machines.blockentities.fluidharvesting.ElectricFluidHarvestingMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentities.fluidharvesting.FluidHarvestingBehavior;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;

public final class ElectricHoneyExtractorMachineBlockEntity extends ElectricFluidHarvestingMachineBlockEntity
{
	private final FluidHarvestingBehavior behavior;
	
	public ElectricHoneyExtractorMachineBlockEntity(BEP bep)
	{
		super(
				bep, "electric_honey_extractor", 4,
				FluidType.BUCKET_VOLUME * 32, EIFluids.HONEY
		);
		
		this.behavior = HoneyExtractorBehavior.electric(this, energy, redstoneControl);
	}
	
	@Override
	public FluidHarvestingBehavior getFluidHarvestingBehavior()
	{
		return behavior;
	}
}
