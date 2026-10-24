package net.swedz.extended_industrialization.machines.component.farmer.harvesting;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface LootTableHarvestableBehavior extends HarvestableBehavior
{
	private int getLootingLevelOf(HarvestingContext context, ItemStack harvestingItem)
	{
		var level = context.level();
		int looting = harvestingItem.getEnchantmentLevel(level.holderOrThrow(Enchantments.LOOTING));
		if(looting > 0)
		{
			var enchantments = new ItemEnchantments.Mutable(harvestingItem.getAllEnchantments(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)));
			enchantments.removeIf((holder) -> holder.is(Enchantments.LOOTING));
			harvestingItem.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
		}
		return looting;
	}
	
	private void applyBonusToDrops(HarvestingContext context, List<ItemStack> blockDrops, int looting)
	{
		if(looting > 0)
		{
			for(var stack : blockDrops)
			{
				int add = context.level().getRandom().nextInt(0, looting + 1);
				if(add > 0)
				{
					stack.setCount(stack.getCount() + add);
				}
			}
		}
	}
	
	@Override
	default List<ItemStack> getDrops(HarvestingContext context)
	{
		var level = (ServerLevel) context.level();
		List<BlockPos> blocks = this.getBlocks(context);
		List<ItemStack> drops = Lists.newArrayList();
		var harvestingOwner = context.harvestingOwner();
		
		var harvestingItem = context.enchantedItem();
		int looting = this.getLootingLevelOf(context, harvestingItem);
		
		for(var blockPos : blocks)
		{
			var blockContext = new HarvestingContext(level, blockPos, level.getBlockState(blockPos), context.harvestingOwnerUUID(), context.enchantment(), context.tier());
			var lootParams = new LootParams.Builder(level)
					.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos))
					.withParameter(LootContextParams.TOOL, harvestingItem);
			harvestingOwner.ifPresent((player) -> lootParams
					.withParameter(LootContextParams.THIS_ENTITY, player));
			
			var blockDrops = blockContext.state().getDrops(lootParams);
			this.applyBonusToDrops(context, blockDrops, looting);
			drops.addAll(blockDrops);
		}
		
		return drops;
	}
}
