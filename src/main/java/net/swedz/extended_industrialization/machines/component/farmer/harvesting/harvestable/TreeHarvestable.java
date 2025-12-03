package net.swedz.extended_industrialization.machines.component.farmer.harvesting.harvestable;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.bus.api.Event;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerBlockMap;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerTree;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.FarmerListener;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.HarvestingContext;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.LootTableHarvestableBehavior;
import net.swedz.tesseract.neoforge.event.treegrowth.TreeGrowthEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class TreeHarvestable implements LootTableHarvestableBehavior
{
	private final Map<BlockPos, FarmerTree> trees = Maps.newHashMap();
	
	@Override
	public boolean matches(HarvestingContext context)
	{
		return trees.containsKey(context.pos());
	}
	
	@Override
	public boolean isFullyGrown(HarvestingContext context)
	{
		return true;
	}
	
	@Override
	public List<BlockPos> getBlocks(HarvestingContext context)
	{
		return trees.get(context.pos()).blocks();
	}
	
	@Override
	public void harvested(HarvestingContext context)
	{
		trees.remove(context.pos());
	}
	
	private void onTreeGrow(TreeGrowthEvent event, FarmerBlockMap blockMap)
	{
		var origin = event.getPos();
		if(blockMap.containsDirtAt(origin.below()))
		{
			var blocks = event.getPositions();
			blocks.removeIf(blockMap::containsDirtAt);
			blocks.sort(Collections.reverseOrder(Comparator.comparingInt(Vec3i::getY)));
			trees.put(origin, new FarmerTree(origin, blocks));
		}
	}
	
	@Override
	public List<FarmerListener<? extends Event>> getListeners(FarmerBlockMap blockMap)
	{
		return List.of(
				new FarmerListener<>(TreeGrowthEvent.class, (event) -> this.onTreeGrow(event, blockMap))
		);
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
		var treesTag = new CompoundTag();
		for(FarmerTree tree : trees.values())
		{
			long[] list = tree.blocks().stream().mapToLong(BlockPos::asLong).toArray();
			treesTag.putLongArray(Long.toString(tree.origin().asLong()), list);
		}
		tag.put("trees", treesTag);
	}
	
	@Override
	public void readNbt(CompoundTag tag)
	{
		var treesTag = tag.getCompound("trees");
		for(String key : treesTag.getAllKeys())
		{
			var base = BlockPos.of(Long.parseLong(key));
			var blocks = Arrays.stream(treesTag.getLongArray(key)).mapToObj(BlockPos::of).toList();
			trees.put(base, new FarmerTree(base, blocks));
		}
	}
}
