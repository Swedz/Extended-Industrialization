package net.swedz.extended_industrialization.machines.component.farmer.harvesting.harvestable;

import com.blakebr0.mysticalagriculture.block.MysticalCropBlock;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.HarvestableBehavior;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.HarvestingContext;

import java.util.List;

public final class MysticalAgricultureHarvestable implements HarvestableBehavior
{
	@Override
	public boolean matches(HarvestingContext context)
	{
		return context.state().getBlock() instanceof MysticalCropBlock;
	}
	
	@Override
	public boolean isFullyGrown(HarvestingContext context)
	{
		MysticalCropBlock mysticalCropBlock = (MysticalCropBlock) context.state().getBlock();
		return mysticalCropBlock.isMaxAge(context.state());
	}
	
	@Override
	public List<BlockPos> getBlocks(HarvestingContext context)
	{
		return Lists.newArrayList(context.pos());
	}
	
	@Override
	public List<ItemStack> getDrops(HarvestingContext context)
	{
		MysticalCropBlock cropBlock = (MysticalCropBlock) context.state().getBlock();
		LootParams.Builder builder = new LootParams.Builder((ServerLevel) context.level())
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(context.pos()))
				.withParameter(LootContextParams.TOOL, ItemStack.EMPTY);
		return cropBlock.getDrops(context.state(), builder);
	}
}