package net.swedz.extended_industrialization.machines.components.farmer.task;

import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import net.swedz.extended_industrialization.machines.components.farmer.FarmerComponentPlantableStacks;
import net.swedz.extended_industrialization.machines.components.farmer.block.FarmerBlockMap;
import net.swedz.extended_industrialization.machines.components.farmer.task.tasks.FertilizingFarmerTask;
import net.swedz.extended_industrialization.machines.components.farmer.task.tasks.HarvestingFarmerTask;
import net.swedz.extended_industrialization.machines.components.farmer.task.tasks.HydratingFarmerTask;
import net.swedz.extended_industrialization.machines.components.farmer.task.tasks.PlantingFarmerTask;
import net.swedz.extended_industrialization.machines.components.farmer.task.tasks.TillingFarmerTask;

public enum FarmerTaskType
{
	TILLING(TillingFarmerTask::new),
	HYDRATING(HydratingFarmerTask::new),
	FERTLIZING(FertilizingFarmerTask::new),
	HARVESTING(HarvestingFarmerTask::new),
	PLANTING(PlantingFarmerTask::new);
	
	private final FarmerTaskFactory factory;
	
	FarmerTaskType(FarmerTaskFactory factory)
	{
		this.factory = factory;
	}
	
	public FarmerTask create(MultiblockInventoryComponent inventory, FarmerBlockMap blockMap, FarmerComponentPlantableStacks plantableStacks, int maxOperations, int processInterval)
	{
		return factory.create(inventory, blockMap, plantableStacks, maxOperations, processInterval);
	}
}
