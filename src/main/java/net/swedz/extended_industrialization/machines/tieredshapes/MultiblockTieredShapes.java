package net.swedz.extended_industrialization.machines.tieredshapes;

import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.components.ActiveShapeComponent;
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;
import net.swedz.tesseract.neoforge.compat.mi.helper.CommonGuiComponents;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class MultiblockTieredShapes<T extends MultiblockTier>
{
	protected final ResourceLocation machineId;
	
	private final Comparator<T> tierSort;
	
	protected List<T>                  tiers        = List.of();
	protected Map<ResourceLocation, T> tiersByBlock = Collections.unmodifiableMap(Maps.newHashMap());
	
	private ShapeTemplate[] shapeTemplates = new ShapeTemplate[0];
	
	public MultiblockTieredShapes(ResourceLocation machineId, Comparator<T> tierSort)
	{
		this.machineId = machineId;
		this.tierSort = tierSort;
	}
	
	public final ResourceLocation machineId()
	{
		return machineId;
	}
	
	public final List<T> tiers()
	{
		return tiers;
	}
	
	public final Map<ResourceLocation, T> tiersByBlock()
	{
		return tiersByBlock;
	}
	
	public final ShapeTemplate[] shapeTemplates()
	{
		return shapeTemplates;
	}
	
	protected abstract List<T> buildTiers();
	
	private void invalidateTiers()
	{
		List<T> newTiers = Lists.newArrayList();
		newTiers.addAll(this.buildTiers());
		newTiers.sort(tierSort);
		
		tiers = Collections.unmodifiableList(newTiers);
		tiersByBlock = tiers.stream().collect(Collectors.toMap(MultiblockTier::blockId, Function.identity()));
	}
	
	protected abstract void buildShapeTemplates(ShapeTemplate[] shapeTemplates);
	
	private void invalidateShapeTemplates()
	{
		shapeTemplates = new ShapeTemplate[tiers.size()];
		this.buildShapeTemplates(shapeTemplates);
	}
	
	protected void invalidateRecipeViewerShapes()
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
	
	protected static String[][] layersConvertFromVertical(String[][] input)
	{
		int rows = input[0].length;
		int columns = input.length;
		
		String[][] result = new String[rows][columns];
		
		for(int row = 0; row < rows; row++)
		{
			for(int column = 0; column < columns; column++)
			{
				result[row][column] = input[column][row];
			}
		}
		
		return result;
	}
}
