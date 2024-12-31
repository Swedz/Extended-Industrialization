package net.swedz.extended_industrialization.client.ber.tesla.behavior;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public interface TeslaPlasmaBehavior
{
	boolean shouldRender();
	
	Vec3 getOffset();
	
	ResourceLocation getModelLocation();
	
	float getModelScale();
	
	float getSpeed();
	
	float getTextureScale();
}
