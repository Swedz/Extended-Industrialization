package net.swedz.extended_industrialization.machines.component.farmer.harvesting.harvestable;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.HarvestingContext;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.LootTableHarvestableBehavior;

import java.util.List;

public final class GrowingPlantHarvestable implements LootTableHarvestableBehavior
{
	private static boolean isValidBlock(BlockState state)
	{
		return state.getBlock() instanceof GrowingPlantBlock block && block.growthDirection == Direction.UP;
	}
	
	@Override
	public boolean matches(HarvestingContext context)
	{
		return isValidBlock(context.state());
	}
	
	@Override
	public boolean isFullyGrown(HarvestingContext context)
	{
		return true;
	}
	
	@Override
	public List<BlockPos> getBlocks(HarvestingContext context)
	{
		List<BlockPos> blocks = Lists.newArrayList();
		
		for(int y = 0; y <= 26; y++)
		{
			BlockPos pos = context.pos().above(y);
			BlockState state = context.level().getBlockState(pos);
			if(isValidBlock(state))
			{
				blocks.add(pos);
			}
			else
			{
				break;
			}
		}
		
		return blocks.size() > 1 ? blocks.reversed() : List.of();
	}
}
