package net.swedz.extended_industrialization.machines.components.farmer.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public final class FarmerBlock
{
	private final BlockPos pos;
	
	private BlockState state;
	
	FarmerBlock(BlockPos pos)
	{
		this.pos = pos;
	}
	
	public BlockPos pos()
	{
		return pos;
	}
	
	public BlockState state(Level level)
	{
		if(state == null)
		{
			this.updateState(level);
		}
		return state;
	}
	
	public void updateState(BlockState state)
	{
		this.state = state;
	}
	
	public void updateState(Level level)
	{
		this.updateState(level.getBlockState(pos));
	}
	
	public void setBlock(Level level, BlockState state, int flags)
	{
		level.setBlock(pos, state, flags);
		this.updateState(state);
	}
	
	public void setBlock(Level level, BlockState state, int flags, GameEvent gameEvent, BlockState affectedState)
	{
		this.setBlock(level, state, flags);
		level.gameEvent(gameEvent, pos, GameEvent.Context.of(affectedState));
	}
	
	public void markDirty()
	{
		state = null;
	}
}
