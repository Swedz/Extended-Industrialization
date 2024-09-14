package net.swedz.extended_industrialization.machines.component.chainer.link.linkable;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.core.Direction;
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
	
	private MIEnergyStorage pickEnergyHandler(boolean input, MIEnergyStorage handlerA, MIEnergyStorage handlerB)
	{
		return handlerA != null && (input ? handlerA.canReceive() : handlerA.canExtract()) ? handlerA :
				handlerB != null && (input ? handlerB.canReceive() : handlerB.canExtract()) ? handlerB : null;
	}
	
	private MIEnergyStorage combine(MIEnergyStorage input, MIEnergyStorage output)
	{
		return input != null || output != null ? new MIEnergyStorage()
		{
			@Override
			public boolean canConnect(CableTier cableTier)
			{
				return true;
			}
			
			@Override
			public long receive(long maxReceive, boolean simulate)
			{
				return input != null ? input.receive(maxReceive, simulate) : 0;
			}
			
			@Override
			public long extract(long maxExtract, boolean simulate)
			{
				return output != null ? output.extract(maxExtract, simulate) : 0;
			}
			
			@Override
			public long getAmount()
			{
				return input != null ? input.getAmount() : 0;
			}
			
			@Override
			public long getCapacity()
			{
				return input != null ? input.getCapacity() : 0;
			}
			
			@Override
			public boolean canExtract()
			{
				return output != null;
			}
			
			@Override
			public boolean canReceive()
			{
				return input != null;
			}
		} : null;
	}
	
	@Override
	public LinkResult test(LinkContext context)
	{
		Direction outputDirection = null;
		
		if(!context.hasItemStack() && context.hasBlockEntity())
		{
			if(context.blockEntity() instanceof MachineChainerMachineBlockEntity chainerBlockEntity)
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
			
			MachineBlockEntity machineBlockEntity = (MachineBlockEntity) context.blockEntity();
			if(machineBlockEntity.orientation.params.hasOutput)
			{
				outputDirection = machineBlockEntity.orientation.outputDirection;
			}
		}
		
		IItemHandler itemHandler = context.level().getCapability(Capabilities.ItemHandler.BLOCK, context.pos(), context.blockState(), context.blockEntity(), null);
		IFluidHandler fluidHandler = context.level().getCapability(Capabilities.FluidHandler.BLOCK, context.pos(), context.blockState(), context.blockEntity(), null);
		MIEnergyStorage energyHandlerA = context.level().getCapability(EnergyApi.SIDED, context.pos(), context.blockState(), context.blockEntity(), null);
		MIEnergyStorage energyHandlerB = outputDirection != null ? context.level().getCapability(EnergyApi.SIDED, context.pos(), context.blockState(), context.blockEntity(), outputDirection) : null;
		MIEnergyStorage inputEnergyHandler = this.pickEnergyHandler(true, energyHandlerA, energyHandlerB);
		MIEnergyStorage outputEnergyHandler = this.pickEnergyHandler(false, energyHandlerA, energyHandlerB);
		MIEnergyStorage energyHandler = this.combine(inputEnergyHandler, outputEnergyHandler);
		if(itemHandler != null || fluidHandler != null || energyHandler != null)
		{
			return LinkResult.success(itemHandler, fluidHandler, energyHandler);
		}
		
		return LinkResult.fail(false);
	}
}
