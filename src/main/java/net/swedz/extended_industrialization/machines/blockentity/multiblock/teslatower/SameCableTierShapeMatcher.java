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
import java.util.function.Consumer;

public class SameCableTierShapeMatcher extends ShapeMatcher
{
	protected final Consumer<Boolean> mismatchingHatchesCallback;
	
	public SameCableTierShapeMatcher(Level level, BlockPos controllerPos, Direction controllerDirection, ShapeTemplate template, Consumer<Boolean> mismatchingHatchesCallback)
	{
		super(level, controllerPos, controllerDirection, template);
		this.mismatchingHatchesCallback = mismatchingHatchesCallback;
	}
	
	@Override
	protected boolean checkRematch(Level world)
	{
		boolean hasMismatchingHatches = false;
		Set<CableTier> tiers = Sets.newHashSet();
		for(HatchBlockEntity hatch : this.getMatchedHatches())
		{
			if(hatch instanceof EnergyHatch energyHatch)
			{
				tiers.add(energyHatch.getCableTier());
				if(tiers.size() > 1)
				{
					hasMismatchingHatches = true;
					break;
				}
			}
		}
		mismatchingHatchesCallback.accept(hasMismatchingHatches);
		return !hasMismatchingHatches;
	}
}
