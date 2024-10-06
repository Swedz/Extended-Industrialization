package net.swedz.extended_industrialization.client.tesla.generator;

import net.minecraft.world.phys.Vec3;

public interface TeslaPlasmaGenerator
{
	Vec3 getTeslaPlasmaOffset();
	
	boolean shouldRenderTeslaPlasma();
}
