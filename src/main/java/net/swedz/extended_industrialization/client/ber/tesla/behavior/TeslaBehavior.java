package net.swedz.extended_industrialization.client.ber.tesla.behavior;

import net.minecraft.resources.ResourceLocation;

public interface TeslaBehavior
{
	boolean shouldTeslaRender();
	
	ResourceLocation getTeslaModelLocation();
}
