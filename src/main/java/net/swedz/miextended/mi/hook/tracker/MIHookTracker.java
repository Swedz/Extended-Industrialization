package net.swedz.miextended.mi.hook.tracker;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.machines.models.MachineCasing;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.apache.commons.compress.utils.Lists;

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
	
	public static final List<Consumer<LanguageProvider>>    LANGUAGE       = Lists.newArrayList();
	public static final Map<String, MachineModelProperties> MACHINE_MODELS = Maps.newHashMap();
	
	public static void addLanguageEntry(Consumer<LanguageProvider> action)
	{
		if(!OPEN)
		{
			throw new IllegalStateException("Tried to add language entry while the tracker was closed.");
		}
		
		LANGUAGE.add(action);
	}
	
	public static void addMachineRecipeTypeLanguageEntry(String id, String englishName)
	{
		addLanguageEntry((provider) -> provider.add("rei_categories.%s.%s".formatted(MI.ID, id), englishName));
	}
	
	public static void addMachineModel(String id, MachineCasing defaultCasing, String overlay, boolean front, boolean top, boolean side, boolean active)
	{
		if(!OPEN)
		{
			throw new IllegalStateException("Tried to add machine block model entry while the tracker was closed.");
		}
		
		MACHINE_MODELS.put(id, new MachineModelProperties(defaultCasing, overlay, front, top, side, active));
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
				defaultOverlays.addProperty("top", "miextended:block/machines/%s/overlay_top".formatted(overlay));
				if(active)
				{
					defaultOverlays.addProperty("top_active", "miextended:block/machines/%s/overlay_top_active".formatted(overlay));
				}
			}
			if(front)
			{
				defaultOverlays.addProperty("front", "miextended:block/machines/%s/overlay_front".formatted(overlay));
				if(active)
				{
					defaultOverlays.addProperty(
							"front_active",
							"miextended:block/machines/%s/overlay_front_active".formatted(overlay)
					);
				}
			}
			if(side)
			{
				defaultOverlays.addProperty("side", "miextended:block/machines/%s/overlay_side".formatted(overlay));
				if(active)
				{
					defaultOverlays.addProperty("side_active", "miextended:block/machines/%s/overlay_side_active".formatted(overlay));
				}
			}
			
			defaultOverlays.addProperty("output", "modern_industrialization:block/overlays/output");
			defaultOverlays.addProperty("item_auto", "modern_industrialization:block/overlays/item_auto");
			defaultOverlays.addProperty("fluid_auto", "modern_industrialization:block/overlays/fluid_auto");
			
			json.add("default_overlays", defaultOverlays);
		}
	}
}
