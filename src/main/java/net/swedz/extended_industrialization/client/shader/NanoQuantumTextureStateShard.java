package net.swedz.extended_industrialization.client.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EIClientShaders;

import java.util.List;

public final class NanoQuantumTextureStateShard extends RenderStateShard.TextureStateShard
{
	private final ResourceLocation glint;
	
	private final boolean renderStars;
	private final float   starScaleX, starScaleY;
	
	private final ResourceLocation       starAtlas;
	private final List<ResourceLocation> sprites;
	
	public NanoQuantumTextureStateShard(ResourceLocation mask, ResourceLocation glint, boolean renderStars, float starScaleX, float starScaleY, ResourceLocation starAtlas, List<ResourceLocation> sprites, boolean blur, boolean mipmap)
	{
		super(mask, blur, mipmap);
		this.glint = glint;
		this.renderStars = renderStars;
		this.starScaleX = starScaleX;
		this.starScaleY = starScaleY;
		this.starAtlas = starAtlas;
		this.sprites = sprites;
	}
	
	@Override
	public void setupRenderState()
	{
		super.setupRenderState();
		
		EIClientShaders.nanoQuantum().getUniform("RenderStars").set(renderStars ? 1 : 0);
		
		EIClientShaders.nanoQuantum().getUniform("StarScale").set(new float[]{starScaleX, starScaleY});
		
		RenderSystem.setShaderTexture(1, glint);
		
		RenderSystem.setShaderTexture(2, starAtlas);
		
		var atlas = Minecraft.getInstance().getTextureAtlas(starAtlas);
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
