package net.swedz.miextended.machines.components.farmer.block;

import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class FarmerBlockMap implements Iterable<FarmerTile>
{
	private List<FarmerTile> tiles         = List.of();
	private List<BlockPos>   dirtPositions = List.of();
	
	private final Map<BlockPos, FarmerTree> trees = Maps.newHashMap();
	
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
	
	public Map<BlockPos, FarmerTree> trees()
	{
		return Map.copyOf(trees);
	}
	
	public void addTree(BlockPos base, List<BlockPos> blocks)
	{
		trees.put(base, new FarmerTree(base, blocks));
	}
	
	public boolean containsTree(FarmerBlock crop)
	{
		return trees.containsKey(crop.pos());
	}
	
	public FarmerTree popTree(FarmerBlock crop)
	{
		return trees.remove(crop.pos());
	}
	
	public void fromOffsets(Level level, BlockPos controllerPos, Direction controllerDirection, List<BlockPos> offsets)
	{
		BlockPos centerPos = controllerPos.relative(controllerDirection.getOpposite());
		
		List<FarmerTile> tiles = new ArrayList<>(offsets.size());
		List<BlockPos> dirtPositions = new ArrayList<>(offsets.size());
		
		int line = 0;
		Integer lastX = null;
		for(BlockPos offset : offsets)
		{
			BlockPos pos = ShapeMatcher.toWorldPos(controllerPos, controllerDirection, offset);
			
			if(lastX != null && lastX != pos.getX())
			{
				line++;
			}
			
			int quadrant;
			if(pos.getX() > centerPos.getX() && pos.getZ() <= centerPos.getZ())
			{
				quadrant = 0;
			}
			else if(pos.getX() >= centerPos.getX() && pos.getZ() > centerPos.getZ())
			{
				quadrant = 1;
			}
			else if(pos.getX() < centerPos.getX() && pos.getZ() >= centerPos.getZ())
			{
				quadrant = 2;
			}
			else if(pos.getX() <= centerPos.getX() && pos.getZ() < centerPos.getZ())
			{
				quadrant = 3;
			}
			else
			{
				throw new IllegalStateException("Somehow a position was not in a quadrant");
			}
			
			FarmerBlock dirt = new FarmerBlock(pos);
			dirt.updateState(level);
			FarmerBlock crop = new FarmerBlock(pos.above());
			crop.updateState(level);
			tiles.add(new FarmerTile(dirt, crop, line, quadrant));
			dirtPositions.add(pos);
			
			lastX = pos.getX();
		}
		
		this.tiles = Collections.unmodifiableList(tiles);
		this.dirtPositions = dirtPositions;
	}
	
	public void markDirty()
	{
		for(FarmerTile tile : tiles)
		{
			tile.dirt().markDirty();
			tile.crop().markDirty();
		}
	}
}
