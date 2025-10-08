package net.swedz.extended_industrialization;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(modid = EI.ID, value = Dist.CLIENT)
public final class EIClientShaders
{
	private static ShaderInstance ARMOR_CUTOUT_GLOW_INSTANCE;
	
	public static ShaderInstance armorCutoutGlow()
	{
		return ARMOR_CUTOUT_GLOW_INSTANCE;
	}
	
	private static ShaderInstance NANO_QUANTUM_INSTANCE;
	
	public static ShaderInstance nanoQuantum()
	{
		return NANO_QUANTUM_INSTANCE;
	}
	
	private static ShaderInstance TESLA_PLASMA_INSTANCE;
	
	public static ShaderInstance teslaPlasma()
	{
		return TESLA_PLASMA_INSTANCE;
	}
	
	private static ShaderInstance TESLA_ARC_INSTANCE;
	
	public static ShaderInstance teslaArc()
	{
		return TESLA_ARC_INSTANCE;
	}
	
	public static final RenderStateShard.ShaderStateShard ARMOR_CUTOUT_GLOW = new RenderStateShard.ShaderStateShard(EIClientShaders::armorCutoutGlow);
	public static final RenderStateShard.ShaderStateShard NANO_QUANTUM      = new RenderStateShard.ShaderStateShard(EIClientShaders::nanoQuantum);
	public static final RenderStateShard.ShaderStateShard TESLA_ARC         = new RenderStateShard.ShaderStateShard(EIClientShaders::teslaArc);
	public static final RenderStateShard.ShaderStateShard TESLA_PLASMA      = new RenderStateShard.ShaderStateShard(EIClientShaders::teslaPlasma);
	
	@SubscribeEvent
	private static void registerShaders(RegisterShadersEvent event)
	{
		try
		{
			event.registerShader(new ShaderInstance(event.getResourceProvider(), EI.id("armor_cutout_glow"), DefaultVertexFormat.NEW_ENTITY), (shader) -> ARMOR_CUTOUT_GLOW_INSTANCE = shader);
			event.registerShader(new ShaderInstance(event.getResourceProvider(), EI.id("nano_quantum"), DefaultVertexFormat.NEW_ENTITY), (shader) -> NANO_QUANTUM_INSTANCE = shader);
			event.registerShader(new ShaderInstance(event.getResourceProvider(), EI.id("tesla_plasma"), DefaultVertexFormat.POSITION_TEX), (shader) -> TESLA_PLASMA_INSTANCE = shader);
			event.registerShader(new ShaderInstance(event.getResourceProvider(), EI.id("tesla_arc"), DefaultVertexFormat.POSITION_TEX), (shader) -> TESLA_ARC_INSTANCE = shader);
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
