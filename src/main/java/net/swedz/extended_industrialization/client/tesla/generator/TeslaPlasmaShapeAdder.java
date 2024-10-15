package net.swedz.extended_industrialization.client.tesla.generator;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

import java.util.Set;

public interface TeslaPlasmaShapeAdder
{
	void add(AABB box, Set<Direction> ignoreFaces);
}
