package net.swedz.extended_industrialization.machines.component.farmer.task.task;

import net.swedz.extended_industrialization.machines.component.farmer.FarmerComponent;
import net.swedz.extended_industrialization.machines.component.farmer.PlantableConfigurableItemStack;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerTile;
import net.swedz.extended_industrialization.machines.component.farmer.planting.PlantableBehaviorHolder;
import net.swedz.extended_industrialization.machines.component.farmer.planting.PlantingContext;
import net.swedz.extended_industrialization.machines.component.farmer.task.FarmerTask;
import net.swedz.extended_industrialization.machines.component.farmer.task.FarmerTaskType;

public final class PlantingFarmerTask extends FarmerTask
{
	private final PlantableBehaviorHolder plantingHandlers;
	
	public PlantingFarmerTask(FarmerComponent component)
	{
		super(FarmerTaskType.PLANTING, component);
		plantingHandlers = component.getPlantableBehaviorHolder();
	}
	
	@Override
	protected boolean run()
	{
		var plantables = plantableStacks.getItems();
		plantables.removeIf((plantable) -> !plantable.isPlantable() || (!plantingMode.includeEmptyStacks() && plantable.getStack().isEmpty()));
		
		if(plantables.isEmpty())
		{
			return false;
		}
		
		for(var tile : blockMap)
		{
			int index = plantingMode.index(tile, plantables);
			if(index == -1)
			{
				for(var plantable : plantables)
				{
					if(this.tryPlant(plantable, tile))
					{
						return true;
					}
				}
			}
			else
			{
				var plantable = plantables.get(index);
				if(this.tryPlant(plantable, tile))
				{
					return true;
				}
			}
		}
		
		return operations.didOperate();
	}
	
	private boolean tryPlant(PlantableConfigurableItemStack plantable, FarmerTile tile)
	{
		if(plantable.getStack().isEmpty())
		{
			return false;
		}
		
		var state = tile.crop().state(level);
		if(state == state.getFluidState().createLegacyBlock())
		{
			var plantingContext = new PlantingContext(level, tile, plantable.getStack().toStack());
			var farmerPlantable = plantable.asPlantable();
			if(farmerPlantable.canPlant(plantingContext))
			{
				plantable.getStack().decrement(1);
				
				farmerPlantable.plant(plantingContext);
				
				return operations.operate();
			}
		}
		
		return false;
	}
}
