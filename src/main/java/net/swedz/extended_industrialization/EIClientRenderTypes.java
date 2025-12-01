package net.swedz.extended_industrialization;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterNamedRenderTypesEvent;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.swedz.extended_industrialization.client.shader.NanoQuantumTextureStateShard;
import net.swedz.extended_industrialization.client.shader.TeslaPlasmaTextureStateShard;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.client.renderer.RenderStateShard.*;
import static net.swedz.extended_industrialization.EIClientShaders.*;

@EventBusSubscriber(modid = EI.ID, value = Dist.CLIENT)
public final class EIClientRenderTypes
{
	public static final BiFunction<ResourceLocation, Boolean, RenderType> ARMOR_CUTOUT_NO_CULL_WITH_TRANSPARENCY = Util.memoize((texture, glow) -> armorCutoutWithTransparency(texture, glow, false));
	public static final BiFunction<ResourceLocation, Boolean, RenderType> ARMOR_CUTOUT_CULL_WITH_TRANSPARENCY    = Util.memoize((texture, glow) -> armorCutoutWithTransparency(texture, glow, true));
	
	private static RenderType armorCutoutWithTransparency(ResourceLocation texture, boolean glow, boolean cull)
	{
		return RenderType.create(
				"armor_cutout_%s_with_transparency".formatted(cull ? "cull" : "no_cull"),
				DefaultVertexFormat.NEW_ENTITY,
				VertexFormat.Mode.QUADS,
				1536,
				false,
				false,
				RenderType.CompositeState.builder()
						.setShaderState(glow ? ARMOR_CUTOUT_GLOW : RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER)
						.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
						.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
						.setCullState(cull ? CULL : NO_CULL)
						.setLightmapState(LIGHTMAP)
						.setOverlayState(NO_OVERLAY)
						.setLayeringState(VIEW_OFFSET_Z_LAYERING)
						.setDepthTestState(LEQUAL_DEPTH_TEST)
						.createCompositeState(false)
		);
	}
	
	public record NanoQuantumState(
			ResourceLocation mask, boolean stars, float starScaleX, float starScaleY, boolean cull
	)
	{
	}
	
	public static final Function<NanoQuantumState, RenderType> NANO_QUANTUM = Util.memoize(EIClientRenderTypes::createNanoQuantum);
	
	private static RenderStateShard.EmptyTextureStateShard nanoQuantumTexture(NanoQuantumState state)
	{
		List<ResourceLocation> sprites = Lists.newArrayList();
		for(int i = 1; i <= 4; i++)
		{
			sprites.add(EI.id("shaders/nano_quantum/%s".formatted(i)));
		}
		return new NanoQuantumTextureStateShard(state.mask(), EI.id("textures/shaders/nano_quantum/glint.png"), state.stars(), state.starScaleX(), state.starScaleY(), EI.id("textures/atlas/nano_quantum.png"), sprites, false, false);
	}
	
	private static RenderType createNanoQuantum(NanoQuantumState state)
	{
		return RenderType.create(
				"nano_quantum_%s".formatted(state.stars() ? "stars" : "no_stars"),
				DefaultVertexFormat.NEW_ENTITY,
				VertexFormat.Mode.QUADS,
				1536,
				true,
				false,
				RenderType.CompositeState.builder()
						.setShaderState(EIClientShaders.NANO_QUANTUM)
						.setTextureState(nanoQuantumTexture(state))
						.setCullState(state.cull() ? CULL : NO_CULL)
						.setLayeringState(VIEW_OFFSET_Z_LAYERING)
						.setTransparencyState(NO_TRANSPARENCY)
						.createCompositeState(true)
		);
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
	
	@SubscribeEvent
	private static void registerNamedRenderTypes(RegisterNamedRenderTypesEvent event)
	{
		event.register(EI.id("nano_quantum_item_stars_cull"), RenderType.cutout(), NANO_QUANTUM.apply(new NanoQuantumState(InventoryMenu.BLOCK_ATLAS, true, 24, 24, true)));
	}
}
