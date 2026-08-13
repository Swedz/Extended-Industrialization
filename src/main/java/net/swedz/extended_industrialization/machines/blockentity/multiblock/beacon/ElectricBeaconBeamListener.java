package net.swedz.extended_industrialization.machines.blockentity.multiblock.beacon;

import aztech.modern_industrialization.machines.multiblocks.world.ChunkEventListener;
import net.minecraft.core.BlockPos;
import net.swedz.extended_industrialization.machines.component.beacon.BeaconBeamComponent;

public final class ElectricBeaconBeamListener implements ChunkEventListener
{
	private final BlockPos            controllerPos;
	private final BeaconBeamComponent beam;
	
	public ElectricBeaconBeamListener(
			BlockPos controllerPos,
			BeaconBeamComponent beam
	)
	{
		this.controllerPos = controllerPos;
		this.beam = beam;
	}
	
	@Override
	public void onBlockUpdate(BlockPos pos)
	{
		if(pos.getX() == controllerPos.getX() &&
		   pos.getY() > controllerPos.getY() &&
		   pos.getZ() == controllerPos.getZ())
		{
			beam.requestRematch();
		}
	}
	
	@Override
	public void onUnload()
	{
		beam.requestRematch();
	}
	
	@Override
	public void onLoad()
	{
		beam.requestRematch();
	}
}
