package net.swedz.extended_industrialization.client.tesla;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.util.RenderHelper;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIClientConfig;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.api.WorldPos;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaArcBehavior;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaArcBehaviorHolder;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaArcs;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaPlasmaBehavior;
import net.swedz.extended_industrialization.client.tesla.generator.TeslaPlasmaBehaviorHolder;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkPart;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypes;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderType;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;
import team.lodestar.lodestone.systems.rendering.trail.TrailPoint;
import team.lodestar.lodestone.systems.rendering.trail.TrailPointBuilder;

import java.util.List;
import java.util.Optional;

import static net.minecraft.client.renderer.RenderStateShard.*;

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
		return player.getMainHandItem().has(EIComponents.SELECTED_TESLA_NETWORK) ? Optional.of(player.getMainHandItem().get(EIComponents.SELECTED_TESLA_NETWORK)) :
				player.getOffhandItem().has(EIComponents.SELECTED_TESLA_NETWORK) ? Optional.of(player.getOffhandItem().get(EIComponents.SELECTED_TESLA_NETWORK)) : Optional.empty();
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
	
	private static RenderType getPlasmaRenderType(float u, float v)
	{
		return RenderType.create(
				"plasma", DefaultVertexFormat.NEW_ENTITY,
				VertexFormat.Mode.QUADS, 1536,
				false, true,
				RenderType.CompositeState.builder()
						.setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
						.setTextureState(new RenderStateShard.TextureStateShard(EI.id("textures/vfx/plasma_overlay.png"), false, false))
						.setTexturingState(new RenderStateShard.OffsetTexturingStateShard(u, v))
						.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
						.setCullState(NO_CULL)
						.setLightmapState(LIGHTMAP)
						.setOverlayState(OVERLAY)
						.createCompositeState(false)
		);
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
				
				float tick = Minecraft.getInstance().levelRenderer.getTicks() + partialTick;
				float speed = behavior.getSpeed();
				float u = (tick * speed) % 1f;
				float v = (tick * speed) % 1f;
				VertexConsumer vc = buffer.getBuffer(getPlasmaRenderType(u, v));
				
				behavior.getShape((box, ignoreFaces) ->
				{
					for(Direction direction : Direction.values())
					{
						if(!ignoreFaces.contains(direction))
						{
							renderPlasmaAddVertexesFace(
									matrices, light, overlay, vc,
									behavior.getTextureScale(), direction,
									(float) box.minX, (float) box.minY, (float) box.minZ,
									(float) box.maxX, (float) box.maxY, (float) box.maxZ
							);
						}
					}
				});
				
				matrices.popPose();
			}
		}
	}
	
	private static void renderPlasmaAddVertexesFace(PoseStack matrices, int light, int overlay,
													VertexConsumer vc,
													float textureScale, Direction direction,
													float x1, float y1, float z1,
													float x2, float y2, float z2)
	{
		float u1, v1, u2, v2;
		switch (direction)
		{
			case DOWN, UP ->
			{
				u1 = (x1 / 64f) / textureScale;
				v1 = (z1 / 32f) / textureScale;
				u2 = (x2 / 64f) / textureScale;
				v2 = (z2 / 32f) / textureScale;
			}
			case NORTH, SOUTH ->
			{
				u1 = (x1 / 64f) / textureScale;
				v1 = (y1 / 32f) / textureScale;
				u2 = (x2 / 64f) / textureScale;
				v2 = (y2 / 32f) / textureScale;
			}
			case WEST, EAST ->
			{
				u1 = (z1 / 64f) / textureScale;
				v1 = (y1 / 32f) / textureScale;
				u2 = (z2 / 64f) / textureScale;
				v2 = (y2 / 32f) / textureScale;
			}
			default -> throw new IllegalStateException("Unexpected value: " + direction);
		}
		switch (direction)
		{
			case DOWN ->
			{
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z1, u1, v1);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z1, u2, v1);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z2, u2, v2);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z2, u1, v2);
			}
			case UP ->
			{
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z1, u1, v1);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z1, u2, v1);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z2, u2, v2);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z2, u1, v2);
			}
			case NORTH ->
			{
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z1, u1, v1);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z1, u2, v1);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z1, u2, v2);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z1, u1, v2);
			}
			case SOUTH ->
			{
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z2, u1, v1);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z2, u2, v1);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z2, u2, v2);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z2, u1, v2);
			}
			case WEST ->
			{
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z1, u1, v1);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z2, u2, v1);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z2, u2, v2);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z1, u1, v2);
			}
			case EAST ->
			{
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z1, u1, v1);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z2, u2, v1);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z2, u2, v2);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z1, u1, v2);
			}
		}
	}
	
	private static void renderPlasmaAddVertex(PoseStack matrices, int light, int overlay,
											  VertexConsumer vc,
											  float x, float y, float z,
											  float u, float v)
	{
		PoseStack.Pose pose = matrices.last();
		vc.addVertex(pose, x, y, z)
				.setColor(1f, 1f, 1f, 0.8f)
				.setLight(light)
				.setNormal(pose, 0, 0, 0)
				.setOverlay(overlay)
				.setUv(u, v);
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
