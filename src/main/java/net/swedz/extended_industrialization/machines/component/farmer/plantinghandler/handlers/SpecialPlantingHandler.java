package net.swedz.extended_industrialization.machines.component.farmer.plantinghandler.handlers;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.SpecialPlantable;
import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.machines.component.farmer.plantinghandler.PlantingContext;
import net.swedz.extended_industrialization.machines.component.farmer.plantinghandler.PlantingHandler;

public final class SpecialPlantingHandler implements PlantingHandler
{
	@Override
	public boolean matches(ItemStack stack)
	{
		return !stack.isEmpty() &&
			   stack.is(EITags.FARMER_PLANTABLE) &&
			   stack.getItem() instanceof SpecialPlantable;
	}
	
	@Override
	public boolean canPlant(PlantingContext context)
	{
		SpecialPlantable plantable = (SpecialPlantable) context.stack().getItem();
		return plantable.canPlacePlantAtPosition(context.stack(), context.level(), context.tile().crop().pos(), Direction.DOWN);
	}
	
	@Override
	public void plant(PlantingContext context)
	{
		SpecialPlantable plantable = (SpecialPlantable) context.stack().getItem();
		plantable.spawnPlantAtPosition(context.stack(), context.level(), context.tile().crop().pos(), Direction.DOWN);
	}
}
