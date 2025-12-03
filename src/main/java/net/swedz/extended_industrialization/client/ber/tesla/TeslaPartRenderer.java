package net.swedz.extended_industrialization.client.ber.tesla;

import aztech.modern_industrialization.MITags;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.renderable.BakedModelRenderable;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIClient;
import net.swedz.extended_industrialization.EIClientRenderTypes;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.client.ber.tesla.arc.TeslaArcPoint;
import net.swedz.extended_industrialization.client.ber.tesla.arc.TeslaArcRenderer;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaArcInstance;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaBehavior;
import net.swedz.extended_industrialization.client.model.tesla.TeslaBakedModel;
import net.swedz.extended_industrialization.compat.continuity.ContinuityModelUnwrapper;
import net.swedz.extended_industrialization.machines.blockentity.tesla.TeslaParticleGeneratorMachineBlockEntity;
import net.swedz.extended_industrialization.machines.component.tesla.network.TeslaNetworkPart;
import net.swedz.tesseract.neoforge.api.WorldPos;
import net.swedz.tesseract.neoforge.helper.CubeOverlayRenderHelper;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(modid = EI.ID, value = Dist.CLIENT)
public final class TeslaPartRenderer
{
	private static void renderHighlight(MachineBlockEntity machine, float partialTick, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		var pos = machine.getBlockPos();
		
		if(machine instanceof TeslaNetworkPart part)
		{
			getHeldNetworkKey().ifPresent((networkKey) ->
			{
				if(part.hasNetwork() && part.getNetworkKey().equals(networkKey))
				{
					matrices.pushPose();
					matrices.translate(-0.005, -0.005, -0.005);
					matrices.scale(1.01f, 1.01f, 1.01f);
					CubeOverlayRenderHelper.render(matrices, buffer, 111f / 256, 111f / 256, 1f, overlay);
					matrices.popPose();
				}
			});
		}
		else if(machine instanceof TeslaParticleGeneratorMachineBlockEntity)
		{
			if(isHoldingWrench())
			{
				matrices.pushPose();
				matrices.translate(-0.005, -0.005, -0.005);
				matrices.scale(1.01f, 1.01f, 1.01f);
				CubeOverlayRenderHelper.render(matrices, buffer, 1f, 1f, 1f, overlay);
				matrices.popPose();
			}
		}
	}
	
	private static Optional<WorldPos> getHeldNetworkKey()
	{
		var player = Minecraft.getInstance().player;
		return player.getMainHandItem().has(EIComponents.SELECTED_TESLA_NETWORK) ? Optional.of(player.getMainHandItem().get(EIComponents.SELECTED_TESLA_NETWORK).key()) :
				player.getOffhandItem().has(EIComponents.SELECTED_TESLA_NETWORK) ? Optional.of(player.getOffhandItem().get(EIComponents.SELECTED_TESLA_NETWORK).key()) : Optional.empty();
	}
	
	private static boolean isHoldingWrench()
	{
		var player = Minecraft.getInstance().player;
		return player.getMainHandItem().is(MITags.WRENCHES) ||
			   player.getOffhandItem().is(MITags.WRENCHES);
	}
	
	private static final Map<BlockPos, TeslaArcInstance> TESLA_ARCS = Maps.newConcurrentMap();
	
	@SubscribeEvent
	private static void onLevelUnload(LevelEvent.Unload event)
	{
		TESLA_ARCS.clear();
	}
	
	public static void removeArcInstance(BlockPos pos)
	{
		TESLA_ARCS.remove(pos);
	}
	
	public static TeslaArcInstance getArcInstance(BlockPos pos)
	{
		var level = Minecraft.getInstance().level;
		if(level.getBlockEntity(pos) instanceof MachineBlockEntity machine &&
		   machine instanceof TeslaBehavior behavior)
		{
			var tesla = getTeslaModel(behavior.getTeslaModelLocation());
			if(tesla != null && tesla.arcs() != null)
			{
				return TESLA_ARCS.computeIfAbsent(
						pos,
						(__) -> new TeslaArcInstance(
								pos,
								() -> machine.orientation.facingDirection,
								() -> getTeslaModel(behavior.getTeslaModelLocation())
						)
				);
			}
		}
		return null;
	}
	
	private static TeslaBakedModel getTeslaModel(ResourceLocation location)
	{
		var modelManager = Minecraft.getInstance().getModelManager();
		var model = modelManager.getModel(ModelResourceLocation.standalone(location));
		model = ContinuityModelUnwrapper.unwrap(model);
		if(model instanceof TeslaBakedModel teslaModel)
		{
			return teslaModel;
		}
		EI.LOGGER.warn("Model {} should have been a TeslaBakedModel, but was {}", location, model.getClass());
		return null;
	}
	
	private static void renderArcBounds(MachineBlockEntity machine, TeslaBakedModel tesla, PoseStack matrices, MultiBufferSource buffer)
	{
		var arcs = tesla.arcs();
		if(arcs != null && (arcs.hasRandomBounds() || arcs.attachesToNearbyEntities()) && Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes())
		{
			matrices.pushPose();
			
			var consumer = buffer.getBuffer(RenderType.lines());
			
			var position = machine.getBlockPos().getCenter();
			var direction = machine.orientation.facingDirection;
			
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
			for(var trail : arcInstance.getTrails())
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
			
			var worldPosition = machine.getBlockPos().getCenter();
			var worldOffset = plasma.worldOffset(worldPosition, machine.orientation.facingDirection);
			var offset = worldOffset.subtract(worldPosition).add(0.5, 0.5, 0.5);
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
				if(tesla != null)
				{
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
}
