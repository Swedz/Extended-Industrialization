package net.swedz.extended_industrialization.machines.component.farmer.planting;

import net.swedz.tesseract.neoforge.behavior.Behavior;

public interface FarmerPlantable extends Behavior<PlantingContext>
{
	boolean canPlant(PlantingContext context);
	
	void plant(PlantingContext context);
}
