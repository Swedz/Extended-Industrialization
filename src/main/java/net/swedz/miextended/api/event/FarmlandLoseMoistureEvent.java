package net.swedz.miextended.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class FarmlandLoseMoistureEvent extends BlockEvent implements ICancellableEvent
{
	private final int moistureBefore, moistureAfter;
	
	public FarmlandLoseMoistureEvent(LevelAccessor level, BlockPos pos, BlockState state, int moistureBefore, int moistureAfter)
	{
		super(level, pos, state);
		this.moistureBefore = moistureBefore;
		this.moistureAfter = moistureAfter;
	}
	
	public int getMoistureBefore()
	{
		return moistureBefore;
	}
	
	public int getMoistureAfter()
	{
		return moistureAfter;
	}
}
