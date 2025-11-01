package net.swedz.extended_industrialization.machines.component.farmer.task;

import net.minecraft.network.chat.Component;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.component.farmer.FarmerComponent;
import net.swedz.extended_industrialization.machines.component.farmer.task.task.FertilizingFarmerTask;
import net.swedz.extended_industrialization.machines.component.farmer.task.task.HarvestingFarmerTask;
import net.swedz.extended_industrialization.machines.component.farmer.task.task.HydratingFarmerTask;
import net.swedz.extended_industrialization.machines.component.farmer.task.task.PlantingFarmerTask;
import net.swedz.extended_industrialization.machines.component.farmer.task.task.TillingFarmerTask;

public enum FarmerTaskType
{
	TILLING(TillingFarmerTask::new, EI.text().farmerTaskTilling(), EI.text().farmerTaskTillingDescription()),
	HYDRATING(HydratingFarmerTask::new, EI.text().farmerTaskHydrating(), EI.text().farmerTaskHydratingDescription()),
	FERTLIZING(FertilizingFarmerTask::new, EI.text().farmerTaskFertilizing(), EI.text().farmerTaskFertilizingDescription()),
	HARVESTING(HarvestingFarmerTask::new, EI.text().farmerTaskHarvesting(), EI.text().farmerTaskHarvestingDescription()),
	PLANTING(PlantingFarmerTask::new, EI.text().farmerTaskPlanting(), EI.text().farmerTaskPlantingDescription());
	
	private final FarmerTaskFactory factory;
	private final Component         tooltipName, tooltipDescription;
	
	FarmerTaskType(FarmerTaskFactory factory, Component tooltipName, Component tooltipDescription)
	{
		this.factory = factory;
		this.tooltipName = tooltipName;
		this.tooltipDescription = tooltipDescription;
	}
	
	FarmerTaskType(FarmerTaskFactory factory)
	{
		this(factory, null, null);
	}
	
	public FarmerTask create(FarmerComponent component)
	{
		return factory.create(component);
	}
	
	public Component tooltip()
	{
		return tooltipName == null || tooltipDescription == null ? null :
				EI.text().farmerTask(tooltipName, tooltipDescription);
	}
}
