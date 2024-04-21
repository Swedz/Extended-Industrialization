package net.swedz.miextended.machines.blockentities.multiblock.farmer;

import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.MIIdentifier;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.helper.SteamHelper;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import aztech.modern_industrialization.util.Simulation;
import net.swedz.miextended.machines.components.farmer.PlantingMode;
import net.swedz.miextended.mi.hook.MIMachineHook;

public final class SteamFarmerBlockEntity extends FarmerBlockEntity
{
	private static final ShapeWrapper SHAPES = new ShapeWrapper(2)
			.withCasing(
					SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(new MIIdentifier("bronze_plated_bricks"))),
					SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(new MIIdentifier("bronze_machine_casing_pipe"))),
					MIMachineHook.Casings.BRONZE_PIPE
			)
			.complete();
	
	public SteamFarmerBlockEntity(BEP bep)
	{
		super(bep, "steam_farmer", 8, PlantingMode.AS_NEEDED, false, 1, SHAPES);
	}
	
	public static void registerReiShapes()
	{
		for(ShapeTemplate shapeTemplate : SHAPES.shapeTemplates())
		{
			ReiMachineRecipes.registerMultiblockShape("steam_farmer", shapeTemplate);
		}
	}
	
	@Override
	public long consumeEu(long max)
	{
		return SteamHelper.consumeSteamEu(inventory.getFluidInputs(), max, Simulation.ACT);
	}
}
