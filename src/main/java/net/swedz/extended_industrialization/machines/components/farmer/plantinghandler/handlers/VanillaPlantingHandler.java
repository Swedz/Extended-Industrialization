package net.swedz.extended_industrialization.machines.components.farmer.plantinghandler.handlers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.swedz.extended_industrialization.machines.components.farmer.plantinghandler.PlantingContext;
import net.swedz.extended_industrialization.machines.components.farmer.plantinghandler.PlantingHandler;

public final class VanillaPlantingHandler implements PlantingHandler
{
	@Override
	public boolean matches(ItemStack stack)
	{
		return !stack.isEmpty() &&
			   stack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS) &&
			   stack.getItem() instanceof BlockItem;
	}
	
	@Override
	public boolean canPlant(PlantingContext context)
	{
		BlockPos farmlandPos = context.pos().below();
		BlockState farmland = context.level().getBlockState(farmlandPos);
		BlockState crop = ((BlockItem) context.stack().getItem()).getBlock().defaultBlockState();
		return farmland.canSustainPlant(context.level(), farmlandPos, Direction.UP, crop).isTrue();
	}
	
	@Override
	public void plant(PlantingContext context)
	{
		BlockState crop = ((BlockItem) context.stack().getItem()).getBlock().defaultBlockState();
		context.level().setBlockAndUpdate(context.pos(), crop);
		context.level().gameEvent(GameEvent.BLOCK_PLACE, context.pos(), GameEvent.Context.of(crop));
	}
}
