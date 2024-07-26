package net.swedz.extended_industrialization.machines.components.farmer.task.tasks;

import net.minecraft.world.level.block.state.BlockState;
import net.swedz.extended_industrialization.machines.components.farmer.FarmerComponent;
import net.swedz.extended_industrialization.machines.components.farmer.PlantableConfigurableItemStack;
import net.swedz.extended_industrialization.machines.components.farmer.block.FarmerBlock;
import net.swedz.extended_industrialization.machines.components.farmer.block.FarmerTile;
import net.swedz.extended_industrialization.machines.components.farmer.plantinghandler.PlantingContext;
import net.swedz.extended_industrialization.machines.components.farmer.plantinghandler.registry.FarmerPlantingHandlersHolder;
import net.swedz.extended_industrialization.machines.components.farmer.task.FarmerTask;
import net.swedz.extended_industrialization.machines.components.farmer.task.FarmerTaskType;

import java.util.List;

public final class PlantingFarmerTask extends FarmerTask
{
	private final FarmerPlantingHandlersHolder plantingHandlers;
	
	public PlantingFarmerTask(FarmerComponent component)
	{
		super(FarmerTaskType.PLANTING, component);
		plantingHandlers = component.getPlantingHandlersHolder();
	}
	
	@Override
	protected boolean run()
	{
		List<PlantableConfigurableItemStack> plantables = plantableStacks.getItems();
		plantables.removeIf((plantable) -> !plantable.isPlantable() || (!plantingMode.includeEmptyStacks() && plantable.getStack().isEmpty()));
		
		if(plantables.isEmpty())
		{
			return false;
		}
		
		for(FarmerTile tile : blockMap)
		{
			FarmerBlock crop = tile.crop();
			
			int index = plantingMode.index(tile, plantables);
			PlantableConfigurableItemStack plantable = plantables.get(index);
			
			if(plantable.getStack().isEmpty())
			{
				continue;
			}
			
			PlantingContext plantingContext = new PlantingContext(level, crop.pos(), plantable.getStack().toStack());
			if(plantable.asPlantable().canPlant(plantingContext))
			{
				BlockState state = crop.state(level);
				if(state.isAir())
				{
					plantable.getStack().decrement(1);
					
					plantable.asPlantable().plant(plantingContext);
					
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
