package net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.blockentities.hatches.EnergyHatch;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class SameCableTierShapeMatcher extends ShapeMatcher
{
	public SameCableTierShapeMatcher(Level level, BlockPos controllerPos, Direction controllerDirection, ShapeTemplate template)
	{
		super(level, controllerPos, controllerDirection, template);
	}
	
	@Override
	public void rematch(Level level)
	{
		this.unlinkHatches();
		matchSuccessful = true;
		
		for(BlockPos pos : simpleMembers.keySet())
		{
			int originalHatchCount = matchedHatches.size();
			if(!this.matches(pos, level, matchedHatches))
			{
				matchSuccessful = false;
			}
			else if(originalHatchCount != matchedHatches.size())
			{
				CableTier cableTier = null;
				for(HatchBlockEntity hatch : matchedHatches)
				{
					if(hatch instanceof EnergyHatch energyHatch)
					{
						if(cableTier == null)
						{
							cableTier = energyHatch.getCableTier();
						}
						if(cableTier != energyHatch.getCableTier())
						{
							matchSuccessful = false;
							break;
						}
					}
				}
			}
		}
		
		if(!matchSuccessful)
		{
			matchedHatches.clear();
		}
		else
		{
			for(HatchBlockEntity hatch : matchedHatches)
			{
				hatch.link(template.hatchCasing);
			}
		}
		
		needsRematch = false;
	}
}
