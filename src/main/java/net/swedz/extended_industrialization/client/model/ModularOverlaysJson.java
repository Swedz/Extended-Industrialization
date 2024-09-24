package net.swedz.extended_industrialization.client.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.lang.reflect.Field;

public interface ModularOverlaysJson
{
	Material[] toSpriteIds();
	
	int[] getOutputSpriteIndexes();
	
	Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();
	
	static <O extends ModularOverlaysJson> O parse(Class<O> clazz, JsonObject json, ModularOverlaysJson defaultOverlay)
	{
		O overlays = GSON.fromJson(json, clazz);
		
		if(defaultOverlay != null)
		{
			try
			{
				for(Field field : clazz.getDeclaredFields())
				{
					if(field.get(overlays) == null)
					{
						field.set(overlays, field.get(defaultOverlay));
					}
				}
			}
			catch (IllegalAccessException ex)
			{
				throw new RuntimeException("Failed to copy fields from default overlay", ex);
			}
		}
		
		return overlays;
	}
	
	default Material select(ResourceLocation... candidates)
	{
		for(ResourceLocation id : candidates)
		{
			if(id != null)
			{
				return new Material(InventoryMenu.BLOCK_ATLAS, id);
			}
		}
		return null;
	}
}
