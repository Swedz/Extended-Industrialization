package net.swedz.extended_industrialization.machines.component.chainer.link;

import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Optional;

public record LinkResult(
		boolean isSuccess,
		Optional<IItemHandler> itemHandler,
		Optional<IFluidHandler> fluidHandler,
		Optional<MIEnergyStorage> energyHandler,
		boolean invalidatesEverything,
		Optional<BlockPos> failPosition
)
{
	public static LinkResult fail(boolean invalidatesEverything, BlockPos failPosition)
	{
		return new LinkResult(
				false,
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				invalidatesEverything,
				Optional.ofNullable(failPosition)
		);
	}
	
	public static LinkResult fail(boolean invalidatesEverything)
	{
		return fail(invalidatesEverything, null);
	}
	
	public static LinkResult success(IItemHandler itemHandler, IFluidHandler fluidHandler, MIEnergyStorage energyHandler)
	{
		return new LinkResult(
				true,
				Optional.ofNullable(itemHandler),
				Optional.ofNullable(fluidHandler),
				Optional.ofNullable(energyHandler),
				false,
				Optional.empty()
		);
	}
	
	public static LinkResult success()
	{
		return success(null, null, null);
	}
}
