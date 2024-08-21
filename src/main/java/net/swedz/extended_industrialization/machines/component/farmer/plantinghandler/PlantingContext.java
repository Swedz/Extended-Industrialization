package net.swedz.extended_industrialization.machines.component.farmer.plantinghandler;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerTile;

public record PlantingContext(Level level, FarmerTile tile, ItemStack stack)
{
}
