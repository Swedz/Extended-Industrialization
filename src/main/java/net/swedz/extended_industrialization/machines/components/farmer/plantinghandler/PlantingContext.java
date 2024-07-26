package net.swedz.extended_industrialization.machines.components.farmer.plantinghandler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record PlantingContext(Level level, BlockPos pos, ItemStack stack)
{
}
