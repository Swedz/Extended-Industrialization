package net.swedz.extended_industrialization.machines.component.farmer.block;

import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class FarmerBlockMap implements Iterable<FarmerTile>
{
	private List<FarmerTile> tiles         = List.of();
	private List<BlockPos>   dirtPositions = List.of();
	
	public List<FarmerTile> tiles()
	{
		return tiles;
	}
	
	@Override
	public Iterator<FarmerTile> iterator()
	{
		return tiles.iterator();
	}
	
	public boolean containsDirtAt(BlockPos pos)
	{
		return dirtPositions.contains(pos);
	}
	
	public void fromOffsets(Level level, BlockPos controllerPos, Direction controllerDirection, List<BlockPos> offsets)
	{
		var centerPos = controllerPos.relative(controllerDirection.getOpposite());
		
		List<FarmerTile> tiles = new ArrayList<>(offsets.size());
		List<BlockPos> dirtPositions = new ArrayList<>(offsets.size());
		
		int minX = offsets.stream().mapToInt(Vec3i::getX).min().orElseThrow();
		
		for(var offset : offsets)
		{
			var dirtPos = ShapeMatcher.toWorldPos(controllerPos, controllerDirection, offset);
			var cropPos = dirtPos.above();
			
			int line = Math.abs(dirtPos.getX() - minX);
			int quadrant = quadrant(dirtPos, centerPos);
			
			var dirt = new FarmerBlock(dirtPos);
			dirt.updateState(level);
			var crop = new FarmerBlock(cropPos);
			crop.updateState(level);
			tiles.add(new FarmerTile(dirt, crop, line, quadrant));
			dirtPositions.add(dirtPos);
		}
		
		this.tiles = Collections.unmodifiableList(tiles);
		this.dirtPositions = Collections.unmodifiableList(dirtPositions);
	}
	
	private static int quadrant(BlockPos dirtPos, BlockPos centerPos)
	{
		int quadrant;
		if(dirtPos.getX() > centerPos.getX() && dirtPos.getZ() <= centerPos.getZ())
		{
			quadrant = 0;
		}
		else if(dirtPos.getX() >= centerPos.getX() && dirtPos.getZ() > centerPos.getZ())
		{
			quadrant = 1;
		}
		else if(dirtPos.getX() < centerPos.getX() && dirtPos.getZ() >= centerPos.getZ())
		{
			quadrant = 2;
		}
		else if(dirtPos.getX() <= centerPos.getX() && dirtPos.getZ() < centerPos.getZ())
		{
			quadrant = 3;
		}
		else
		{
			throw new IllegalStateException("Somehow a position was not in a quadrant");
		}
		return quadrant;
	}
	
	public void markDirty()
	{
		for(var tile : tiles)
		{
			tile.dirt().markDirty();
			tile.crop().markDirty();
		}
	}
}
