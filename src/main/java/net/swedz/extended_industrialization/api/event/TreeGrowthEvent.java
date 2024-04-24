package net.swedz.extended_industrialization.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.ArrayList;
import java.util.List;

public class TreeGrowthEvent extends BlockEvent
{
	private final List<BlockPos> positions;
	
	public TreeGrowthEvent(LevelAccessor level, BlockPos pos, BlockState state, List<BlockPos> positions)
	{
		super(level, pos, state);
		this.positions = positions;
	}
	
	public List<BlockPos> getPositions()
	{
		return new ArrayList<>(positions);
	}
}
