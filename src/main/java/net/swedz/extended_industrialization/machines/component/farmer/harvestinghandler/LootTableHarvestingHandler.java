package net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface LootTableHarvestingHandler extends HarvestingHandler
{
	default LootTable getLootTable(HarvestingContext context)
	{
		ResourceKey<LootTable> lootTableId = context.state().getBlock().getLootTable();
		return context.level().getServer().reloadableRegistries().getLootTable(lootTableId);
	}
	
	@Override
	default List<ItemStack> getDrops(HarvestingContext context)
	{
		List<BlockPos> blocks = this.getBlocks(context);
		List<ItemStack> drops = Lists.newArrayList();
		for(BlockPos block : blocks)
		{
			HarvestingContext blockContext = new HarvestingContext(context.level(), block, context.level().getBlockState(block));
			LootTable lootTable = this.getLootTable(blockContext);
			LootParams lootParams = new LootParams.Builder((ServerLevel) context.level())
					.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(block))
					.withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
					.withParameter(LootContextParams.BLOCK_STATE, blockContext.state())
					.create(LootContextParamSets.BLOCK);
			drops.addAll(lootTable.getRandomItems(lootParams));
		}
		return drops;
	}
}
