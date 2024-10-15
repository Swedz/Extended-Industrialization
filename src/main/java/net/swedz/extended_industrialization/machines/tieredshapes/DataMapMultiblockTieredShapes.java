package net.swedz.extended_industrialization.machines.tieredshapes;

import com.google.common.collect.Lists;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.Comparator;
import java.util.List;

public abstract class DataMapMultiblockTieredShapes<T extends MultiblockTier, D extends DataMultiblockTier<T>> extends MultiblockTieredShapes<T>
{
	private final DataMapType<Block, D> tierDataMap;
	
	public DataMapMultiblockTieredShapes(ResourceLocation machineId, Comparator<T> tierSort, DataMapType<Block, D> tierDataMap)
	{
		super(machineId, tierSort);
		this.tierDataMap = tierDataMap;
	}
	
	@Override
	protected List<T> buildTiers()
	{
		List<T> newTiers = Lists.newArrayList();
		
		BuiltInRegistries.BLOCK.getDataMap(tierDataMap).forEach((block, tier) ->
				newTiers.add(tier.wrap(block)));
		
		return newTiers;
	}
}
