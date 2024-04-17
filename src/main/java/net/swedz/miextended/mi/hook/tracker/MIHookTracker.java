package net.swedz.miextended.mi.hook.tracker;

import aztech.modern_industrialization.definition.FluidDefinition;
import aztech.modern_industrialization.machines.models.MachineCasing;
import com.google.common.collect.Maps;
import net.swedz.miextended.datagen.api.object.DatagenLanguageWrapper;
import net.swedz.miextended.datagen.api.object.DatagenModelWrapper;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.function.TriConsumer;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class MIHookTracker
{
	private static boolean OPEN;
	
	public static void open()
	{
		OPEN = true;
	}
	
	public static void close()
	{
		OPEN = false;
	}
	
	public static boolean isOpen()
	{
		return OPEN;
	}
	
	public static final List<Consumer<DatagenLanguageWrapper>>     LANGUAGE          = Lists.newArrayList();
	public static final Map<String, Consumer<DatagenModelWrapper>> BLOCK_STATES      = Maps.newHashMap();
	public static final Map<String, Consumer<DatagenModelWrapper>> BLOCK_MODELS      = Maps.newHashMap();
	public static final Map<String, Consumer<DatagenModelWrapper>> ITEM_MODELS       = Maps.newHashMap();
	public static final List<FluidDefinition>                      FLUID_DEFINITIONS = Lists.newArrayList();
	
	public static void addLanguageEntry(String id, String englishName, TriConsumer<DatagenLanguageWrapper, String, String> action)
	{
		if(!OPEN)
		{
			throw new IllegalStateException("Tried to add language entry while the tracker was closed.");
		}
		
		LANGUAGE.add((wrapper) -> action.accept(wrapper, id, englishName));
	}
	
	public static void addItemLanguageEntry(String id, String englishName)
	{
		addLanguageEntry(id, englishName, DatagenLanguageWrapper::addItem);
	}
	
	public static void addMachineLanguageEntry(String id, String englishName)
	{
		addLanguageEntry(id, englishName, DatagenLanguageWrapper::addBlock);
	}
	
	public static void addMachineRecipeTypeLanguageEntry(String id, String englishName)
	{
		addLanguageEntry(id, englishName, DatagenLanguageWrapper::addRecipeCategory);
	}
	
	public static void addFluidDefinitionLanguageEntry(String id, String englishName)
	{
		addLanguageEntry(id, englishName, DatagenLanguageWrapper::addFluidDefinition);
	}
	
	public static void addMachineModelBlockState(String id)
	{
		if(!OPEN)
		{
			throw new IllegalStateException("Tried to add block state entry while the tracker was closed.");
		}
		
		BLOCK_STATES.put(id, (wrapper) -> wrapper.blankBlockState(id));
	}
	
	public static void addMachineModelBlockModel(String id, String overlay,
												 MachineCasing defaultCasing,
												 boolean front, boolean top, boolean side, boolean active)
	{
		if(!OPEN)
		{
			throw new IllegalStateException("Tried to add block model entry while the tracker was closed.");
		}
		
		BLOCK_MODELS.put(id, (wrapper) -> wrapper.modernIndustrializationMachineBlockModel(id, overlay, defaultCasing, front, top, side, active));
	}
	
	public static void addStandardItemModelEntry(String id)
	{
		if(!OPEN)
		{
			throw new IllegalStateException("Tried to add item model entry while the tracker was closed.");
		}
		
		ITEM_MODELS.put(id, (wrapper) -> wrapper.standardItemGenerated(id));
	}
	
	public static void addMachineModelItemModel(String id)
	{
		if(!OPEN)
		{
			throw new IllegalStateException("Tried to add item model entry while the tracker was closed.");
		}
		
		ITEM_MODELS.put(id, (wrapper) -> wrapper.parentBlockItemModel(id));
	}
	
	public static void addFluidDefinition(FluidDefinition fluidDefinition)
	{
		if(!OPEN)
		{
			throw new IllegalStateException("Tried to add fluid definition entry while the tracker was closed.");
		}
		
		FLUID_DEFINITIONS.add(fluidDefinition);
	}
}
