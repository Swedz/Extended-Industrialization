package net.swedz.miextended.datagen.api.object;

import aztech.modern_industrialization.machines.models.MachineCasing;
import com.google.gson.JsonObject;
import net.swedz.miextended.datagen.api.DatagenOutputTarget;
import net.swedz.miextended.datagen.api.DatagenProvider;

public class DatagenModelWrapper extends DatagenJsonObjectWrapper
{
	public DatagenModelWrapper(DatagenProvider provider, String path, String name)
	{
		super(provider, DatagenOutputTarget.RESOURCE_PACK, (p) -> p.resolve(path).resolve(name + ".json"));
	}
	
	public void blankBlockState(String blockId)
	{
		JsonObject variants = new JsonObject();
		JsonObject model = new JsonObject();
		model.addProperty("model", "%s:%s/%s".formatted(provider.modId(), "block", blockId));
		variants.add("", model);
		this.get().add("variants", variants);
	}
	
	public void modernIndustrializationMachineBlockModel(String id, String overlay,
														 MachineCasing defaultCasing,
														 boolean front, boolean top, boolean side, boolean active)
	{
		this.get().addProperty("loader", "%s:%s".formatted(provider.modId(), "machine"));
		
		this.get().addProperty("casing", defaultCasing.name);
		
		JsonObject defaultOverlays = new JsonObject();
		
		if(top)
		{
			defaultOverlays.addProperty("top", "%s:block/machines/%s/overlay_top".formatted(provider.modId(), overlay));
			if(active)
			{
				defaultOverlays.addProperty("top_active", "%s:block/machines/%s/overlay_top_active".formatted(provider.modId(), overlay));
			}
		}
		if(front)
		{
			defaultOverlays.addProperty("front", "%s:block/machines/%s/overlay_front".formatted(provider.modId(), overlay));
			if(active)
			{
				defaultOverlays.addProperty(
						"front_active",
						"%s:block/machines/%s/overlay_front_active".formatted(provider.modId(), overlay)
				);
			}
		}
		if(side)
		{
			defaultOverlays.addProperty("side", "%s:block/machines/%s/overlay_side".formatted(provider.modId(), overlay));
			if(active)
			{
				defaultOverlays.addProperty("side_active", "%s:block/machines/%s/overlay_side_active".formatted(provider.modId(), overlay));
			}
		}
		
		defaultOverlays.addProperty("output", "%s:block/overlays/output".formatted(provider.modId()));
		defaultOverlays.addProperty("item_auto", "%s:block/overlays/item_auto".formatted(provider.modId()));
		defaultOverlays.addProperty("fluid_auto", "%s:block/overlays/fluid_auto".formatted(provider.modId()));
		
		this.get().add("default_overlays", defaultOverlays);
	}
	
	public void parentBlockItemModel(String blockId)
	{
		this.get().addProperty("parent", "%s:block/%s".formatted(provider.modId(), blockId));
	}
	
	public void standardItemGenerated(String itemId)
	{
		this.get().addProperty("parent", "minecraft:item/generated");
		JsonObject textures = new JsonObject();
		textures.addProperty("layer0", "%s:item/%s".formatted(provider.modId(), itemId));
		this.get().add("textures", textures);
	}
}
