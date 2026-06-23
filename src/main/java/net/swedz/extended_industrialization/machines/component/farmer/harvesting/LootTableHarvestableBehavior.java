package net.swedz.extended_industrialization.machines.component.farmer.harvesting;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface LootTableHarvestableBehavior extends HarvestableBehavior
{
	@Override
	default List<ItemStack> getDrops(HarvestingContext context)
	{
		List<BlockPos> blocks = this.getBlocks(context);
		List<ItemStack> drops = Lists.newArrayList();
		var harvestingOwner = context.harvestingOwner();
		for(BlockPos block : blocks)
		{
			var blockContext = new HarvestingContext(context.level(), block, context.level().getBlockState(block), context.harvestingOwnerUUID(), context.enchantment(), context.tier());
			var lootParams = new LootParams.Builder((ServerLevel) context.level())
					.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(block))
					.withParameter(LootContextParams.TOOL, blockContext.enchantedItem());
			harvestingOwner.ifPresent((player) -> lootParams
					.withParameter(LootContextParams.THIS_ENTITY, player));
			drops.addAll(blockContext.state().getDrops(lootParams));
		}
		return drops;
	}
}
