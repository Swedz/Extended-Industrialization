package net.swedz.extended_industrialization.client.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EIClientShaders;

import java.util.List;

public class AtlasTextureStateShard extends RenderStateShard.TextureStateShard
{
	private final ResourceLocation       atlas;
	private final List<ResourceLocation> sprites;
	
	public AtlasTextureStateShard(ResourceLocation texture, ResourceLocation atlas, List<ResourceLocation> sprites, boolean blur, boolean mipmap)
	{
		super(texture, blur, mipmap);
		this.atlas = atlas;
		this.sprites = sprites;
	}
	
	@Override
	public void setupRenderState()
	{
		super.setupRenderState();
		
		RenderSystem.setShaderTexture(1, atlas);
		
		var atlas = Minecraft.getInstance().getTextureAtlas(this.atlas);
		int index = 1;
		for(ResourceLocation spriteLocation : sprites)
		{
			var sprite = atlas.apply(spriteLocation);
			var uniform = EIClientShaders.nanoQuantum().getUniform("QuantumStarUV" + index);
			uniform.setSafe(sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1());
			index++;
		}
	}
}
