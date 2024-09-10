package net.swedz.extended_industrialization.machines.component.farmer.task.task;

import net.minecraft.world.level.block.state.BlockState;
import net.swedz.extended_industrialization.machines.component.farmer.FarmerComponent;
import net.swedz.extended_industrialization.machines.component.farmer.PlantableConfigurableItemStack;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerTile;
import net.swedz.extended_industrialization.machines.component.farmer.planting.PlantingContext;
import net.swedz.extended_industrialization.machines.component.farmer.planting.FarmerPlantable;
import net.swedz.extended_industrialization.machines.component.farmer.planting.FarmerPlantableBehaviorHolder;
import net.swedz.extended_industrialization.machines.component.farmer.task.FarmerTask;
import net.swedz.extended_industrialization.machines.component.farmer.task.FarmerTaskType;

import java.util.List;

public final class PlantingFarmerTask extends FarmerTask
{
	private final FarmerPlantableBehaviorHolder plantingHandlers;
	
	public PlantingFarmerTask(FarmerComponent component)
	{
		super(FarmerTaskType.PLANTING, component);
		plantingHandlers = component.getPlantableBehaviorHolder();
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
			int index = plantingMode.index(tile, plantables);
			PlantableConfigurableItemStack plantable = plantables.get(index);
			
			if(plantable.getStack().isEmpty())
			{
				continue;
			}
			
			BlockState state = tile.crop().state(level);
			if(state.isAir())
			{
				PlantingContext plantingContext = new PlantingContext(level, tile, plantable.getStack().toStack());
				FarmerPlantable farmerPlantable = plantable.asPlantable();
				if(farmerPlantable.canPlant(plantingContext))
				{
					plantable.getStack().decrement(1);
					
					farmerPlantable.plant(plantingContext);
					
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
