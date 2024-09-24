package net.swedz.extended_industrialization.client.model;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.ModelHelper;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModularMachineBakedModel implements IDynamicBakedModel
{
	public static float Z_OFFSET = 5e-4f;
	
	private static final ChunkRenderTypeSet CUTOUT_MIPPED = ChunkRenderTypeSet.of(RenderType.cutoutMipped());
	
	public static final String CASING_FOLDER = "machine_casing";
	
	public static ModelResourceLocation getCasingModelId(MachineCasing casing)
	{
		return ModelResourceLocation.standalone(MI.id(CASING_FOLDER + "/" + casing.name));
	}
	
	public static BakedModel getCasingModel(MachineCasing casing)
	{
		return Minecraft.getInstance().getModelManager().getModel(getCasingModelId(casing));
	}
	
	private final MachineCasing                     baseCasing;
	private final int[]                             outputOverlayIndexes;
	private final TextureAtlasSprite[]              defaultOverlays;
	private final Map<String, TextureAtlasSprite[]> tieredOverlays;
	private final MachineModelClientData            defaultData;
	
	public ModularMachineBakedModel(MachineCasing baseCasing,
									int[] outputOverlayIndexes, TextureAtlasSprite[] defaultOverlays,
									Map<String, TextureAtlasSprite[]> tieredOverlays)
	{
		this.baseCasing = baseCasing;
		this.outputOverlayIndexes = outputOverlayIndexes;
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
	
	public TextureAtlasSprite getSprite(TextureAtlasSprite[] sprites, Direction side, Direction facingDirection, boolean isActive)
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
		MachineModelClientData machineData = modelData.get(MachineModelClientData.KEY);
		if(machineData == null)
		{
			return modelData;
		}
		MachineCasing casing = Objects.requireNonNullElse(machineData.casing, baseCasing);
		return getCasingModel(casing).getModelData(level, pos, state, modelData);
	}
	
	protected List<BakedQuad> getSideQuads(BlockState state, Direction side, RandomSource random,
										   ModelData extraData, RenderType renderType,
										   MachineModelClientData data, MachineCasing casing,
										   TextureAtlasSprite[] sprites,
										   QuadBakingVertexConsumer vertexConsumer)
	{
		List<BakedQuad> quads = Lists.newArrayList();
		
		if(side != null)
		{
			quads.addAll(getCasingModel(casing).getQuads(state, side, random, extraData, renderType));
			
			TextureAtlasSprite sprite = this.getSprite(sprites, side, data.frontDirection, false);
			if(sprite != null)
			{
				quads.add(ModelHelper.bakeSprite(vertexConsumer, side, sprite, -Z_OFFSET));
			}
		}
		
		return quads;
	}
	
	protected List<BakedQuad> getOutputQuads(BlockState state, Direction side, RandomSource random,
											 ModelData extraData, RenderType renderType,
											 MachineModelClientData data, MachineCasing casing,
											 TextureAtlasSprite[] sprites,
											 QuadBakingVertexConsumer vertexConsumer)
	{
		List<BakedQuad> quads = Lists.newArrayList();
		
		if(data.outputDirection != null && side == data.outputDirection)
		{
			quads.add(ModelHelper.bakeSprite(vertexConsumer, data.outputDirection, sprites[outputOverlayIndexes[0]], -3 * Z_OFFSET));
			if(data.itemAutoExtract)
			{
				quads.add(ModelHelper.bakeSprite(vertexConsumer, data.outputDirection, sprites[outputOverlayIndexes[1]], -3 * Z_OFFSET));
			}
			if(data.fluidAutoExtract)
			{
				quads.add(ModelHelper.bakeSprite(vertexConsumer, data.outputDirection, sprites[outputOverlayIndexes[2]], -3 * Z_OFFSET));
			}
		}
		
		return quads;
	}
	
	protected List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random,
									   ModelData extraData, RenderType renderType,
									   MachineModelClientData data, MachineCasing casing,
									   TextureAtlasSprite[] sprites,
									   QuadBakingVertexConsumer vertexConsumer)
	{
		List<BakedQuad> quads = Lists.newArrayList();
		
		quads.addAll(this.getSideQuads(state, side, random, extraData, renderType, data, casing, sprites, vertexConsumer));
		
		quads.addAll(this.getOutputQuads(state, side, random, extraData, renderType, data, casing, sprites, vertexConsumer));
		
		return quads;
	}
	
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random,
									ModelData extraData, RenderType renderType)
	{
		MachineModelClientData data = extraData.get(MachineModelClientData.KEY);
		if(data == null)
		{
			data = defaultData;
		}
		
		MachineCasing casing = Objects.requireNonNullElse(data.casing, baseCasing);
		TextureAtlasSprite[] sprites = this.getSprites(casing);
		
		List<BakedQuad> quads = Lists.newArrayList();
		QuadBakingVertexConsumer vertexConsumer = new QuadBakingVertexConsumer();
		
		quads.addAll(this.getQuads(state, side, random, extraData, renderType, data, casing, sprites, vertexConsumer));
		
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
