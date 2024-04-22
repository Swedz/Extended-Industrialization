package net.swedz.miextended.machines.components.farmer.task.tasks;

import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.swedz.miextended.machines.components.farmer.FarmerComponentPlantableStacks;
import net.swedz.miextended.machines.components.farmer.block.FarmerBlock;
import net.swedz.miextended.machines.components.farmer.block.FarmerBlockMap;
import net.swedz.miextended.machines.components.farmer.block.FarmerTile;
import net.swedz.miextended.machines.components.farmer.task.FarmerTask;
import net.swedz.miextended.machines.components.farmer.task.FarmerTaskType;

public final class TillingFarmerTask extends FarmerTask
{
	public TillingFarmerTask(MultiblockInventoryComponent inventory, FarmerBlockMap blockMap, FarmerComponentPlantableStacks plantableStacks, int maxOperations, int processInterval)
	{
		super(FarmerTaskType.TILLING, inventory, blockMap, plantableStacks, maxOperations, processInterval);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected boolean run()
	{
		if(!tilling || !hasWater)
		{
			return false;
		}
		
		for(FarmerTile tile : blockMap.tiles())
		{
			FarmerBlock dirt = tile.dirt();
			BlockPos pos = dirt.pos();
			BlockState state = dirt.state(level);
			if(state.is(BlockTags.DIRT))
			{
				BlockState newState = Blocks.FARMLAND.defaultBlockState();
				if(Blocks.FARMLAND.canSurvive(newState, level, pos))
				{
					dirt.setBlock(level, newState, 1 | 2 | 8, GameEvent.BLOCK_CHANGE, newState);
					
					if(operations.operate())
					{
						return true;
					}
				}
			}
		}
		
		return operations.didOperate();
	}
}
