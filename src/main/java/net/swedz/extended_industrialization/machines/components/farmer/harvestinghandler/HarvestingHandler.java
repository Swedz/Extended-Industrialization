package net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.swedz.extended_industrialization.machines.components.farmer.block.FarmerBlockMap;
import net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.registry.FarmerListener;

import java.util.List;

public interface HarvestingHandler
{
	boolean matches(HarvestingContext context);
	
	boolean isFullyGrown(HarvestingContext context);
	
	List<ItemStack> getDrops(HarvestingContext context);
	
	List<BlockPos> getBlocks(HarvestingContext context);
	
	default void harvested(HarvestingContext context)
	{
	}
	
	default List<FarmerListener<? extends Event>> getListeners(FarmerBlockMap farmerBlockMap)
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
