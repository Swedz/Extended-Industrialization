package net.swedz.extended_industrialization.client.tesla;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
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
	
	private static final LodestoneRenderType RENDER_LAYER = LodestoneRenderTypes.TRANSPARENT_TEXTURE.applyAndCache(RenderTypeToken.createToken(EI.id("textures/vfx/tesla_arc.png")));
	
	private static void renderArcs(MachineBlockEntity machine, float partialTick, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		if(machine instanceof TeslaArcGenerator generator && generator.shouldRenderTeslaArcs())
		{
			VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld();
			builder.replaceBufferSource(RenderHandler.LATE_DELAYED_RENDER.getTarget())
					.setRenderType(RENDER_LAYER)
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
				
				matrices.pushPose();
				
				builder.renderTrail(matrices, trail.getTrailPoints(), (i) -> 1 - i);
				
				matrices.popPose();
			}
		}
	}
	
	static void render(MachineBlockEntity machine, float partialTick, PoseStack matrices, MultiBufferSource buffer, int light, int overlay)
	{
		renderHighlight(machine, partialTick, matrices, buffer, light, overlay);
		renderArcs(machine, partialTick, matrices, buffer, light, overlay);
	}
}
