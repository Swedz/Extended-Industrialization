package net.swedz.miextended.machines.components.farmer.task.tasks;

import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.miextended.machines.components.farmer.FarmerComponent;
import net.swedz.miextended.machines.components.farmer.FarmerComponentPlantableStacks;
import net.swedz.miextended.machines.components.farmer.block.FarmerBlock;
import net.swedz.miextended.machines.components.farmer.block.FarmerBlockMap;
import net.swedz.miextended.machines.components.farmer.block.FarmerTile;
import net.swedz.miextended.machines.components.farmer.task.FarmerTask;

public final class HydratingFarmerTask extends FarmerTask
{
	public HydratingFarmerTask(MultiblockInventoryComponent inventory, FarmerBlockMap blockMap, FarmerComponentPlantableStacks plantableStacks, int maxOperations)
	{
		super(inventory, blockMap, plantableStacks, maxOperations);
	}
	
	@Override
	protected boolean run()
	{
		if(!hasWater)
		{
			return false;
		}
		
		for(FarmerTile tile : blockMap.tiles())
		{
			FarmerBlock dirt = tile.dirt();
			BlockPos pos = dirt.pos();
			BlockState state = dirt.state(level);
			if(state.getBlock() instanceof FarmBlock)
			{
				int moisture = state.getValue(FarmBlock.MOISTURE);
				if(moisture < 7 && FarmerComponent.consumeWater(inventory, Simulation.ACT))
				{
					BlockState newState = state.setValue(FarmBlock.MOISTURE, 7);
					dirt.setBlock(level, newState, 2);
					
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
