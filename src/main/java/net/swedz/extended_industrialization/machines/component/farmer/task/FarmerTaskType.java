package net.swedz.extended_industrialization.machines.component.farmer.task;

import net.minecraft.network.chat.Component;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.machines.component.farmer.FarmerComponent;
import net.swedz.extended_industrialization.machines.component.farmer.task.tasks.FertilizingFarmerTask;
import net.swedz.extended_industrialization.machines.component.farmer.task.tasks.HarvestingFarmerTask;
import net.swedz.extended_industrialization.machines.component.farmer.task.tasks.HydratingFarmerTask;
import net.swedz.extended_industrialization.machines.component.farmer.task.tasks.PlantingFarmerTask;
import net.swedz.extended_industrialization.machines.component.farmer.task.tasks.TillingFarmerTask;
import net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine;

import static aztech.modern_industrialization.MITooltips.*;

public enum FarmerTaskType
{
	TILLING(TillingFarmerTask::new, EIText.FARMER_TASK_TILLING, EIText.FARMER_TASK_TILLING_DESCRIPTION),
	HYDRATING(HydratingFarmerTask::new, EIText.FARMER_TASK_HYDRATING, EIText.FARMER_TASK_HYDRATING_DESCRIPTION),
	FERTLIZING(FertilizingFarmerTask::new, EIText.FARMER_TASK_FERTILIZING, EIText.FARMER_TASK_FERTILIZING_DESCRIPTION),
	HARVESTING(HarvestingFarmerTask::new, EIText.FARMER_TASK_HARVESTING, EIText.FARMER_TASK_HARVESTING_DESCRIPTION),
	PLANTING(PlantingFarmerTask::new, EIText.FARMER_TASK_PLANTING, EIText.FARMER_TASK_PLANTING_DESCRIPTION);
	
	private final FarmerTaskFactory factory;
	private final EIText            tooltipName, tooltipDescription;
	
	FarmerTaskType(FarmerTaskFactory factory, EIText tooltipName, EIText tooltipDescription)
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
				MICompatibleTextLine.line(EIText.FARMER_TASK)
						.arg(tooltipName.text().setStyle(NUMBER_TEXT))
						.arg(tooltipDescription.text());
	}
}
