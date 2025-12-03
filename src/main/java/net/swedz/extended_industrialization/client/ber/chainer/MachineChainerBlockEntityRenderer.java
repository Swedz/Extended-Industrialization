package net.swedz.extended_industrialization.client.ber.chainer;

import aztech.modern_industrialization.client.compat.sodium.SodiumCompat;
import aztech.modern_industrialization.client.util.ModelHelper;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.client.model.chainer.MachineChainerBakedModel;
import net.swedz.extended_industrialization.compat.continuity.ContinuityModelUnwrapper;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;

/**
 * Based on {@link aztech.modern_industrialization.client.machines.MachineBlockEntityRenderer}
 */
public sealed class MachineChainerBlockEntityRenderer implements BlockEntityRenderer<MachineChainerMachineBlockEntity> permits MachineChainerHighlightRenderer
{
	private final        BlockModelShaper         blockModels;
	private              BlockState               lastBlockState = null;
	private              MachineChainerBakedModel model          = null;
	private final        Object[]                 quadCache      = new Object[36];
	private static final Object                   NO_QUAD        = new Object();
	
	public MachineChainerBlockEntityRenderer(BlockEntityRendererProvider.Context context)
	{
		blockModels = context.getBlockRenderDispatcher().getBlockModelShaper();
	}
	
	private BakedQuad getCachedQuad(MachineModelClientData data, Direction direction)
	{
		var facing = data.frontDirection;
		int cachedQuadIndex = facing.ordinal() * 6 + direction.ordinal();
		
		if(quadCache[cachedQuadIndex] == null)
		{
			TextureAtlasSprite sprite = model == null ? null : MachineChainerBakedModel.getSprite(model.getSprites(), direction, facing, true);
			if(sprite != null)
			{
				var vc = new QuadBakingVertexConsumer();
				quadCache[cachedQuadIndex] = ModelHelper.bakeSprite(vc, direction, sprite, -2 * MachineChainerBakedModel.Z_OFFSET);
			}
			else
			{
				quadCache[cachedQuadIndex] = NO_QUAD;
			}
		}
		
		var quad = quadCache[cachedQuadIndex];
		return quad == NO_QUAD ? null : (BakedQuad) quad;
	}
	
	private MachineChainerBakedModel getMachineModel(BlockState state)
	{
		var model = blockModels.getBlockModel(state);
		model = ContinuityModelUnwrapper.unwrap(model);
		if(model instanceof MachineChainerBakedModel machineModel)
		{
			return machineModel;
		}
		EI.LOGGER.warn("Model {} should have been a MachineChainerBakedModel, but was {}", state, model.getClass());
		return null;
	}
	
	@Override
	public void render(MachineChainerMachineBlockEntity machine, float tickDelta, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		var state = machine.getBlockState();
		if(lastBlockState == null)
		{
			lastBlockState = state;
			model = this.getMachineModel(state);
		}
		else if(lastBlockState != state)
		{
			throw new IllegalStateException("Tried to use the same machine BER with two block states: " + state + " and " + lastBlockState);
		}
		
		var data = machine.getModelData().get(MachineModelClientData.KEY);
		if(data.isActive)
		{
			var vc = buffer.getBuffer(Sheets.cutoutBlockSheet());
			
			for(var direction : Direction.values())
			{
				var quad = this.getCachedQuad(data, direction);
				if(quad != null)
				{
					int faceLight = LevelRenderer.getLightColor(machine.getLevel(), machine.getBlockState(), machine.getBlockPos().relative(direction));
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
