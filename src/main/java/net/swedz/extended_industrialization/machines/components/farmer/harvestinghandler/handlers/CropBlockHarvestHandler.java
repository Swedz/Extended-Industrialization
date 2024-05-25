package net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.handlers;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CropBlock;
import net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.HarvestingContext;
import net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.LootTableHarvestingHandler;

import java.util.List;

public final class CropBlockHarvestHandler implements LootTableHarvestingHandler
{
	@Override
	public boolean matches(HarvestingContext context)
	{
		return context.state().getBlock() instanceof CropBlock;
	}
	
	@Override
	public boolean isFullyGrown(HarvestingContext context)
	{
		CropBlock cropBlock = (CropBlock) context.state().getBlock();
		return cropBlock.isMaxAge(context.state());
	}
	
	@Override
	public List<BlockPos> getBlocks(HarvestingContext context)
	{
		return Lists.newArrayList(context.pos());
	}
}
