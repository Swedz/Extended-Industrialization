package net.swedz.extended_industrialization.client.model.chainer;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.compat.sodium.SodiumCompat;
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
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;

import java.util.IdentityHashMap;

public class MachineChainerBlockEntityRenderer implements BlockEntityRenderer<MachineChainerMachineBlockEntity>
{
	private final        BlockModelShaper                         blockModels;
	private              BlockState                               lastBlockState = null;
	private              MachineChainerBakedModel                 model          = null;
	private final        IdentityHashMap<MachineCasing, Object[]> quadCache      = new IdentityHashMap<>();
	private static final Object                                   NO_QUAD        = new Object();
	
	public MachineChainerBlockEntityRenderer(BlockEntityRendererProvider.Context ctx)
	{
		this.blockModels = ctx.getBlockRenderDispatcher().getBlockModelShaper();
	}
	
	private BakedQuad getCachedQuad(MachineModelClientData data, Direction d)
	{
		var facing = data.frontDirection;
		int cachedQuadIndex = facing.ordinal() * 6 + d.ordinal();
		var casing = data.casing;
		var cachedQuads = quadCache.computeIfAbsent(casing, c -> new Object[36]);
		
		if(cachedQuads[cachedQuadIndex] == null)
		{
			TextureAtlasSprite sprite = model == null ? null : MachineChainerBakedModel.getSprite(model.getSprites(casing), d, facing, true);
			if(sprite != null)
			{
				var vc = new QuadBakingVertexConsumer();
				cachedQuads[cachedQuadIndex] = ModelHelper.bakeSprite(vc, d, sprite, -2 * MachineChainerBakedModel.Z_OFFSET);
			}
			else
			{
				cachedQuads[cachedQuadIndex] = NO_QUAD;
			}
		}
		
		var quad = cachedQuads[cachedQuadIndex];
		return quad == NO_QUAD ? null : (BakedQuad) quad;
	}
	
	private MachineChainerBakedModel getMachineModel(BlockState state)
	{
		if(blockModels.getBlockModel(state) instanceof MachineChainerBakedModel mbm)
		{
			return mbm;
		}
		else
		{
			MI.LOGGER.warn("Model {} should have been a MachineChainerBakedModel, but was {}", state, blockModels.getBlockModel(state).getClass());
			return null;
		}
	}
	
	@Override
	public void render(MachineChainerMachineBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vcp, int light, int overlay)
	{
		BlockState state = entity.getBlockState();
		if(lastBlockState == null)
		{
			lastBlockState = state;
			model = getMachineModel(state);
		}
		else if(lastBlockState != state)
		{
			// Sanity check.
			throw new IllegalStateException("Tried to use the same machine BER with two block states: " + state + " and " + lastBlockState);
		}
		
		MachineModelClientData data = entity.getMachineModelData();
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
