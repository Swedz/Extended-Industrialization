package net.swedz.extended_industrialization.client.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.swedz.extended_industrialization.EIClientShaders;

public final class TeslaPlasmaTextureStateShard extends RenderStateShard.TextureStateShard
{
	private final float scale, speed;
	
	public TeslaPlasmaTextureStateShard(ResourceLocation plasmaTexture, float scale, float speed)
	{
		super(plasmaTexture, false, false);
		this.scale = scale;
		this.speed = speed;
	}
	
	@Override
	public void setupRenderState()
	{
		super.setupRenderState();
		
		RenderSystem.setShaderTexture(3, InventoryMenu.BLOCK_ATLAS);
		
		EIClientShaders.teslaPlasma().getUniform("PlasmaScale").set(scale);
		EIClientShaders.teslaPlasma().getUniform("PlasmaSpeed").set(speed);
	}
}
