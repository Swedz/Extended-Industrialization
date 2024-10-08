package net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIBlocks;
import net.swedz.extended_industrialization.EIDataMaps;
import net.swedz.extended_industrialization.datamap.TeslaTowerTierData;
import net.swedz.extended_industrialization.machines.tieredshapes.DataMapMultiblockTieredShapes;

import java.util.Comparator;

public final class TeslaTowerShapes extends DataMapMultiblockTieredShapes<TeslaTowerTier, TeslaTowerTierData>
{
	TeslaTowerShapes()
	{
		super(
				EI.id("tesla_tower"),
				Comparator.comparing(TeslaTowerTier::maxDistance),
				EIDataMaps.TESLA_TOWER_TIER
		);
	}
	
	private String[][] pattern()
	{
		String[] layerWinding = {
				"       ",
				"       ",
				"       ",
				"   w   ",
				"       ",
				"       ",
				"       "
		};
		String[] layerRingedWinding = {
				"       ",
				"  TTT  ",
				" T P T ",
				" TPwPT ",
				" T P T ",
				"  TTT  ",
				"       "
		};
		String[] ballEnd = {
				"       ",
				"       ",
				"  TTT  ",
				"  TTT  ",
				"  TTT  ",
				"       ",
				"       "
		};
		String[] ballMiddle = {
				"       ",
				"  TTT  ",
				" TTTTT ",
				" TTTTT ",
				" TTTTT ",
				"  TTT  ",
				"       "
		};
		return new String[][]{
				{
						" SSSSS ",
						"SSWWWSS",
						"SWWWWWS",
						"SWWwWWS",
						"SWWWWWS",
						"SSWWWSS",
						" SS#SS "
				},
				{
						"       ",
						"  WWW  ",
						" W   W ",
						" W w W ",
						" W   W ",
						"  WWW  ",
						"       "
				},
				layerWinding,
				layerWinding,
				layerRingedWinding,
				layerWinding,
				layerRingedWinding,
				layerWinding,
				layerRingedWinding,
				layerWinding,
				layerRingedWinding,
				layerWinding,
				ballEnd,
				ballMiddle,
				ballMiddle,
				ballMiddle,
				ballEnd
		};
	}
	
	@Override
	protected void buildShapeTemplates(ShapeTemplate[] shapeTemplates)
	{
		for(int i = 0; i < tiers.size(); i++)
		{
			TeslaTowerTier tier = tiers.get(i);
			
			ShapeTemplate.LayeredBuilder builder = new ShapeTemplate.LayeredBuilder(
					MachineCasings.CLEAN_STAINLESS_STEEL,
					layersConvertFromVertical(this.pattern())
			);
			builder.key('S', SimpleMember.forBlockId(MI.id("clean_stainless_steel_machine_casing")), new HatchFlags.Builder().with(HatchType.ENERGY_INPUT).build());
			builder.key('P', SimpleMember.forBlockId(MI.id("stainless_steel_machine_casing_pipe")), HatchFlags.NO_HATCH);
			builder.key('T', SimpleMember.forBlock(EIBlocks.POLISHED_STAINLESS_STEEL_CASING), HatchFlags.NO_HATCH);
			builder.key('W', SimpleMember.forBlockId(tier.blockId()), HatchFlags.NO_HATCH);
			builder.key('w', SimpleMember.forBlockId(tier.blockId()), HatchFlags.NO_HATCH); // TODO secondary winding?
			shapeTemplates[i] = builder.build();
		}
	}
}
