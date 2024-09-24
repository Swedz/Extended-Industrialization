package net.swedz.extended_industrialization.client.model;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.compat.sodium.SodiumCompat;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.ModelHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.swedz.extended_industrialization.mixin.mi.accessor.MachineModelDataAccessor;

import java.util.IdentityHashMap;

public class ModularMachineBlockEntityRenderer<T extends MachineBlockEntity> implements BlockEntityRenderer<T>
{
	private static final Object NO_QUAD = new Object();
	
	private final BlockModelShaper                         blockModels;
	private final IdentityHashMap<MachineCasing, Object[]> quadCache = new IdentityHashMap<>();
	
	private BlockState               lastBlockState = null;
	private ModularMachineBakedModel model          = null;
	
	public ModularMachineBlockEntityRenderer(BlockEntityRendererProvider.Context ctx)
	{
		this.blockModels = ctx.getBlockRenderDispatcher().getBlockModelShaper();
	}
	
	private BakedQuad getCachedQuad(MachineModelClientData data, Direction direction)
	{
		Direction facing = data.frontDirection;
		int cachedQuadIndex = facing.ordinal() * 6 + direction.ordinal();
		MachineCasing casing = data.casing;
		Object[] cachedQuads = quadCache.computeIfAbsent(casing, c -> new Object[36]);
		
		if(cachedQuads[cachedQuadIndex] == null)
		{
			TextureAtlasSprite sprite = model == null ? null : model.getSprite(model.getSprites(casing), direction, facing, true);
			if(sprite != null)
			{
				QuadBakingVertexConsumer vc = new QuadBakingVertexConsumer();
				cachedQuads[cachedQuadIndex] = ModelHelper.bakeSprite(vc, direction, sprite, -2 * ModularMachineBakedModel.Z_OFFSET);
			}
			else
			{
				cachedQuads[cachedQuadIndex] = NO_QUAD;
			}
		}
		
		Object quad = cachedQuads[cachedQuadIndex];
		return quad == NO_QUAD ? null : (BakedQuad) quad;
	}
	
	private ModularMachineBakedModel getMachineModel(BlockState state)
	{
		if(blockModels.getBlockModel(state) instanceof ModularMachineBakedModel mbm)
		{
			return mbm;
		}
		else
		{
			MI.LOGGER.warn("Model {} should have been a ModularMachineBakedModel, but was {}", state, blockModels.getBlockModel(state).getClass());
			return null;
		}
	}
	
	@Override
	public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vcp, int light, int overlay)
	{
		BlockState state = entity.getBlockState();
		if(lastBlockState == null)
		{
			lastBlockState = state;
			model = getMachineModel(state);
		}
		else if(lastBlockState != state)
		{
			throw new IllegalStateException("Tried to use the same machine BER with two block states: " + state + " and " + lastBlockState);
		}
		
		MachineModelClientData data = ((MachineModelDataAccessor) entity).getMachineModelData();
		if(data.isActive)
		{
			VertexConsumer vc = vcp.getBuffer(Sheets.cutoutBlockSheet());
			
			for(Direction d : Direction.values())
			{
				BakedQuad quad = getCachedQuad(data, d);
				if(quad != null)
				{
					int faceLight = LevelRenderer.getLightColor(entity.getLevel(), entity.getBlockState(), entity.getBlockPos().relative(d));
					vc.putBulkData(matrices.last(), quad, 1.0f, 1.0f, 1.0f, 1.0f, faceLight, OverlayTexture.NO_OVERLAY);
					
					SodiumCompat.markSpriteActive(quad.getSprite());
				}
			}
		}
	}
	
	@Override
	public int getViewDistance()
	{
		return 256;
	}
}
