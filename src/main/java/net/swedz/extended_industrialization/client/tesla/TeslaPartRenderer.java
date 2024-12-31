package net.swedz.extended_industrialization.client.tesla;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.renderable.BakedModelRenderable;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIClientConfig;
import net.swedz.extended_industrialization.EIClientRenderTypes;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaArcBehavior;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaArcBehaviorHolder;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaArcs;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaPlasmaBehavior;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaPlasmaBehaviorHolder;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkPart;
import net.swedz.tesseract.neoforge.api.WorldPos;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypes;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderType;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;
import team.lodestar.lodestone.systems.rendering.trail.TrailPoint;
import team.lodestar.lodestone.systems.rendering.trail.TrailPointBuilder;

import java.util.List;
import java.util.Optional;

final class TeslaPartRenderer
{
	private static void renderHighlight(MachineBlockEntity machine, float partialTick, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		BlockPos pos = machine.getBlockPos();
		
		getHeldNetworkKey().ifPresent((networkKey) ->
		{
			if(machine instanceof TeslaNetworkPart part &&
			   part.hasNetwork() && part.getNetworkKey().equals(networkKey))
			{
				matrices.pushPose();
				matrices.translate(-0.005, -0.005, -0.005);
				matrices.scale(1.01f, 1.01f, 1.01f);
				RenderHelper.drawOverlay(matrices, buffer, 111f / 256, 111f / 256, 1f, RenderHelper.FULL_LIGHT, overlay);
				matrices.popPose();
			}
		});
	}
	
	private static Optional<WorldPos> getHeldNetworkKey()
	{
		Player player = Minecraft.getInstance().player;
		return player.getMainHandItem().has(EIComponents.SELECTED_TESLA_NETWORK) ? Optional.of(player.getMainHandItem().get(EIComponents.SELECTED_TESLA_NETWORK).key()) :
				player.getOffhandItem().has(EIComponents.SELECTED_TESLA_NETWORK) ? Optional.of(player.getOffhandItem().get(EIComponents.SELECTED_TESLA_NETWORK).key()) : Optional.empty();
	}
	
	private static final LodestoneRenderType TESLA_ARC = LodestoneRenderTypes.TRANSPARENT_TEXTURE.applyAndCache(RenderTypeToken.createToken(EI.id("textures/vfx/tesla_arc.png")));
	
	private static void renderArcs(MachineBlockEntity machine, float partialTick, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		if(machine instanceof TeslaArcBehaviorHolder holder)
		{
			TeslaArcBehavior behavior = holder.getTeslaArcBehavior();
			if(behavior.shouldRender())
			{
				VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld();
				builder.replaceBufferSource(RenderHandler.LATE_DELAYED_RENDER.getTarget())
						.setRenderType(TESLA_ARC)
						.setColorRaw(1f, 1f, 1f);
				
				TeslaArcs arcs = behavior.getArcs();
				for(TrailPointBuilder trail : arcs.getTrails())
				{
					List<TrailPoint> points = trail.getTrailPoints();
					if(points.size() < 2)
					{
						continue;
					}
					int ticks = points.getFirst().getTimeActive();
					builder.setAlpha(0.9f * (ticks == 0 ? partialTick : ticks == arcs.duration() ? (1 - partialTick) : 1));
					int halfPoints = points.size() / 2;
					if(ticks == 0 || ticks == 1)
					{
						points = points.subList(0, (int) (halfPoints * partialTick) + (ticks == 1 ? halfPoints : 0));
					}
					
					matrices.pushPose();
					
					builder.renderTrail(matrices, points, (i) -> (1 - i) * arcs.widthScale());
					
					matrices.popPose();
				}
			}
		}
	}
	
	private static BakedModelRenderable getModel(ResourceLocation location)
	{
		return BakedModelRenderable.of(ModelResourceLocation.standalone(location));
	}
	
	private static void renderPlasma(MachineBlockEntity machine, float partialTick, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		if(machine instanceof TeslaPlasmaBehaviorHolder holder)
		{
			TeslaPlasmaBehavior behavior = holder.getTeslaPlasmaBehavior();
			if(behavior.shouldRender())
			{
				matrices.pushPose();
				
				Vec3 offset = behavior.getOffset();
				matrices.translate(offset.x(), offset.y(), offset.z());
				
				float modelScale = behavior.getModelScale();
				matrices.scale(modelScale, modelScale, modelScale);
				
				var model = getModel(behavior.getModelLocation());
				float textureScale = behavior.getTextureScale();
				float speed = behavior.getSpeed();
				model.render(matrices, buffer, (texture) -> EIClientRenderTypes.TESLA_PLASMA.apply(textureScale, speed), light, overlay, partialTick, new BakedModelRenderable.Context(ModelData.EMPTY));
				
				matrices.popPose();
			}
		}
	}
	
	static void render(MachineBlockEntity machine, float partialTick, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		renderHighlight(machine, partialTick, matrices, buffer, light, overlay);
		if(EIClientConfig.renderTeslaAnimations)
		{
			renderArcs(machine, partialTick, matrices, buffer, light, overlay);
			renderPlasma(machine, partialTick, matrices, buffer, light, overlay);
		}
	}
}
