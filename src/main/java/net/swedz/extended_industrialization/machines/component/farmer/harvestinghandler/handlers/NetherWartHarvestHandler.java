package net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler.handlers;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.NetherWartBlock;
import net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler.HarvestingContext;
import net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler.LootTableHarvestingHandler;

import java.util.List;

public final class NetherWartHarvestHandler implements LootTableHarvestingHandler
{
	@Override
	public boolean matches(HarvestingContext context)
	{
		return context.state().getBlock() instanceof NetherWartBlock;
	}
	
	@Override
	public boolean isFullyGrown(HarvestingContext context)
	{
		return context.state().getValue(NetherWartBlock.AGE) == NetherWartBlock.MAX_AGE;
	}
	
	@Override
	public List<BlockPos> getBlocks(HarvestingContext context)
	{
		return Lists.newArrayList(context.pos());
	}
}
