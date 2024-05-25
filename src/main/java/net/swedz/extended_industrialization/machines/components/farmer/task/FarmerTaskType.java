package net.swedz.extended_industrialization.machines.components.farmer.task;

import net.swedz.extended_industrialization.machines.components.farmer.FarmerComponent;
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
	
	public FarmerTask create(FarmerComponent component)
	{
		return factory.create(component);
	}
}
