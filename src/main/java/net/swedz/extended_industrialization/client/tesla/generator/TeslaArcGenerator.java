package net.swedz.extended_industrialization.client.tesla.generator;

import net.minecraft.world.phys.Vec3;

public interface TeslaArcGenerator
{
	Vec3 getTeslaArcsOffset();
	
	TeslaArcs getTeslaArcs();
	
	boolean shouldRenderTeslaArcs();
}
