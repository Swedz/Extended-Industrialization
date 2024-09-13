package net.swedz.extended_industrialization.machines.component.chainer.link.linkable;

import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.machines.component.chainer.link.ChainerLinkable;
import net.swedz.extended_industrialization.machines.component.chainer.link.LinkContext;
import net.swedz.extended_industrialization.machines.component.chainer.link.LinkResult;

public final class TaggedLinkable implements ChainerLinkable
{
	@Override
	public boolean matches(LinkContext context)
	{
		return context.hasBlockState() &&
			   context.blockState().is(EITags.Blocks.MACHINE_CHAINER_LINKABLE);
	}
	
	@Override
	public LinkResult test(LinkContext context)
	{
		if(context.hasItemStack())
		{
			return LinkResult.success();
		}
		else
		{
			IItemHandler itemHandler = context.level().getCapability(Capabilities.ItemHandler.BLOCK, context.pos(), context.blockState(), context.blockEntity(), null);
			IFluidHandler fluidHandler = context.level().getCapability(Capabilities.FluidHandler.BLOCK, context.pos(), context.blockState(), context.blockEntity(), null);
			MIEnergyStorage energyHandler = context.level().getCapability(EnergyApi.SIDED, context.pos(), context.blockState(), context.blockEntity(), null);
			if(itemHandler != null || fluidHandler != null || energyHandler != null)
			{
				return LinkResult.success(itemHandler, fluidHandler, energyHandler);
			}
			return LinkResult.fail(false);
		}
	}
}
