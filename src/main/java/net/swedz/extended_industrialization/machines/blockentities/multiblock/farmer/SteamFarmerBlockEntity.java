package net.swedz.extended_industrialization.machines.blockentities.multiblock.farmer;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.helper.SteamHelper;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import aztech.modern_industrialization.util.Simulation;
import net.swedz.extended_industrialization.EIMachines;
import net.swedz.extended_industrialization.machines.components.farmer.PlantingMode;
import net.swedz.extended_industrialization.machines.components.farmer.task.FarmerProcessRates;
import net.swedz.extended_industrialization.machines.components.farmer.task.FarmerTaskType;

public final class SteamFarmerBlockEntity extends FarmerBlockEntity
{
	private static final ShapeWrapper SHAPES = new ShapeWrapper(2)
			.withCasing(
					SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("bronze_plated_bricks"))),
					SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("bronze_machine_casing_pipe"))),
					EIMachines.Casings.BRONZE_PIPE
			)
			.complete();
	
	private static final FarmerProcessRates PROCESS_RATES = new FarmerProcessRates()
			.with(FarmerTaskType.TILLING, 1, 1)
			.with(FarmerTaskType.HYDRATING, 1, 1)
			.with(FarmerTaskType.HARVESTING, 1, 2 * 20)
			.with(FarmerTaskType.PLANTING, 1, 10);
	
	public SteamFarmerBlockEntity(BEP bep)
	{
		super(bep, "steam_farmer", 8, PlantingMode.AS_NEEDED, false, PROCESS_RATES, SHAPES);
	}
	
	public static void registerReiShapes()
	{
		int index = 0;
		for(ShapeTemplate shapeTemplate : SHAPES.shapeTemplates())
		{
			ReiMachineRecipes.registerMultiblockShape("steam_farmer", shapeTemplate, "" + index);
			index++;
		}
	}
	
	@Override
	public long consumeEu(long max)
	{
		return SteamHelper.consumeSteamEu(inventory.getFluidInputs(), max, Simulation.ACT);
	}
}
