package net.swedz.miextended.machines.blockentities.multiblock.farmer;

import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.helper.SteamHelper;
import aztech.modern_industrialization.util.Simulation;
import net.swedz.miextended.machines.components.farmer.PlantingMode;

public final class SteamFarmerBlockEntity extends FarmerBlockEntity
{
	public SteamFarmerBlockEntity(BEP bep)
	{
		super(bep, "steam_farmer", 8, PlantingMode.AS_NEEDED, false, 1);
	}
	
	@Override
	public long consumeEu(long max)
	{
		return SteamHelper.consumeSteamEu(inventory.getFluidInputs(), max, Simulation.ACT);
	}
}
