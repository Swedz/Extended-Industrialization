package net.swedz.extended_industrialization.machines.tieredshapes;

import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.components.ActiveShapeComponent;
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;
import net.swedz.tesseract.neoforge.compat.mi.helper.CommonGuiComponents;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class MultiblockTieredShapes<T extends MultiblockTier, D extends DataMultiblockTier<T>>
{
	protected final ResourceLocation      machineId;
	protected final DataMapType<Block, D> tierDataMap;
	protected final Comparator<T>         tierSort;
	
	protected List<T>                  tiers        = List.of();
	protected Map<ResourceLocation, T> tiersByBlock = Collections.unmodifiableMap(Maps.newHashMap());
	
	protected ShapeTemplate[] shapeTemplates = new ShapeTemplate[0];
	
	public MultiblockTieredShapes(ResourceLocation machineId, DataMapType<Block, D> tierDataMap, Comparator<T> tierSort)
	{
		this.machineId = machineId;
		this.tierDataMap = tierDataMap;
		this.tierSort = tierSort;
	}
	
	public ResourceLocation machineId()
	{
		return machineId;
	}
	
	public List<T> tiers()
	{
		return tiers;
	}
	
	public Map<ResourceLocation, T> tiersByBlock()
	{
		return tiersByBlock;
	}
	
	public ShapeTemplate[] shapeTemplates()
	{
		return shapeTemplates;
	}
	
	private void invalidateTiers()
	{
		List<T> newTiers = Lists.newArrayList();
		
		BuiltInRegistries.BLOCK.getDataMap(tierDataMap).forEach((block, tier) ->
				newTiers.add(tier.wrap(block)));
		
		newTiers.sort(tierSort);
		
		tiers = Collections.unmodifiableList(newTiers);
		tiersByBlock = tiers.stream().collect(Collectors.toMap(MultiblockTier::blockId, Function.identity()));
	}
	
	protected abstract void invalidateShapeTemplates();
	
	private void invalidateRecipeViewerShapes()
	{
		ReiMachineRecipes.multiblockShapes.removeIf((e) -> e.machine().equals(machineId));
		int index = 0;
		for(ShapeTemplate shapeTemplate : shapeTemplates)
		{
			ReiMachineRecipes.registerMultiblockShape(machineId, shapeTemplate, "" + index);
			index++;
		}
	}
	
	private void invalidate()
	{
		this.invalidateTiers();
		this.invalidateShapeTemplates();
		this.invalidateRecipeViewerShapes();
	}
	
	public ShapeSelection.Server createShapeSelectionGuiComponent(MultiblockMachineBlockEntity machine, ActiveShapeComponent activeShape, boolean useArrows)
	{
		List<Component> tierNames = tiers.stream().map(MultiblockTier::getDisplayName).toList();
		return CommonGuiComponents.rangedShapeSelection(machine, activeShape, tierNames, useArrows);
	}
	
	public final void register()
	{
		NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, DataMapsUpdatedEvent.class, (event) ->
				event.ifRegistry(Registries.BLOCK, (registry) -> this.invalidate()));
	}
}
