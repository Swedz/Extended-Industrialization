package net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.blockentities.hatches.EnergyHatch;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.Set;

public class SameCableTierShapeMatcher extends ShapeMatcher
{
	protected boolean hasMismatchingHatches;
	
	public SameCableTierShapeMatcher(Level level, BlockPos controllerPos, Direction controllerDirection, ShapeTemplate template)
	{
		super(level, controllerPos, controllerDirection, template);
	}
	
	public boolean hasMismatchingHatches()
	{
		return hasMismatchingHatches;
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
				Set<CableTier> tiers = Sets.newHashSet();
				for(HatchBlockEntity hatch : matchedHatches)
				{
					if(hatch instanceof EnergyHatch energyHatch)
					{
						tiers.add(energyHatch.getCableTier());
						if(tiers.size() > 1)
						{
							hasMismatchingHatches = true;
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
			hasMismatchingHatches = false;
			for(HatchBlockEntity hatch : matchedHatches)
			{
				hatch.link(template.hatchCasing);
			}
		}
		
		needsRematch = false;
	}
}
