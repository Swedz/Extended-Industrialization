package net.swedz.extended_industrialization.client.model.tesla;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.IModelBuilder;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.SimpleUnbakedGeometry;
import net.neoforged.neoforge.client.textures.UnitTextureAtlasSprite;
import net.swedz.extended_industrialization.EI;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * This is basically a {@link net.neoforged.neoforge.client.model.EmptyModel} but the particle icon is an empty texture.
 */
public final class TeslaParticleGeneratorModel extends SimpleUnbakedGeometry<TeslaParticleGeneratorModel>
{
	private static final BakedModel BAKED = new Baked();
	
	public static final ResourceLocation                             LOADER_ID = EI.id("tesla_particle_generator");
	public static final TeslaParticleGeneratorModel                  INSTANCE  = new TeslaParticleGeneratorModel();
	public static final IGeometryLoader<TeslaParticleGeneratorModel> LOADER    = (json, ctx) -> INSTANCE;
	
	private TeslaParticleGeneratorModel()
	{
	}
	
	@Override
	protected void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform)
	{
	}
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
	{
		return BAKED;
	}
	
	private static class Baked extends SimpleBakedModel
	{
		private static final Material TEXTURE = new Material(InventoryMenu.BLOCK_ATLAS, EI.id("block/tesla_particle_generator"));
		
		// SimpleBakedModel must have a quad list per face in its map.
		private static Map<Direction, List<BakedQuad>> makeEmptyCulledFaces()
		{
			Map<Direction, List<BakedQuad>> map = new EnumMap<>(Direction.class);
			for(Direction direction : Direction.values())
			{
				map.put(direction, List.of());
			}
			return map;
		}
		
		public Baked()
		{
			super(List.of(), makeEmptyCulledFaces(), false, false, false, UnitTextureAtlasSprite.INSTANCE, ItemTransforms.NO_TRANSFORMS, ItemOverrides.EMPTY, RenderTypeGroup.EMPTY);
		}
		
		@Override
		public TextureAtlasSprite getParticleIcon()
		{
			return TEXTURE.sprite();
		}
	}
}
