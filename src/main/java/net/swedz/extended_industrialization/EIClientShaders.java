package net.swedz.extended_industrialization;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(value = Dist.CLIENT, modid = EI.ID, bus = EventBusSubscriber.Bus.MOD)
public final class EIClientShaders
{
	private static ShaderInstance TESLA_PLASMA_INSTANCE;
	
	public static ShaderInstance teslaPlasma()
	{
		return TESLA_PLASMA_INSTANCE;
	}
	
	public static final RenderStateShard.ShaderStateShard TESLA_PLASMA = new RenderStateShard.ShaderStateShard(EIClientShaders::teslaPlasma);
	
	@SubscribeEvent
	private static void registerShaders(RegisterShadersEvent event)
	{
		try
		{
			event.registerShader(new ShaderInstance(event.getResourceProvider(), EI.id("tesla_plasma"), DefaultVertexFormat.POSITION_TEX), (shader) -> TESLA_PLASMA_INSTANCE = shader);
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
