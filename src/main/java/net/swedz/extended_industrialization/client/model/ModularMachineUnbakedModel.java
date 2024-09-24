package net.swedz.extended_industrialization.client.model;

import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineCasings;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ModularMachineUnbakedModel<O extends ModularOverlaysJson> implements IUnbakedGeometry<ModularMachineUnbakedModel<O>>
{
	private final ModularMachineModelBaker modelBaker;
	
	private final MachineCasing           baseCasing;
	private final int[]                   outputOverlayIndexes;
	private final Material[]              defaultOverlays;
	private final Map<String, Material[]> tieredOverlays = new HashMap<>();
	
	public ModularMachineUnbakedModel(Class<O> overlayClass, ModularMachineModelBaker modelBaker, JsonObject json)
	{
		this.modelBaker = modelBaker;
		
		this.baseCasing = MachineCasings.get(GsonHelper.getAsString(json, "casing"));
		
		O defaultOverlaysJson = ModularOverlaysJson.parse(overlayClass, GsonHelper.getAsJsonObject(json, "default_overlays"), null);
		this.outputOverlayIndexes = defaultOverlaysJson.getOutputSpriteIndexes();
		this.defaultOverlays = defaultOverlaysJson.toSpriteIds();
		
		JsonObject tieredOverlays = GsonHelper.getAsJsonObject(json, "tiered_overlays", new JsonObject());
		for(String casingTier : tieredOverlays.keySet())
		{
			O casingOverlaysJson = ModularOverlaysJson.parse(overlayClass, GsonHelper.getAsJsonObject(tieredOverlays, casingTier), defaultOverlaysJson);
			this.tieredOverlays.put(casingTier, casingOverlaysJson.toSpriteIds());
		}
	}
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter,
						   ModelState modelState, ItemOverrides overrides)
	{
		TextureAtlasSprite[] defaultOverlays = loadSprites(spriteGetter, this.defaultOverlays);
		HashMap<String, TextureAtlasSprite[]> tieredOverlays = Maps.newHashMap();
		this.tieredOverlays.forEach((key, sprites) ->
				tieredOverlays.put(key, loadSprites(spriteGetter, sprites)));
		return modelBaker.bake(baseCasing, outputOverlayIndexes, defaultOverlays, tieredOverlays);
	}
	
	private static TextureAtlasSprite[] loadSprites(Function<Material, TextureAtlasSprite> textureGetter, Material[] ids)
	{
		TextureAtlasSprite[] sprites = new TextureAtlasSprite[ids.length];
		for(int i = 0; i < ids.length; ++i)
		{
			if(ids[i] != null)
			{
				sprites[i] = textureGetter.apply(ids[i]);
			}
		}
		return sprites;
	}
}
