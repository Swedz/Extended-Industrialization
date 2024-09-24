package net.swedz.extended_industrialization.client.model;

import aztech.modern_industrialization.machines.models.MachineCasing;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.Map;

public interface ModularMachineModelBaker
{
	ModularMachineBakedModel bake(MachineCasing baseCasing,
								  int[] outputOverlayIndexes, TextureAtlasSprite[] defaultOverlays,
								  Map<String, TextureAtlasSprite[]> tieredOverlays);
}
