package net.swedz.miextended.machines.components.farmer.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public record FarmerTree(BlockPos base, List<BlockPos> blocks)
{
	public List<BlockState> blockStates(Level level)
	{
		return blocks.stream().map(level::getBlockState).toList();
	}
}
