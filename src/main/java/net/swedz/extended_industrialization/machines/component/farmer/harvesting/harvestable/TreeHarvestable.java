package net.swedz.extended_industrialization.machines.component.farmer.harvesting.harvestable;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.bus.api.Event;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerBlockMap;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerTree;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.HarvestingContext;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.LootTableHarvestableBehavior;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.FarmerListener;
import net.swedz.tesseract.neoforge.event.TreeGrowthEvent;

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
	
	@Override
	public List<FarmerListener<? extends Event>> getListeners(FarmerBlockMap blockMap)
	{
		return List.of(
				new FarmerListener<>(TreeGrowthEvent.class, (event) ->
				{
					BlockPos base = event.getPos();
					if(blockMap.containsDirtAt(base.below()))
					{
						List<BlockPos> blocks = event.getPositions();
						blocks.removeIf(blockMap::containsDirtAt);
						blocks.sort(Collections.reverseOrder(Comparator.comparingInt(Vec3i::getY)));
						trees.put(base, new FarmerTree(base, blocks));
					}
				})
		);
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
		CompoundTag trees = new CompoundTag();
		for(FarmerTree tree : this.trees.values())
		{
			long[] list = tree.blocks().stream().mapToLong(BlockPos::asLong).toArray();
			trees.putLongArray(Long.toString(tree.base().asLong()), list);
		}
		tag.put("trees", trees);
	}
	
	@Override
	public void readNbt(CompoundTag tag)
	{
		CompoundTag trees = tag.getCompound("trees");
		for(String key : trees.getAllKeys())
		{
			BlockPos base = BlockPos.of(Long.parseLong(key));
			List<BlockPos> blocks = Arrays.stream(trees.getLongArray(key)).mapToObj(BlockPos::of).toList();
			this.trees.put(base, new FarmerTree(base, blocks));
		}
	}
}
