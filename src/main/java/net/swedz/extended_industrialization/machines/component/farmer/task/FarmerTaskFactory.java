package net.swedz.extended_industrialization.machines.component.farmer.task;

import net.swedz.extended_industrialization.machines.component.farmer.FarmerComponent;

public interface FarmerTaskFactory
{
	FarmerTask create(FarmerComponent component);
}
