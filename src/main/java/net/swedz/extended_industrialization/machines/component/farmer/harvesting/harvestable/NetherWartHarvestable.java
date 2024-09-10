package net.swedz.extended_industrialization.machines.component.farmer.harvesting.harvestable;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.NetherWartBlock;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.HarvestingContext;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.LootTableHarvestableBehavior;

import java.util.List;

public final class NetherWartHarvestable implements LootTableHarvestableBehavior
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
