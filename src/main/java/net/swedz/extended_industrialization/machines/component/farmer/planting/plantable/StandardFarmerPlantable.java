package net.swedz.extended_industrialization.machines.component.farmer.planting.plantable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.machines.component.farmer.planting.FarmerPlantable;
import net.swedz.extended_industrialization.machines.component.farmer.planting.PlantingContext;

public final class StandardFarmerPlantable implements FarmerPlantable
{
	private static boolean supportsBlockType(Block block)
	{
		if(block instanceof GrowingPlantBlock plant)
		{
			return plant.growthDirection == Direction.UP;
		}
		return true;
	}
	
	@Override
	public boolean matches(PlantingContext context)
	{
		var stack = context.stack();
		return !stack.isEmpty() &&
			   stack.is(EITags.Items.FARMER_PLANTABLE) &&
			   stack.getItem() instanceof BlockItem item &&
			   supportsBlockType(item.getBlock());
	}
	
	@Override
	public boolean canPlant(PlantingContext context)
	{
		return FarmerPlantable.getPlacementStateBlockItem(context) != null;
	}
	
	@Override
	public void plant(PlantingContext context)
	{
		var crop = FarmerPlantable.getPlacementStateBlockItem(context);
		context.tile().crop().setBlock(context.level(), crop, 3, GameEvent.BLOCK_PLACE, crop);
	}
}
