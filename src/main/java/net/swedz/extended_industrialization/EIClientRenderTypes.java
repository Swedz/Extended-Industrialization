package net.swedz.extended_industrialization;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.swedz.extended_industrialization.client.shader.NanoQuantumTextureStateShard;
import net.swedz.extended_industrialization.client.shader.TeslaPlasmaTextureStateShard;

import java.util.List;
import java.util.function.BiFunction;

import static net.minecraft.client.renderer.RenderStateShard.*;
import static net.swedz.extended_industrialization.EIClientShaders.*;

@EventBusSubscriber(modid = EI.ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class EIClientRenderTypes
{
	public static final BiFunction<ResourceLocation, Boolean, RenderType> ARMOR_CUTOUT_NO_CULL_WITH_TRANSPARENCY = Util.memoize((texture, glow) -> armorCutoutWithTransparency(texture, glow, false));
	public static final BiFunction<ResourceLocation, Boolean, RenderType> ARMOR_CUTOUT_CULL_WITH_TRANSPARENCY    = Util.memoize((texture, glow) -> armorCutoutWithTransparency(texture, glow, true));
	
	private static RenderType armorCutoutWithTransparency(ResourceLocation texture, boolean glow, boolean cull)
	{
		var state = RenderType.CompositeState.builder()
				.setShaderState(glow ? ARMOR_CUTOUT_GLOW : RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(cull ? CULL : NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(NO_OVERLAY)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.setDepthTestState(LEQUAL_DEPTH_TEST)
				.createCompositeState(false);
		return RenderType.create("armor_cutout_%s_with_transparency".formatted(cull ? "cull" : "no_cull"), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, false, state);
	}
	
	public static final BiFunction<ResourceLocation, Boolean, RenderType> NANO_QUANTUM_STARS    = Util.memoize((mask, cull) -> createNanoQuantum(mask, true, cull));
	public static final BiFunction<ResourceLocation, Boolean, RenderType> NANO_QUANTUM_NO_STARS = Util.memoize((mask, cull) -> createNanoQuantum(mask, false, cull));
	
	private static RenderStateShard.EmptyTextureStateShard nanoQuantumTexture(ResourceLocation mask, boolean stars)
	{
		List<ResourceLocation> sprites = Lists.newArrayList();
		for(int i = 1; i <= 4; i++)
		{
			sprites.add(EI.id("shaders/nano_quantum/%d".formatted(i)));
		}
		return new NanoQuantumTextureStateShard(mask, EI.id("textures/shaders/nano_quantum/glint.png"), stars, EI.id("textures/atlas/nano_quantum.png"), sprites, false, false);
	}
	
	private static RenderType createNanoQuantum(ResourceLocation mask, boolean stars, boolean cull)
	{
		var state = RenderType.CompositeState.builder()
				.setShaderState(EIClientShaders.NANO_QUANTUM)
				.setTextureState(nanoQuantumTexture(mask, stars))
				.setCullState(cull ? CULL : NO_CULL)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.createCompositeState(false);
		return RenderType.create("nano_quantum_%s".formatted(stars ? "stars" : "no_stars"), EIClientShaders.NANO_QUANTUM_VERTEX_FORMAT, VertexFormat.Mode.QUADS, 1536, false, false, state);
	}
	
	public static final RenderType TESLA_ARC = RenderType.create(
			"tesla_arc",
			DefaultVertexFormat.POSITION_TEX_COLOR,
			VertexFormat.Mode.QUADS,
			256,
			false,
			true,
			RenderType.CompositeState.builder()
					.setShaderState(EIClientShaders.TESLA_ARC)
					.setTextureState(new RenderStateShard.TextureStateShard(EI.id("textures/vfx/tesla_arc.png"), false, false))
					.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
					.setCullState(CULL)
					.setLightmapState(LIGHTMAP)
					.setOverlayState(OVERLAY)
					.createCompositeState(false)
	);
	
	public static final BiFunction<Float, Float, RenderType> TESLA_PLASMA = Util.memoize(EIClientRenderTypes::createTeslaPlasma);
	
	private static RenderType createTeslaPlasma(float scale, float speed)
	{
		return RenderType.create(
				"plasma",
				DefaultVertexFormat.POSITION_TEX_COLOR,
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
	
	@SubscribeEvent
	private static void onRegisterRenderBuffers(RegisterRenderBuffersEvent event)
	{
		event.registerRenderBuffer(TESLA_ARC);
	}
}
