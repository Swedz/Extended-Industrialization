package net.swedz.extended_industrialization.client.tesla;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.util.RenderHelper;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.api.WorldPos;
import net.swedz.extended_industrialization.client.tesla.arcs.TeslaArcGenerator;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkPart;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypes;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderType;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;
import team.lodestar.lodestone.systems.rendering.trail.TrailPoint;
import team.lodestar.lodestone.systems.rendering.trail.TrailPointBuilder;

import java.util.Collections;
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
		if(machine instanceof TeslaArcGenerator generator && generator.shouldRenderTeslaArcs())
		{
			VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld();
			builder.replaceBufferSource(RenderHandler.LATE_DELAYED_RENDER.getTarget())
					.setRenderType(TESLA_ARC)
					.setColorRaw(1f, 1f, 1f)
					.setAlpha(0.9f);
			
			for(TrailPointBuilder trail : generator.getTeslaArcs().getTrails())
			{
				List<TrailPoint> points = trail.getTrailPoints();
				if(points.size() < 2)
				{
					continue;
				}
				int ticks = points.getFirst().getTimeActive();
				builder.setAlpha(0.9f * (ticks == 0 ? partialTick : ticks == generator.getTeslaArcs().duration() ? (1 - partialTick) : 1));
				int halfPoints = points.size() / 2;
				if(ticks == 0 || ticks == 1)
				{
					points = points.subList(0, (int) (halfPoints * partialTick) + (ticks == 1 ? halfPoints : 0));
				}
				
				matrices.pushPose();
				
				builder.renderTrail(matrices, points, (i) -> 1 - i);
				
				matrices.popPose();
			}
		}
	}
	
	private record IgnoreSideShape(Direction ignoreSide, VoxelShape shape)
	{
	}
	
	private static final List<IgnoreSideShape> TESLA_TOP_LOAD_SHAPES;
	
	static
	{
		List<IgnoreSideShape> shapes = Lists.newArrayList();
		double inflate = 0.05;
		shapes.add(new IgnoreSideShape(Direction.UP, Shapes.create(new AABB(-1, -2 - inflate, -1, 1 + 1, -2 + 1 - inflate, 1 + 1).inflate(inflate, 0, inflate))));
		shapes.add(new IgnoreSideShape(Direction.DOWN, Shapes.create(new AABB(-1, 2 + inflate, -1, 1 + 1, 2 + 1 + inflate, 1 + 1).inflate(inflate, 0, inflate))));
		shapes.add(new IgnoreSideShape(Direction.WEST, Shapes.create(new AABB(2 + inflate, -1, -1, 2 + 1 + inflate, 1 + 1, 1 + 1).inflate(0, inflate, inflate))));
		shapes.add(new IgnoreSideShape(Direction.EAST, Shapes.create(new AABB(-2 - inflate, -1, -1, -2 + 1 - inflate, 1 + 1, 1 + 1).inflate(0, inflate, inflate))));
		shapes.add(new IgnoreSideShape(Direction.NORTH, Shapes.create(new AABB(-1, -1, 2 + inflate, 1 + 1, 1 + 1, 2 + 1 + inflate).inflate(inflate, inflate, 0))));
		shapes.add(new IgnoreSideShape(Direction.SOUTH, Shapes.create(new AABB(-1, -1, -2 - inflate, 1 + 1, 1 + 1, -2 + 1 - inflate).inflate(inflate, inflate, 0))));
		TESLA_TOP_LOAD_SHAPES = Collections.unmodifiableList(shapes);
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
		matrices.pushPose();
		
		float tick = Minecraft.getInstance().levelRenderer.getTicks() + partialTick;
		float speed = 0.001f;//0.015f;
		float u = (tick * speed) % 1f;
		float v = (tick * speed) % 1f;
		VertexConsumer vc = buffer.getBuffer(getPlasmaRenderType(u, v));
		
		for(IgnoreSideShape shape : TESLA_TOP_LOAD_SHAPES)
		{
			for(AABB box : shape.shape().toAabbs())
			{
				for(Direction direction : Direction.values())
				{
					if(direction != shape.ignoreSide())
					{
						renderPlasmaAddVertexesFace(
								matrices, light, overlay, vc, direction,
								(float) box.minX, (float) box.minY, (float) box.minZ,
								(float) box.maxX, (float) box.maxY, (float) box.maxZ,
								0, 0
						);
					}
				}
			}
			
			/*LevelRenderer.renderVoxelShape(
					matrices,
					buffer.getBuffer(RenderType.lines()),
					shape.shape(),
					0, 0, 0,
					1f, 1f, 1f, 1f, true
			);*/
		}
		
		matrices.popPose();
	}
	
	private static void renderPlasmaAddVertexesFace(PoseStack matrices, int light, int overlay,
													VertexConsumer vc,
													Direction direction,
													float x1, float y1, float z1,
													float x2, float y2, float z2,
													float u, float v)
	{
		float dx = Math.abs(x2 - x1);
		float dy = Math.abs(y2 - y1);
		float dz = Math.abs(z2 - z1);
		float width, height;
		switch (direction)
		{
			case DOWN ->
			{
				width = dx;
				height = dz;
			}
			case UP ->
			{
				width = dz;
				height = dx;
			}
			case NORTH, SOUTH ->
			{
				width = dx;
				height = dy;
			}
			case WEST, EAST ->
			{
				width = dz;
				height = dy;
			}
			default -> throw new IllegalStateException("Unexpected value: " + direction);
		}
		
		float scale = 8f / 64f;
		float offsetU = width / 64f / scale;
		float offsetV = height / 32f / scale;
		
		switch (direction)
		{
			case DOWN ->
			{
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z1, u, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z1, u + offsetU, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z2, u + offsetU, v + offsetV);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z2, u, v + offsetV);
			}
			case UP ->
			{
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z1, u, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z2, u + offsetU, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z2, u + offsetU, v + offsetV);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z1, u, v + offsetV);
			}
			case NORTH ->
			{
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z1, u, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z1, u + offsetU, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z1, u + offsetU, v + offsetV);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z1, u, v + offsetV);
			}
			case SOUTH ->
			{
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z2, u, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z2, u + offsetU, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z2, u + offsetU, v + offsetV);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z2, u, v + offsetV);
			}
			case WEST ->
			{
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z1, u, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z2, u + offsetU, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z2, u + offsetU, v + offsetV);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z1, u, v + offsetV);
			}
			case EAST ->
			{
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z1, u, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z1, u + offsetU, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z2, u + offsetU, v + offsetV);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z2, u, v + offsetV);
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
				.setColor(1f, 1f, 1f, 0.5f)
				.setLight(light)
				.setNormal(pose, 0, 0, 0)
				.setOverlay(overlay)
				.setUv(u, v);
	}
	
	static void render(MachineBlockEntity machine, float partialTick, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		renderHighlight(machine, partialTick, matrices, buffer, light, overlay);
		renderArcs(machine, partialTick, matrices, buffer, light, overlay);
		renderPlasma(machine, partialTick, matrices, buffer, light, overlay);
	}
}
