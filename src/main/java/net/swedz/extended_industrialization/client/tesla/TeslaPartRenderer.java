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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
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
	
	private static final VoxelShape TESLA_TOP_LOAD_SHAPE;
	
	static
	{
		VoxelShape shape = Shapes.empty();
		
		for(int y = -2; y <= 2; y++)
		{
			for(int x = -1; x <= 1; x++)
			{
				for(int z = -1; z <= 1; z++)
				{
					shape = Shapes.joinUnoptimized(shape, Shapes.create(AABB.unitCubeFromLowerCorner(new Vec3(x, y, z)).inflate(0.05)), BooleanOp.OR);
				}
			}
		}
		
		for(int y = -1; y <= 1; y++)
		{
			for(int x = -2; x <= 2; x++)
			{
				for(int z = -1; z <= 1; z++)
				{
					shape = Shapes.joinUnoptimized(shape, Shapes.create(AABB.unitCubeFromLowerCorner(new Vec3(x, y, z)).inflate(0.05)), BooleanOp.OR);
					if(x != z)
					{
						shape = Shapes.joinUnoptimized(shape, Shapes.create(AABB.unitCubeFromLowerCorner(new Vec3(z, y, x)).inflate(0.05)), BooleanOp.OR);
					}
				}
			}
		}
		
		TESLA_TOP_LOAD_SHAPE = shape;
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
		float speed = 0.015f;
		float u = Mth.cos(tick * speed) * 0.5f % 1f;
		float v = tick * speed * 0.75f % 1f;
		VertexConsumer vc = buffer.getBuffer(getPlasmaRenderType(u, v));
		
		for(AABB box : TESLA_TOP_LOAD_SHAPE.toAabbs())
		{
			for(Direction direction : Direction.values())
			{
				renderPlasmaAddVertexesFace(
						matrices, light, overlay, vc, direction,
						(float) box.minX, (float) box.minY, (float) box.minZ,
						(float) box.maxX, (float) box.maxY, (float) box.maxZ,
						0, 0
				);
			}
		}
		
		/*LevelRenderer.renderVoxelShape(
				matrices,
				buffer.getBuffer(RenderType.lineStrip()),
				TESLA_TOP_LOAD_SHAPE,
				0, 0, 0,
				0f, 0f, 1f, 1f, true
		);*/
		
		matrices.popPose();
	}
	
	private static void renderPlasmaAddVertexesFace(PoseStack matrices, int light, int overlay,
													VertexConsumer vc,
													Direction direction,
													float x1, float y1, float z1,
													float x2, float y2, float z2,
													float u, float v)
	{
		// TODO calculate UV offsets based on the side - currently it scales wrong on taller/wider faces
		float offsetV = 0.5f * 3f;
		float offsetU = (offsetV / 2f);// * 3f;
		switch (direction)
		{
			case DOWN:
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z1, u, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z1, u + offsetU, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z2, u + offsetU, v + offsetV);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z2, u, v + offsetV);
				break;
			case UP:
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z1, u, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z2, u + offsetU, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z2, u + offsetU, v + offsetV);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z1, u, v + offsetV);
				break;
			case NORTH:
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z1, u, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z1, u + offsetU, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z1, u + offsetU, v + offsetV);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z1, u, v + offsetV);
				break;
			case SOUTH:
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z2, u, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z2, u + offsetU, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z2, u + offsetU, v + offsetV);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z2, u, v + offsetV);
				break;
			case EAST:
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z1, u, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z1, u + offsetU, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y2, z2, u + offsetU, v + offsetV);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x2, y1, z2, u, v + offsetV);
				break;
			case WEST:
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z1, u, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y1, z2, u + offsetU, v);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z2, u + offsetU, v + offsetV);
				renderPlasmaAddVertex(matrices, light, overlay, vc, x1, y2, z1, u, v + offsetV);
				break;
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
