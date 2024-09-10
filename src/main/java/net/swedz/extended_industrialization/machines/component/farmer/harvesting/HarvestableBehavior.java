package net.swedz.extended_industrialization.machines.component.farmer.harvesting;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerBlockMap;
import net.swedz.tesseract.neoforge.behavior.Behavior;

import java.util.List;

public interface HarvestableBehavior extends Behavior<HarvestingContext>
{
	boolean isFullyGrown(HarvestingContext context);
	
	List<ItemStack> getDrops(HarvestingContext context);
	
	List<BlockPos> getBlocks(HarvestingContext context);
	
	default void harvested(HarvestingContext context)
	{
	}
	
	default List<FarmerListener<? extends Event>> getListeners(FarmerBlockMap blockMap)
	{
		return List.of();
	}
	
	default void writeNbt(CompoundTag tag)
	{
	}
	
	default void readNbt(CompoundTag tag)
	{
	}
}
