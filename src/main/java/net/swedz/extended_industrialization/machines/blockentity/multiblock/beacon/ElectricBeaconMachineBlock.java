package net.swedz.extended_industrialization.machines.blockentity.multiblock.beacon;

import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;

public final class ElectricBeaconMachineBlock extends MachineBlock
{
	public ElectricBeaconMachineBlock(
			BiFunction<BlockPos, BlockState, ? extends MachineBlockEntity> blockEntityConstructor,
			Properties properties
	)
	{
		super(blockEntityConstructor, properties);
	}
	
	@Override
	protected RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}
}
