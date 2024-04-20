package net.swedz.miextended.machines.components.farmer.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.IPlantable;

public record FarmerTile(FarmerBlock dirt, FarmerBlock crop, int line, int quadrant)
{
	public boolean canBePlantedOnBy(Level level, IPlantable plantable)
	{
		return dirt.state(level).canSustainPlant(level, dirt.pos(), Direction.UP, plantable);
	}
}
