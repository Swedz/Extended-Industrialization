package net.swedz.extended_industrialization.machines.components.farmer.task.tasks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.swedz.extended_industrialization.machines.components.farmer.FarmerComponent;
import net.swedz.extended_industrialization.machines.components.farmer.PlantableConfigurableItemStack;
import net.swedz.extended_industrialization.machines.components.farmer.block.FarmerBlock;
import net.swedz.extended_industrialization.machines.components.farmer.block.FarmerTile;
import net.swedz.extended_industrialization.machines.components.farmer.task.FarmerTask;
import net.swedz.extended_industrialization.machines.components.farmer.task.FarmerTaskType;

import java.util.List;

public final class PlantingFarmerTask extends FarmerTask
{
	public PlantingFarmerTask(FarmerComponent component)
	{
		super(FarmerTaskType.PLANTING, component);
	}
	
	@Override
	protected boolean run()
	{
		List<PlantableConfigurableItemStack> plantables = plantableStacks.getItems();
		plantables.removeIf((plantable) -> !plantable.isPlantable() || (!plantingMode.includeEmptyStacks() && plantable.getStack().isEmpty()));
		
		if(plantables.size() == 0)
		{
			return false;
		}
		
		for(FarmerTile tile : blockMap)
		{
			FarmerBlock crop = tile.crop();
			
			int index = plantingMode.index(tile, plantables);
			PlantableConfigurableItemStack plantable = plantables.get(index);
			if(tile.canBePlantedOnBy(level, plantable.asPlantable()) && !plantable.getStack().isEmpty())
			{
				BlockPos pos = crop.pos();
				BlockState state = crop.state(level);
				if(state.isAir())
				{
					BlockState plantState = plantable.getPlant(level, pos);
					
					if(plantState.canSurvive(level, pos))
					{
						plantable.getStack().decrement(1);
						
						crop.setBlock(level, plantState, 1 | 2, GameEvent.BLOCK_PLACE, plantState);
						
						if(operations.operate())
						{
							return true;
						}
					}
				}
			}
		}
		
		return operations.didOperate();
	}
}
