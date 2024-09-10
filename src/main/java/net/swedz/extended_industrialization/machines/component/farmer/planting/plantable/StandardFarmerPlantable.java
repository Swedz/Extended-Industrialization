package net.swedz.extended_industrialization.machines.component.farmer.planting.plantable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerBlock;
import net.swedz.extended_industrialization.machines.component.farmer.planting.PlantingContext;
import net.swedz.extended_industrialization.machines.component.farmer.planting.FarmerPlantable;

public final class StandardFarmerPlantable implements FarmerPlantable
{
	@Override
	public boolean matches(PlantingContext context)
	{
		ItemStack stack = context.stack();
		return !stack.isEmpty() &&
			   stack.is(EITags.FARMER_PLANTABLE) &&
			   stack.getItem() instanceof BlockItem;
	}
	
	@Override
	public boolean canPlant(PlantingContext context)
	{
		Level level = context.level();
		FarmerBlock dirt = context.tile().dirt();
		FarmerBlock crop = context.tile().crop();
		
		BlockState farmland = dirt.state(level);
		BlockState cropState = ((BlockItem) context.stack().getItem()).getBlock().defaultBlockState();
		TriState soilDecision = farmland.canSustainPlant(level, dirt.pos(), Direction.UP, cropState);
		return soilDecision.isDefault() ? cropState.canSurvive(level, crop.pos()) : soilDecision.isTrue();
	}
	
	@Override
	public void plant(PlantingContext context)
	{
		BlockState crop = ((BlockItem) context.stack().getItem()).getBlock().defaultBlockState();
		context.tile().crop().setBlock(context.level(), crop, 3, GameEvent.BLOCK_PLACE, crop);
	}
}
