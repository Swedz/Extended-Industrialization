package net.swedz.extended_industrialization;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.swedz.extended_industrialization.client.shader.TeslaPlasmaTextureStateShard;

import java.util.function.BiFunction;

import static net.minecraft.client.renderer.RenderStateShard.*;

public final class EIClientRenderTypes
{
	public static final BiFunction<Float, Float, RenderType> TESLA_PLASMA = Util.memoize(EIClientRenderTypes::createTeslaPlasma);
	
	private static RenderType createTeslaPlasma(float scale, float speed)
	{
		return RenderType.create(
				"plasma",
				DefaultVertexFormat.POSITION_TEX,
				VertexFormat.Mode.QUADS,
				1536,
				false,
				true,
				RenderType.CompositeState.builder()
						.setShaderState(EIClientShaders.TESLA_PLASMA)
						.setTextureState(new TeslaPlasmaTextureStateShard(EI.id("textures/vfx/tesla_plasma.png"), scale, speed))
						.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
						.setCullState(CULL)
						.setLightmapState(LIGHTMAP)
						.setOverlayState(OVERLAY)
						.createCompositeState(false)
		);
	}
}
