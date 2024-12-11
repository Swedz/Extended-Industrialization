package net.swedz.extended_industrialization.machines.component.farmer.planting;

import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriState;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerBlock;
import net.swedz.tesseract.neoforge.behavior.Behavior;

public interface FarmerPlantable extends Behavior<PlantingContext>
{
	static boolean canPlace(PlantingContext context, BlockState state)
	{
		Level level = context.level();
		FarmerBlock dirt = context.tile().dirt();
		FarmerBlock crop = context.tile().crop();
		
		BlockState farmland = dirt.state(level);
		BlockState cropState = ((BlockItem) context.stack().getItem()).getBlock().defaultBlockState();
		TriState soilDecision = farmland.canSustainPlant(level, dirt.pos(), Direction.UP, cropState);
		return soilDecision.isDefault() ? cropState.canSurvive(level, crop.pos()) : soilDecision.isTrue();
	}
	
	static BlockState getPlacementState(PlantingContext context, Block block)
	{
		BlockState state = block.getStateForPlacement(context.placeContext());
		return state != null && canPlace(context, state) ? state : null;
	}
	
	static BlockState getPlacementStateBlockItem(PlantingContext context)
	{
		return getPlacementState(context, ((BlockItem) context.stack().getItem()).getBlock());
	}
	
	boolean canPlant(PlantingContext context);
	
	void plant(PlantingContext context);
}
