package net.swedz.extended_industrialization.machines.blockentity.multiblock.farmer;

import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.swedz.extended_industrialization.machines.component.farmer.FarmerComponent;

public final class FarmerShapeMatcher extends ShapeMatcher
{
	private final FarmerComponent farmer;
	
	public FarmerShapeMatcher(Level world, BlockPos controllerPos, Direction controllerDirection, ShapeTemplate template,
							  FarmerComponent farmer)
	{
		super(world, controllerPos, controllerDirection, template);
		this.farmer = farmer;
	}
	
	@Override
	public void registerListeners(Level world)
	{
		super.registerListeners(world);
		farmer.registerListeners(world, this);
	}
	
	@Override
	public void unregisterListeners(Level world)
	{
		super.unregisterListeners(world);
		farmer.unregisterListeners(world, this);
	}
}
