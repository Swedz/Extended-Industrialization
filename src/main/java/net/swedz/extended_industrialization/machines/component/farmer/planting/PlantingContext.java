package net.swedz.extended_industrialization.machines.component.farmer.planting;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerTile;

public record PlantingContext(Level level, FarmerTile tile, ItemStack stack)
{
	public BlockPlaceContext placeContext()
	{
		var dirtPos = tile.dirt().pos();
		return new BlockPlaceContext(level, null, InteractionHand.MAIN_HAND, stack, new BlockHitResult(dirtPos.getBottomCenter().add(0, 1, 0), Direction.UP, dirtPos, false));
	}
}
