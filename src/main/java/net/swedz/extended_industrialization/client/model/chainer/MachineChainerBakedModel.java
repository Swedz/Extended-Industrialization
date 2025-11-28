package net.swedz.extended_industrialization.client.model.chainer;

import aztech.modern_industrialization.client.machines.models.MachineBakedModel;
import aztech.modern_industrialization.client.util.ModelHelper;
import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Based on {@link MachineBakedModel}
 */
public final class MachineChainerBakedModel implements IDynamicBakedModel
{
	public static float Z_OFFSET = 5e-4f; // Cannot be lower due to Embeddium compact vertex format
	
	private static final ChunkRenderTypeSet CUTOUT_MIPPED = ChunkRenderTypeSet.of(RenderType.cutoutMipped());
	
	private final MachineCasing              casing;
	private final MachineChainerOverlaysJson overlaysJson;
	private final TextureAtlasSprite[]       overlays;
	private final MachineModelClientData     defaultData;
	
	MachineChainerBakedModel(MachineCasing casing, MachineChainerOverlaysJson overlaysJson, TextureAtlasSprite[] overlays)
	{
		this.casing = casing;
		this.overlaysJson = overlaysJson;
		this.overlays = overlays;
		this.defaultData = new MachineModelClientData(casing, Direction.NORTH);
	}
	
	public TextureAtlasSprite[] getSprites()
	{
		return overlays;
	}
	
	public static TextureAtlasSprite getSprite(TextureAtlasSprite[] sprites, Direction side, Direction facingDirection, boolean isActive)
	{
		int spriteId = 1;
		if(side == facingDirection)
		{
			spriteId = 0;
		}
		else if(side == facingDirection.getOpposite())
		{
			spriteId = 1;
		}
		else if(side.getAxis().isHorizontal())
		{
			if(facingDirection.getAxis().isVertical())
			{
				spriteId = facingDirection == Direction.UP ? 2 : 3;
			}
			else
			{
				spriteId = switch (facingDirection)
				{
					case NORTH -> side == Direction.WEST ? 4 : 5;
					case SOUTH -> side == Direction.WEST ? 5 : 4;
					case WEST -> side == Direction.NORTH ? 5 : 4;
					case EAST -> side == Direction.NORTH ? 4 : 5;
					default -> throw new IllegalStateException("Unexpected value: " + facingDirection);
				};
			}
		}
		else if(side.getAxis().isVertical() && facingDirection.getAxis().isHorizontal())
		{
			spriteId = switch (facingDirection)
			{
				case NORTH -> side == Direction.UP ? 2 : 3;
				case SOUTH -> side == Direction.UP ? 3 : 2;
				case WEST -> 4;
				case EAST -> 5;
				default -> throw new IllegalStateException("Unexpected value: " + facingDirection);
			};
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
		MachineCasing casing = Objects.requireNonNullElse(machineData.casing, this.casing);
		return MachineBakedModel.getCasingModel(casing).getModelData(level, pos, state, modelData);
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
		MachineCasing casing = Objects.requireNonNullElse(data.casing, this.casing);
		
		var sprites = this.getSprites();
		
		List<BakedQuad> quads = new ArrayList<>();
		var vc = new QuadBakingVertexConsumer();
		
		if(side != null)
		{
			quads.addAll(MachineBakedModel.getCasingModel(casing).getQuads(state, side, rand, extraData, renderType));
			TextureAtlasSprite sprite = getSprite(sprites, side, data.frontDirection, false);
			if(sprite != null)
			{
				quads.add(ModelHelper.bakeSprite(vc, side, sprite, -Z_OFFSET));
			}
		}
		
		if(data.outputDirection != null && side == data.outputDirection)
		{
			var outputIndexes = overlaysJson.getOutputSpriteIndexes();
			quads.add(ModelHelper.bakeSprite(vc, data.outputDirection, sprites[outputIndexes[0]], -3 * Z_OFFSET));
			if(data.itemAutoExtract)
			{
				quads.add(ModelHelper.bakeSprite(vc, data.outputDirection, sprites[outputIndexes[1]], -3 * Z_OFFSET));
			}
			if(data.fluidAutoExtract)
			{
				quads.add(ModelHelper.bakeSprite(vc, data.outputDirection, sprites[outputIndexes[2]], -3 * Z_OFFSET));
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
	
	@SuppressWarnings("deprecation")
	@Override
	public TextureAtlasSprite getParticleIcon()
	{
		return MachineBakedModel.getCasingModel(casing).getParticleIcon();
	}
	
	@SuppressWarnings("deprecation")
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