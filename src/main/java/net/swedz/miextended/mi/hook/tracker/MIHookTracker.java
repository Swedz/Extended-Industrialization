package net.swedz.miextended.mi.hook.tracker;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.definition.FluidDefinition;
import aztech.modern_industrialization.machines.models.MachineCasing;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredItem;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
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
	
	public static final List<Consumer<LanguageProvider>>    LANGUAGE          = Lists.newArrayList();
	public static final Map<String, MachineModelProperties> MACHINE_MODELS    = Maps.newHashMap();
	public static final List<Consumer<ItemModelProvider>>   ITEM_MODELS       = Lists.newArrayList();
	public static final List<FluidDefinition>               FLUID_DEFINITIONS = Lists.newArrayList();
	
	public static void addLanguageEntry(Consumer<LanguageProvider> action)
	{
		if(!OPEN)
		{
			throw new IllegalStateException("Tried to add language entry while the tracker was closed.");
		}
		
		LANGUAGE.add(action);
	}
	
	public static void addItemLanguageEntry(DeferredItem<?> item, String englishName)
	{
		addLanguageEntry((provider) -> provider.add(item.asItem(), englishName));
	}
	
	public static void addMachineRecipeTypeLanguageEntry(String id, String englishName)
	{
		addLanguageEntry((provider) -> provider.add("rei_categories.%s.%s".formatted(MI.ID, id), englishName));
	}
	
	public static void addFluidDefinitionLanguageEntry(FluidDefinition fluidDefinition)
	{
		addLanguageEntry((provider) -> provider.add(fluidDefinition.asFluidBlock(), fluidDefinition.getEnglishName()));
	}
	
	public static void addMachineModel(String id, MachineCasing defaultCasing, String overlay, boolean front, boolean top, boolean side, boolean active)
	{
		if(!OPEN)
		{
			throw new IllegalStateException("Tried to add machine block model entry while the tracker was closed.");
		}
		
		MACHINE_MODELS.put(id, new MachineModelProperties(defaultCasing, overlay, front, top, side, active));
	}
	
	public static void addItemModel(DeferredItem<?> item, BiConsumer<Item, ItemModelProvider> modelProvider)
	{
		if(!OPEN)
		{
			throw new IllegalStateException("Tried to add item model entry while the tracker was closed.");
		}
		
		ITEM_MODELS.add((provider) -> modelProvider.accept(item.asItem(), provider));
	}
	
	public static void addFluidDefinition(FluidDefinition fluidDefinition)
	{
		if(!OPEN)
		{
			throw new IllegalStateException("Tried to add fluid definition entry while the tracker was closed.");
		}
		
		FLUID_DEFINITIONS.add(fluidDefinition);
	}
	
	public record MachineModelProperties(
			MachineCasing defaultCasing, String overlay, boolean front, boolean top, boolean side, boolean active
	)
	{
		public void addToMachineJson(JsonObject json)
		{
			json.addProperty("casing", defaultCasing.name);
			
			var defaultOverlays = new JsonObject();
			
			if(top)
			{
				defaultOverlays.addProperty("top", "modern_industrialization:block/machines/%s/overlay_top".formatted(overlay));
				if(active)
				{
					defaultOverlays.addProperty("top_active", "modern_industrialization:block/machines/%s/overlay_top_active".formatted(overlay));
				}
			}
			if(front)
			{
				defaultOverlays.addProperty("front", "modern_industrialization:block/machines/%s/overlay_front".formatted(overlay));
				if(active)
				{
					defaultOverlays.addProperty(
							"front_active",
							"modern_industrialization:block/machines/%s/overlay_front_active".formatted(overlay)
					);
				}
			}
			if(side)
			{
				defaultOverlays.addProperty("side", "modern_industrialization:block/machines/%s/overlay_side".formatted(overlay));
				if(active)
				{
					defaultOverlays.addProperty("side_active", "modern_industrialization:block/machines/%s/overlay_side_active".formatted(overlay));
				}
			}
			
			defaultOverlays.addProperty("output", "modern_industrialization:block/overlays/output");
			defaultOverlays.addProperty("item_auto", "modern_industrialization:block/overlays/item_auto");
			defaultOverlays.addProperty("fluid_auto", "modern_industrialization:block/overlays/fluid_auto");
			
			json.add("default_overlays", defaultOverlays);
		}
	}
}
