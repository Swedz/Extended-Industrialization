package net.swedz.extended_industrialization.client.tesla.generator;

import net.minecraft.world.phys.Vec3;

public interface TeslaPlasmaBehavior
{
	boolean shouldRender();
	
	Vec3 getOffset();
	
	void getShape(TeslaPlasmaShapeAdder shapes);
	
	float getSpeed();
	
	float getTextureScale();
}
