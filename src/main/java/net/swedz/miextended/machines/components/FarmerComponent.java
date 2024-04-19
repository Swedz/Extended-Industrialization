package net.swedz.miextended.machines.components;

import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public final class FarmerComponent implements IComponent
{
	private List<BlockPos> dirtPositions;
	
	public void fromOffsets(BlockPos controllerPos, Direction controllerDirection, List<BlockPos> offsets)
	{
		List<BlockPos> dirtPositions = new ArrayList<>(offsets.size());
		for(BlockPos offset : offsets)
		{
			BlockPos worldPos = ShapeMatcher.toWorldPos(controllerPos, controllerDirection, offset);
			dirtPositions.add(worldPos);
		}
		this.dirtPositions = dirtPositions;
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
	}
	
	@Override
	public void readNbt(CompoundTag tag, boolean isUpgradingMachine)
	{
	}
}
