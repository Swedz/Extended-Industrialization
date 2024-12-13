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
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.client.model.MachineOverlaysJson;

import java.util.function.Function;

/**
 * Based on {@link aztech.modern_industrialization.machines.models.MachineUnbakedModel}
 */
public final class MachineChainerUnbakedModel implements IUnbakedGeometry<MachineChainerUnbakedModel>
{
	public static final ResourceLocation                            LOADER_ID = EI.id("machine_chainer");
	public static final IGeometryLoader<MachineChainerUnbakedModel> LOADER    = (json, context) -> new MachineChainerUnbakedModel(json);
	
	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();
	
	private final MachineCasing              casing;
	private final MachineChainerOverlaysJson overlaysJson;
	private final Material[]                 overlays;
	
	private MachineChainerUnbakedModel(JsonObject json)
	{
		casing = MachineCasings.get(ResourceLocation.parse(GsonHelper.getAsString(json, "casing")));
		
		overlaysJson = MachineOverlaysJson.parse(MachineChainerOverlaysJson.class, GsonHelper.getAsJsonObject(json, "overlays"), null);
		overlays = overlaysJson.toSpriteIds();
	}
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter,
						   ModelState modelState, ItemOverrides overrides)
	{
		var overlays = loadSprites(spriteGetter, this.overlays);
		return new MachineChainerBakedModel(casing, overlaysJson, overlays);
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
}
