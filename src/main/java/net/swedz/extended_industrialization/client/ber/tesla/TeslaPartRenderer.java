package net.swedz.extended_industrialization.client.ber.tesla;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.renderable.BakedModelRenderable;
import net.swedz.extended_industrialization.EIClient;
import net.swedz.extended_industrialization.EIClientRenderTypes;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.client.ber.tesla.arc.TeslaArcBuilder;
import net.swedz.extended_industrialization.client.ber.tesla.arc.TeslaArcPoint;
import net.swedz.extended_industrialization.client.ber.tesla.arc.TeslaArcRenderer;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaArcInstance;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaBehavior;
import net.swedz.extended_industrialization.client.model.tesla.TeslaBakedModel;
import net.swedz.extended_industrialization.machines.component.tesla.network.TeslaNetworkPart;
import net.swedz.tesseract.neoforge.api.WorldPos;
import net.swedz.tesseract.neoforge.helper.CubeOverlayRenderHelper;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public final class TeslaPartRenderer
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
				CubeOverlayRenderHelper.render(matrices, buffer, 111f / 256, 111f / 256, 1f, overlay);
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
	
	private static final Cache<BlockPos, TeslaArcInstance> TESLA_ARCS = CacheBuilder.newBuilder()
			.expireAfterAccess(1, TimeUnit.SECONDS)
			.build();
	
	public static TeslaArcInstance getArcInstance(BlockPos pos)
	{
		var level = Minecraft.getInstance().level;
		if(level.getBlockEntity(pos) instanceof MachineBlockEntity machine &&
		   machine instanceof TeslaBehavior behavior)
		{
			try
			{
				var tesla = getTeslaModel(behavior.getTeslaModelLocation());
				if(tesla.arcs() != null)
				{
					return TESLA_ARCS.get(
							pos,
							() -> new TeslaArcInstance(
									pos,
									() -> machine.orientation.facingDirection,
									() -> getTeslaModel(behavior.getTeslaModelLocation())
							)
					);
				}
			}
			catch (ExecutionException ex)
			{
				throw new RuntimeException(ex);
			}
		}
		return null;
	}
	
	private static TeslaBakedModel getTeslaModel(ResourceLocation location)
	{
		var modelManager = Minecraft.getInstance().getModelManager();
		if(modelManager.getModel(ModelResourceLocation.standalone(location)) instanceof TeslaBakedModel model)
		{
			return model;
		}
		throw new IllegalArgumentException("Model \"%s\" is not a tesla model".formatted(location));
	}
	
	private static void renderArcBounds(MachineBlockEntity machine, TeslaBakedModel tesla, PoseStack matrices, MultiBufferSource buffer)
	{
		var arcs = tesla.arcs();
		if(arcs != null && (arcs.hasRandomBounds() || arcs.attachesToNearbyEntities()) && Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes())
		{
			matrices.pushPose();
			
			VertexConsumer consumer = buffer.getBuffer(RenderType.lines());
			
			Vec3 position = machine.getBlockPos().getCenter();
			Direction direction = machine.orientation.facingDirection;
			
			if(arcs.hasRandomBounds())
			{
				var box = arcs.worldIncludeBounds(position, direction).move(position.scale(-1)).move(0.5, 0.5, 0.5);
				LevelRenderer.renderLineBox(matrices, consumer, box, 0.75f, 1, 0.75f, 1);
				
				box = arcs.worldExcludeBounds(position, direction).move(position.scale(-1)).move(0.5, 0.5, 0.5);
				LevelRenderer.renderLineBox(matrices, consumer, box, 1, 0.75f, 0.75f, 1);
			}
			if(arcs.attachesToNearbyEntities())
			{
				var box = arcs.worldNearbyEntitiesBounds(position, direction).move(position.scale(-1)).move(0.5, 0.5, 0.5);
				LevelRenderer.renderLineBox(matrices, consumer, box, 0.75f, 0.75f, 1, 1);
			}
			
			matrices.popPose();
		}
	}
	
	private static void renderArcs(MachineBlockEntity machine, TeslaBakedModel tesla, Vector3f color, float partialTick, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		var arcs = tesla.arcs();
		if(arcs != null)
		{
			var arcInstance = getArcInstance(machine.getBlockPos());
			if(arcInstance == null)
			{
				return;
			}
			for(TeslaArcBuilder trail : arcInstance.getTrails())
			{
				List<TeslaArcPoint> points = trail.points();
				if(points.size() < 2)
				{
					continue;
				}
				boolean instant = arcs.duration() == 0;
				float alpha = 0.9f;
				if(!instant)
				{
					int ticks = points.getFirst().timeActive();
					int halfPoints = points.size() / 2;
					if(ticks == 0 || ticks == 1)
					{
						points = points.subList(0, (int) (halfPoints * partialTick) + (ticks == 1 ? halfPoints : 0));
					}
					alpha *= (ticks == 0 ? partialTick : ticks == arcs.duration() ? (1 - partialTick) : 1);
				}
				
				matrices.pushPose();
				
				var consumer = buffer.getBuffer(EIClientRenderTypes.TESLA_ARC);
				TeslaArcRenderer.renderArc(
						matrices, consumer, points, (i) -> (1 - i) * arcs.widthScale(),
						color.x(), color.y(), color.z(), alpha
				);
				
				matrices.popPose();
			}
		}
	}
	
	private static void renderPlasma(MachineBlockEntity machine, TeslaBakedModel tesla, Vector3f color, float partialTick, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		var plasma = tesla.plasma();
		if(plasma != null)
		{
			matrices.pushPose();
			
			Vec3 worldPosition = machine.getBlockPos().getCenter();
			Vec3 worldOffset = plasma.worldOffset(worldPosition, machine.orientation.facingDirection);
			Vec3 offset = worldOffset.subtract(worldPosition).add(0.5, 0.5, 0.5);
			matrices.translate(offset.x(), offset.y(), offset.z());
			
			float modelScale = plasma.scale();
			matrices.scale(modelScale, modelScale, modelScale);
			
			float textureScale = plasma.textureScale();
			float speed = plasma.speed();
			BakedModelRenderable.of(tesla).render(
					matrices, buffer,
					(texture) -> EIClientRenderTypes.TESLA_PLASMA.apply(textureScale, speed),
					light, overlay, partialTick,
					new BakedModelRenderable.Context(
							null,
							new Direction[1],
							RandomSource.create(),
							1835364215L,
							ModelData.EMPTY,
							new Vector4f(color.x(), color.y(), color.z(), 0.8f)
					)
			);
			
			matrices.popPose();
		}
	}
	
	static void render(MachineBlockEntity machine, float partialTick, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		renderHighlight(machine, partialTick, matrices, buffer, light, overlay);
		if(EIClient.config().renderTeslaAnimations() && machine instanceof TeslaBehavior behavior)
		{
			int renderDistance = EIClient.config().teslaAnimationsRenderDistance();
			if(renderDistance == 0 || Minecraft.getInstance().player.position().closerThan(machine.getBlockPos().getCenter(), renderDistance))
			{
				var tesla = getTeslaModel(behavior.getTeslaModelLocation());
				var color = behavior.getTeslaColor();
				renderArcBounds(machine, tesla, matrices, buffer);
				if(behavior.shouldTeslaRender())
				{
					renderArcs(machine, tesla, color, partialTick, matrices, buffer, light, overlay);
					renderPlasma(machine, tesla, color, partialTick, matrices, buffer, light, overlay);
				}
			}
		}
	}
}
