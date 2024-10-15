package net.swedz.extended_industrialization.client.tesla.generator;

import net.minecraft.world.phys.Vec3;

public interface TeslaPlasmaGenerator
{
	boolean shouldRenderTeslaPlasma();
	
	Vec3 getTeslaPlasmaOffset();
	
	void getTeslaPlasmaShape(TeslaPlasmaShapeAdder shapes);
}
