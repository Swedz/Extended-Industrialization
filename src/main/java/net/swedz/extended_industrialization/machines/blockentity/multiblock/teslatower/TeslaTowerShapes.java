package net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import net.minecraft.world.level.block.Blocks;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIDataMaps;
import net.swedz.extended_industrialization.datamap.TeslaTowerTierData;
import net.swedz.extended_industrialization.machines.tieredshapes.MultiblockTieredShapes;

import java.util.Comparator;

public final class TeslaTowerShapes extends MultiblockTieredShapes<TeslaTowerTier, TeslaTowerTierData>
{
	TeslaTowerShapes()
	{
		super(
				EI.id("tesla_tower"),
				EIDataMaps.TESLA_TOWER_TIER,
				Comparator.comparing(TeslaTowerTier::cableTier)
		);
	}
	
	private static String[][] formatPattern(String[][] input)
	{
		int rows = input[0].length;
		int columns = input.length;
		
		String[][] result = new String[rows][columns];
		
		for(int row = 0; row < rows; row++)
		{
			for(int column = 0; column < columns; column++)
			{
				result[row][column] = input[column][row];
			}
		}
		
		return result;
	}
	
	private String[][] pattern()
	{
		String[] layerPipe = {
				"       ",
				"       ",
				"       ",
				"   P   ",
				"       ",
				"       ",
				"       "
		};
		String[] layerPipeWithCoil = {
				"       ",
				"  WWW  ",
				" W   W ",
				" W P W ",
				" W   W ",
				"  WWW  ",
				"       "
		};
		String[] ballEnd = {
				"       ",
				"       ",
				"  WWW  ",
				"  WWW  ",
				"  WWW  ",
				"       ",
				"       "
		};
		String[] ballMiddle = {
				"       ",
				"  WWW  ",
				" WWWWW ",
				" WWWWW ",
				" WWWWW ",
				"  WWW  ",
				"       "
		};
		return new String[][]{
				{
						" SSSSS ",
						"SSCCCSS",
						"SCCCCCS",
						"SCCPCCS",
						"SCCCCCS",
						"SSCCCSS",
						" SS#SS "
				},
				{
						"       ",
						"  CCC  ",
						" C   C ",
						" C P C ",
						" C   C ",
						"  CCC  ",
						"       "
				},
				layerPipe,
				layerPipe,
				layerPipeWithCoil,
				layerPipe,
				layerPipeWithCoil,
				layerPipe,
				layerPipeWithCoil,
				layerPipe,
				layerPipeWithCoil,
				layerPipe,
				ballEnd,
				ballMiddle,
				ballMiddle,
				ballMiddle,
				ballEnd
		};
	}
	
	@Override
	protected void invalidateShapeTemplates()
	{
		shapeTemplates = new ShapeTemplate[tiers.size()];
		
		for(int i = 0; i < tiers.size(); i++)
		{
			TeslaTowerTier tier = tiers.get(i);
			
			ShapeTemplate.LayeredBuilder builder = new ShapeTemplate.LayeredBuilder(
					MachineCasings.CLEAN_STAINLESS_STEEL,
					formatPattern(this.pattern())
			);
			builder.key('S', SimpleMember.forBlockId(MI.id("clean_stainless_steel_machine_casing")), new HatchFlags.Builder().with(HatchType.ENERGY_INPUT).build());
			builder.key('P', SimpleMember.forBlockId(MI.id("stainless_steel_machine_casing_pipe")), HatchFlags.NO_HATCH);
			builder.key('C', SimpleMember.forBlockId(tier.blockId()), HatchFlags.NO_HATCH);
			builder.key('W', SimpleMember.forBlock(() -> Blocks.WHITE_WOOL), HatchFlags.NO_HATCH);
			shapeTemplates[i] = builder.build();
		}
	}
}
