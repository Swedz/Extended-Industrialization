package net.swedz.extended_industrialization.machines.component.chainer.link.linkable;

import aztech.modern_industrialization.api.energy.EnergyApi;
import net.neoforged.neoforge.capabilities.Capabilities;
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
		else if(context.hasBlockEntity())
		{
			var itemHandler = context.level().getCapability(Capabilities.ItemHandler.BLOCK, context.pos(), context.blockState(), context.blockEntity(), null);
			var fluidHandler = context.level().getCapability(Capabilities.FluidHandler.BLOCK, context.pos(), context.blockState(), context.blockEntity(), null);
			var energyHandler = context.level().getCapability(EnergyApi.SIDED, context.pos(), context.blockState(), context.blockEntity(), null);
			if(itemHandler != null || fluidHandler != null || energyHandler != null)
			{
				return LinkResult.success(itemHandler, fluidHandler, energyHandler);
			}
		}
		return LinkResult.fail(false);
	}
}
