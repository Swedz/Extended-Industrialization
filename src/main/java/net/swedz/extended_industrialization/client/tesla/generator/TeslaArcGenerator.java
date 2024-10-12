package net.swedz.extended_industrialization.client.tesla.generator;

import net.minecraft.world.phys.Vec3;

public interface TeslaArcGenerator
{
	TeslaArcs getTeslaArcs();
	
	Vec3 getTeslaArcsOffset();
	
	boolean shouldRenderTeslaArcs();
}
