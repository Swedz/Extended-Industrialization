package net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.handlers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.extended_industrialization.datamaps.FarmerSimpleTallCropSize;
import net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.HarvestingContext;
import net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.LootTableHarvestingHandler;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public final class SimpleTallCropHarvestHandler implements LootTableHarvestingHandler
{
	@Override
	public boolean matches(HarvestingContext context)
	{
		return FarmerSimpleTallCropSize.getFor(context.state().getBlock()) != null;
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
		int maxHeight = FarmerSimpleTallCropSize.getFor(context.state().getBlock()).maxHeight();
		for(int y = 0; y <= maxHeight; y++)
		{
			BlockPos pos = context.pos().above(y);
			BlockState state = context.level().getBlockState(pos);
			if(state.getBlock() == context.state().getBlock())
			{
				blocks.add(pos);
			}
			else
			{
				break;
			}
		}
		return blocks.size() > 1 ? blocks : List.of();
	}
}
