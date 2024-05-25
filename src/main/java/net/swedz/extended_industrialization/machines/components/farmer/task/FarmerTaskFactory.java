package net.swedz.extended_industrialization.machines.components.farmer.task;

import net.swedz.extended_industrialization.machines.components.farmer.FarmerComponent;

public interface FarmerTaskFactory
{
	FarmerTask create(FarmerComponent component);
}
