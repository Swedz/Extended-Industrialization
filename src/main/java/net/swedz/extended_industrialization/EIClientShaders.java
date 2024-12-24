package net.swedz.extended_industrialization;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
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
	private static ShaderInstance ARMOR_CUTOUT_GLOW_INSTANCE;
	private static ShaderInstance NANO_QUANTUM_INSTANCE;
	
	public static ShaderInstance armorCutoutGlow()
	{
		return ARMOR_CUTOUT_GLOW_INSTANCE;
	}
	
	public static ShaderInstance nanoQuantum()
	{
		return NANO_QUANTUM_INSTANCE;
	}
	
	public static final RenderStateShard.ShaderStateShard ARMOR_CUTOUT_GLOW = new RenderStateShard.ShaderStateShard(EIClientShaders::armorCutoutGlow);
	public static final RenderStateShard.ShaderStateShard NANO_QUANTUM      = new RenderStateShard.ShaderStateShard(EIClientShaders::nanoQuantum);
	
	public static final VertexFormat NANO_QUANTUM_VERTEX_FORMAT = VertexFormat.builder()
			.add("Position", VertexFormatElement.POSITION)
			.add("UV0", VertexFormatElement.UV0)
			.add("Color", VertexFormatElement.COLOR)
			.build();
	
	@SubscribeEvent
	private static void registerShaders(RegisterShadersEvent event)
	{
		try
		{
			event.registerShader(new ShaderInstance(event.getResourceProvider(), EI.id("armor_cutout_glow"), DefaultVertexFormat.NEW_ENTITY), (shader) -> ARMOR_CUTOUT_GLOW_INSTANCE = shader);
			event.registerShader(new ShaderInstance(event.getResourceProvider(), EI.id("nano_quantum"), NANO_QUANTUM_VERTEX_FORMAT), (shader) -> NANO_QUANTUM_INSTANCE = shader);
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
