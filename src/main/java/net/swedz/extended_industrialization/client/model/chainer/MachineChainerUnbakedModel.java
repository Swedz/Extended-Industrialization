package net.swedz.extended_industrialization.client.model.chainer;

import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineCasings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.swedz.extended_industrialization.EI;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MachineChainerUnbakedModel implements IUnbakedGeometry<MachineChainerUnbakedModel>
{
	public static final ResourceLocation                            LOADER_ID = EI.id("machine_chainer");
	public static final IGeometryLoader<MachineChainerUnbakedModel> LOADER    = (jsonObject, deserializationContext) -> new MachineChainerUnbakedModel(jsonObject);
	
	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();
	
	private final MachineCasing           baseCasing;
	private final Material[]              defaultOverlays;
	private final Map<String, Material[]> tieredOverlays = new HashMap<>();
	
	private MachineChainerUnbakedModel(JsonObject obj)
	{
		this.baseCasing = MachineCasings.get(GsonHelper.getAsString(obj, "casing"));
		
		var defaultOverlaysJson = OverlaysJson.parse(GsonHelper.getAsJsonObject(obj, "default_overlays"), null);
		this.defaultOverlays = defaultOverlaysJson.toSpriteIds();
		
		var tieredOverlays = GsonHelper.getAsJsonObject(obj, "tiered_overlays", new JsonObject());
		for(var casingTier : tieredOverlays.keySet())
		{
			var casingOverlaysJson = OverlaysJson.parse(GsonHelper.getAsJsonObject(tieredOverlays, casingTier), defaultOverlaysJson);
			this.tieredOverlays.put(casingTier, casingOverlaysJson.toSpriteIds());
		}
	}
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter,
						   ModelState modelState, ItemOverrides overrides)
	{
		var defaultOverlays = loadSprites(spriteGetter, this.defaultOverlays);
		var tieredOverlays = new HashMap<String, TextureAtlasSprite[]>();
		for(var entry : this.tieredOverlays.entrySet())
		{
			tieredOverlays.put(entry.getKey(), loadSprites(spriteGetter, entry.getValue()));
		}
		return new MachineChainerBakedModel(baseCasing, defaultOverlays, tieredOverlays);
	}
	
	private static TextureAtlasSprite[] loadSprites(Function<Material, TextureAtlasSprite> textureGetter, Material[] ids)
	{
		var sprites = new TextureAtlasSprite[ids.length];
		for(int i = 0; i < ids.length; ++i)
		{
			if(ids[i] != null)
			{
				sprites[i] = textureGetter.apply(ids[i]);
			}
		}
		return sprites;
	}
	
	private static class OverlaysJson
	{
		private ResourceLocation front;
		private ResourceLocation back;
		private ResourceLocation up;
		private ResourceLocation down;
		private ResourceLocation left;
		private ResourceLocation right;
		private ResourceLocation output;
		private ResourceLocation item_auto;
		private ResourceLocation fluid_auto;
		
		private static OverlaysJson parse(JsonObject json, OverlaysJson defaultOverlay)
		{
			var overlays = GSON.fromJson(json, OverlaysJson.class);
			
			if(defaultOverlay != null)
			{
				// Copy null fields from the default.
				try
				{
					for(var field : OverlaysJson.class.getDeclaredFields())
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
		
		private Material[] toSpriteIds()
		{
			return new Material[]{
					select(front),
					select(back),
					select(up),
					select(down),
					select(left),
					select(right),
					select(output),
					select(item_auto),
					select(fluid_auto),
			};
		}
		
		private static Material select(@Nullable ResourceLocation... candidates)
		{
			for(var id : candidates)
			{
				if(id != null)
				{
					return new Material(InventoryMenu.BLOCK_ATLAS, id);
				}
			}
			return null;
		}
	}
}