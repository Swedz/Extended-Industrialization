package net.swedz.extended_industrialization.machines.blockentity.multiblock.beacon;

import aztech.modern_industrialization.machines.components.ShapeValidComponent;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.world.ChunkEventListeners;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.swedz.extended_industrialization.machines.component.beacon.BeaconBeamComponent;

public final class ElectricBeaconShapeMatcher extends ShapeMatcher
{
	private final ElectricBeaconBeamListener listener;
	
	public ElectricBeaconShapeMatcher(
			Level world,
			BlockPos controllerPos,
			Direction controllerDirection,
			ShapeTemplate template,
			ShapeValidComponent shapeValid,
			BeaconBeamComponent beam
	)
	{
		super(world, controllerPos, controllerDirection, template, shapeValid);
		
		listener = new ElectricBeaconBeamListener(controllerPos, beam);
	}
	
	public ElectricBeaconBeamListener getBeamListener()
	{
		return listener;
	}
	
	@Override
	public void registerListeners(Level world)
	{
		super.registerListeners(world);
		
		ChunkEventListeners.listeners.add(world, new ChunkPos(controllerPos), listener);
	}
	
	@Override
	public void unregisterListeners(Level world)
	{
		super.unregisterListeners(world);
		
		ChunkEventListeners.listeners.remove(world, new ChunkPos(controllerPos), listener);
	}
}
