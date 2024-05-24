package net.swedz.extended_industrialization.machines.renderer.solarpanel;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.ModelHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class SolarPanelBakedModel implements IDynamicBakedModel
{
	public static float Z_OFFSET = 5e-4f; // Cannot be lower due to Embeddium compact vertex format
	
	private static final ChunkRenderTypeSet CUTOUT_MIPPED = ChunkRenderTypeSet.of(RenderType.cutoutMipped());
	
	public static final String CASING_FOLDER = "machine_casing";
	
	public static ResourceLocation getCasingModelId(MachineCasing casing)
	{
		return MI.id(CASING_FOLDER + "/" + casing.name);
	}
	
	public static BakedModel getCasingModel(MachineCasing casing)
	{
		return Minecraft.getInstance().getModelManager().getModel(getCasingModelId(casing));
	}
	
	private final MachineCasing                     baseCasing;
	private final TextureAtlasSprite[]              defaultOverlays;
	private final Map<String, TextureAtlasSprite[]> tieredOverlays;
	private final MachineModelClientData            defaultData;
	
	SolarPanelBakedModel(MachineCasing baseCasing,
						 TextureAtlasSprite[] defaultOverlays,
						 Map<String, TextureAtlasSprite[]> tieredOverlays)
	{
		this.baseCasing = baseCasing;
		this.defaultOverlays = defaultOverlays;
		this.tieredOverlays = tieredOverlays;
		this.defaultData = new MachineModelClientData(baseCasing, Direction.NORTH);
	}
	
	public MachineCasing getBaseCasing()
	{
		return baseCasing;
	}
	
	public TextureAtlasSprite[] getSprites(MachineCasing casing)
	{
		if(casing == null)
		{
			return defaultOverlays;
		}
		return tieredOverlays.getOrDefault(casing.name, defaultOverlays);
	}
	
	public static TextureAtlasSprite getSprite(TextureAtlasSprite[] sprites, Direction side, Direction facingDirection, boolean isActive)
	{
		int spriteId;
		if(side.getAxis().isHorizontal())
		{
			spriteId = (facingDirection.get2DDataValue() - side.get2DDataValue() + 4) % 4 * 2;
		}
		else
		{
			spriteId = (facingDirection.get2DDataValue() + 4) * 2;
			
			if(side == Direction.DOWN)
			{
				spriteId += 8;
			}
		}
		if(isActive)
		{
			spriteId++;
		}
		return sprites[spriteId];
	}
	
	@Override
	public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData)
	{
		var machineData = modelData.get(MachineModelClientData.KEY);
		if(machineData == null)
		{
			return modelData;
		}
		
		MachineCasing casing = Objects.requireNonNullElse(machineData.casing, baseCasing);
		return getCasingModel(casing).getModelData(level, pos, state, modelData);
	}
	
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand,
									ModelData extraData, RenderType renderType)
	{
		var data = extraData.get(MachineModelClientData.KEY);
		if(data == null)
		{
			data = defaultData;
		}
		
		MachineCasing casing = Objects.requireNonNullElse(data.casing, baseCasing);
		var sprites = getSprites(casing);
		
		List<BakedQuad> quads = new ArrayList<>();
		var vc = new QuadBakingVertexConsumer(quads::add);
		
		if(side != null)
		{
			// Casing
			quads.addAll(getCasingModel(casing).getQuads(state, side, rand, extraData, renderType));
			// Machine overlays
			TextureAtlasSprite sprite = getSprite(sprites, side, data.frontDirection, false);
			if(sprite != null)
			{
				ModelHelper.emitSprite(vc, side, sprite, -Z_OFFSET);
			}
		}
		
		// Output overlays
		if(data.outputDirection != null && side == data.outputDirection)
		{
			ModelHelper.emitSprite(vc, data.outputDirection, sprites[24], -3 * Z_OFFSET);
			if(data.itemAutoExtract)
			{
				ModelHelper.emitSprite(vc, data.outputDirection, sprites[25], -3 * Z_OFFSET);
			}
			if(data.fluidAutoExtract)
			{
				ModelHelper.emitSprite(vc, data.outputDirection, sprites[26], -3 * Z_OFFSET);
			}
		}
		
		return quads;
	}
	
	@Override
	public boolean useAmbientOcclusion()
	{
		return true;
	}
	
	@Override
	public boolean isGui3d()
	{
		return false;
	}
	
	@Override
	public boolean usesBlockLight()
	{
		return true;
	}
	
	@Override
	public boolean isCustomRenderer()
	{
		return false;
	}
	
	@Override
	public TextureAtlasSprite getParticleIcon()
	{
		return getCasingModel(baseCasing).getParticleIcon();
	}
	
	@Override
	public ItemTransforms getTransforms()
	{
		return ModelHelper.MODEL_TRANSFORM_BLOCK;
	}
	
	@Override
	public ItemOverrides getOverrides()
	{
		return ItemOverrides.EMPTY;
	}
	
	@Override
	public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data)
	{
		return CUTOUT_MIPPED;
	}
}
