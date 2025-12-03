package net.swedz.extended_industrialization.machines.component.farmer.planting.plantable;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.common.SpecialPlantable;
import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.machines.component.farmer.planting.FarmerPlantable;
import net.swedz.extended_industrialization.machines.component.farmer.planting.PlantingContext;

public final class SpecialFarmerPlantable implements FarmerPlantable
{
	@Override
	public boolean matches(PlantingContext context)
	{
		var stack = context.stack();
		return !stack.isEmpty() &&
			   stack.is(EITags.Items.FARMER_PLANTABLE) &&
			   stack.getItem() instanceof SpecialPlantable;
	}
	
	@Override
	public boolean canPlant(PlantingContext context)
	{
		var plantable = (SpecialPlantable) context.stack().getItem();
		return plantable.canPlacePlantAtPosition(context.stack(), context.level(), context.tile().crop().pos(), Direction.DOWN);
	}
	
	@Override
	public void plant(PlantingContext context)
	{
		var plantable = (SpecialPlantable) context.stack().getItem();
		plantable.spawnPlantAtPosition(context.stack(), context.level(), context.tile().crop().pos(), Direction.DOWN);
	}
}
