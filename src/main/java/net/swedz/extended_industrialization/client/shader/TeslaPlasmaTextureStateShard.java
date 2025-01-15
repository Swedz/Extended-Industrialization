package net.swedz.extended_industrialization.client.shader;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EIClientShaders;

public final class TeslaPlasmaTextureStateShard extends RenderStateShard.TextureStateShard
{
	private final float scale, speed;
	
	public TeslaPlasmaTextureStateShard(ResourceLocation texture, float scale, float speed)
	{
		super(texture, false, false);
		this.scale = scale;
		this.speed = speed;
	}
	
	@Override
	public void setupRenderState()
	{
		super.setupRenderState();
		
		EIClientShaders.teslaPlasma().getUniform("PlasmaScale").set(scale);
		EIClientShaders.teslaPlasma().getUniform("PlasmaSpeed").set(speed);
	}
}
