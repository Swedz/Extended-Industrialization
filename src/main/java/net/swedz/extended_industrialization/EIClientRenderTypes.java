package net.swedz.extended_industrialization;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.client.shader.AtlasTextureStateShard;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.client.renderer.RenderStateShard.*;
import static net.swedz.extended_industrialization.EIClientShaders.*;

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
	
	public static final Function<ResourceLocation, RenderType> NANO_QUANTUM = Util.memoize(EIClientRenderTypes::createNanoQuantum);
	
	private static RenderStateShard.EmptyTextureStateShard nanoQuantumTexture(ResourceLocation maskTexture)
	{
		List<ResourceLocation> sprites = Lists.newArrayList();
		for(int i = 1; i <= 4; i++)
		{
			sprites.add(EI.id("shaders/nano_quantum/%d".formatted(i)));
		}
		return new AtlasTextureStateShard(maskTexture, EI.id("textures/atlas/nano_quantum.png"), sprites, false, false);
	}
	
	private static RenderType createNanoQuantum(ResourceLocation texture)
	{
		var state = RenderType.CompositeState.builder()
				.setShaderState(EIClientShaders.NANO_QUANTUM)
				.setTextureState(nanoQuantumTexture(texture))
				.setCullState(NO_CULL)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.createCompositeState(false);
		return RenderType.create("nano_quantum", EIClientShaders.NANO_QUANTUM_VERTEX_FORMAT, VertexFormat.Mode.QUADS, 1536, false, false, state);
	}
}
