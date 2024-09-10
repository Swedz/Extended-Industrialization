package net.swedz.extended_industrialization.machines.component.chainer.link.linkable;

import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.component.chainer.link.ChainerLinkable;
import net.swedz.extended_industrialization.machines.component.chainer.link.LinkContext;
import net.swedz.extended_industrialization.machines.component.chainer.link.LinkResult;

public final class MachineBlockLinkable implements ChainerLinkable
{
	@Override
	public boolean matches(LinkContext context)
	{
		if(context.hasItemStack())
		{
			return context.itemStack().getItem() instanceof BlockItem blockItem &&
				   blockItem.getBlock() instanceof MachineBlock;
		}
		else
		{
			return context.hasBlockEntity() &&
				   context.blockEntity() instanceof MachineBlockEntity;
		}
	}
	
	@Override
	public LinkResult test(LinkContext context)
	{
		if(!context.hasItemStack())
		{
			if(context.hasBlockEntity() && context.blockEntity() instanceof MachineChainerMachineBlockEntity chainerBlockEntity)
			{
				if(chainerBlockEntity.orientation.facingDirection == context.links().direction() ||
				   chainerBlockEntity.orientation.facingDirection.getOpposite() == context.links().direction())
				{
					return LinkResult.fail(true, context.pos());
				}
				if(chainerBlockEntity.getChainerComponent().links().contains(context.links().origin(), true))
				{
					return LinkResult.fail(true, context.pos());
				}
			}
		}
		
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
