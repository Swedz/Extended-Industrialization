package net.swedz.extended_industrialization.mi.hook.tracker;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.machines.models.MachineCasing;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.swedz.extended_industrialization.EI;
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
				defaultOverlays.addProperty("top", "%s:block/machines/%s/overlay_top".formatted(EI.ID, overlay));
				if(active)
				{
					defaultOverlays.addProperty("top_active", "%s:block/machines/%s/overlay_top_active".formatted(EI.ID, overlay));
				}
			}
			if(front)
			{
				defaultOverlays.addProperty("front", "%s:block/machines/%s/overlay_front".formatted(EI.ID, overlay));
				if(active)
				{
					defaultOverlays.addProperty(
							"front_active",
							"%s:block/machines/%s/overlay_front_active".formatted(EI.ID, overlay)
					);
				}
			}
			if(side)
			{
				defaultOverlays.addProperty("side", "%s:block/machines/%s/overlay_side".formatted(EI.ID, overlay));
				if(active)
				{
					defaultOverlays.addProperty("side_active", "%s:block/machines/%s/overlay_side_active".formatted(EI.ID, overlay));
				}
			}
			
			defaultOverlays.addProperty("output", "%s:block/overlays/output".formatted(MI.ID));
			defaultOverlays.addProperty("item_auto", "%s:block/overlays/item_auto".formatted(MI.ID));
			defaultOverlays.addProperty("fluid_auto", "%s:block/overlays/fluid_auto".formatted(MI.ID));
			
			json.add("default_overlays", defaultOverlays);
		}
	}
}
