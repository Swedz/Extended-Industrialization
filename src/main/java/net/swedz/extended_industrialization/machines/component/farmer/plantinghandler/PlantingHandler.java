package net.swedz.extended_industrialization.machines.component.farmer.plantinghandler;

import net.minecraft.world.item.ItemStack;

public interface PlantingHandler
{
	boolean matches(ItemStack stack);
	
	boolean canPlant(PlantingContext context);
	
	void plant(PlantingContext context);
}
