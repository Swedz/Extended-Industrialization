package net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public record HarvestingContext(Level level, BlockPos pos, BlockState state)
{
}
