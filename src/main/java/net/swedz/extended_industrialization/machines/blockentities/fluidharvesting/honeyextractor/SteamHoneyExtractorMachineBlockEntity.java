package net.swedz.extended_industrialization.machines.blockentities.fluidharvesting.honeyextractor;

import aztech.modern_industrialization.machines.BEP;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.machines.blockentities.fluidharvesting.FluidHarvestingBehavior;
import net.swedz.extended_industrialization.machines.blockentities.fluidharvesting.SteamFluidHarvestingMachineBlockEntity;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;

public final class SteamHoneyExtractorMachineBlockEntity extends SteamFluidHarvestingMachineBlockEntity
{
	private final FluidHarvestingBehavior behavior;
	
	public SteamHoneyExtractorMachineBlockEntity(BEP bep, boolean bronze)
	{
		super(
				bep, bronze ? "bronze_honey_extractor" : "steel_honey_extractor", bronze ? 1 : 2,
				bronze, FluidType.BUCKET_VOLUME * (bronze ? 8 : 16), EIFluids.HONEY
		);
		
		this.behavior = HoneyExtractorBehavior.steam(this);
	}
	
	@Override
	public FluidHarvestingBehavior getFluidHarvestingBehavior()
	{
		return behavior;
	}
}
