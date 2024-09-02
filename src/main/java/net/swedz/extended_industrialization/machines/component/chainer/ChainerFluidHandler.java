package net.swedz.extended_industrialization.machines.component.chainer;

import com.google.common.collect.Lists;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Collections;
import java.util.List;

public final class ChainerFluidHandler extends ChainerHandler<IFluidHandler> implements IFluidHandler
{
	public ChainerFluidHandler(MachineLinks machineLinks)
	{
		super(machineLinks);
	}
	
	@Override
	public void invalidate()
	{
		List<InventoryHandler<IFluidHandler>> handlers = Lists.newArrayList();
		int slots = 0;
		
		for(IFluidHandler handler : this.getMachineLinks().fluidHandlers())
		{
			int handlerSlots = handler.getTanks();
			handlers.add(new InventoryHandler<>(slots, handlerSlots, handler));
			slots += handlerSlots;
		}
		
		this.handlers = Collections.unmodifiableList(handlers);
		this.slots = slots;
	}
	
	@Override
	public int getTanks()
	{
		return slots;
	}
	
	@Override
	public FluidStack getFluidInTank(int tank)
	{
		return this.getHandler(tank).map((h) -> h.handler().getFluidInTank(h.toLocalSlot(tank))).orElse(FluidStack.EMPTY);
	}
	
	@Override
	public int getTankCapacity(int tank)
	{
		return this.getHandler(tank).map((h) -> h.handler().getTankCapacity(h.toLocalSlot(tank))).orElse(0);
	}
	
	@Override
	public boolean isFluidValid(int tank, FluidStack stack)
	{
		return this.getHandler(tank).map((h) -> h.handler().isFluidValid(h.toLocalSlot(tank), stack)).orElse(false);
	}
	
	@Override
	public int fill(FluidStack resource, FluidAction action)
	{
		int amountFilled = 0;
		for(int index = 0; index < handlers.size(); index++)
		{
			var handler = handlers.get(index);
			int remainingStorages = handlers.size() - index;
			int remainingAmountToInsert = resource.getAmount() - amountFilled;
			int amountToInsert = remainingAmountToInsert / remainingStorages;
			amountFilled += handler.handler().fill(resource.copyWithAmount(amountToInsert), action);
		}
		return amountFilled;
	}
	
	private FluidStack drain(Fluid fluid, int maxAmount, FluidAction action)
	{
		int amountTransferred = 0;
		for(int index = 0; index < handlers.size(); index++)
		{
			var handler = handlers.get(index);
			int remainingStorages = handlers.size() - index;
			int remainingAmountToTransfer = maxAmount - amountTransferred;
			int amountToTansfer = remainingAmountToTransfer / remainingStorages;
			FluidStack transferred = fluid == null ?
					handler.handler().drain(amountToTansfer, action) :
					handler.handler().drain(new FluidStack(fluid, amountToTansfer), action);
			if(!transferred.isEmpty())
			{
				fluid = transferred.getFluid();
				amountTransferred += transferred.getAmount();
			}
		}
		return fluid == null ? FluidStack.EMPTY : new FluidStack(fluid, amountTransferred);
	}
	
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action)
	{
		return this.drain(resource.getFluid(), resource.getAmount(), action);
	}
	
	@Override
	public FluidStack drain(int maxDrain, FluidAction action)
	{
		return this.drain(null, maxDrain, action);
	}
}
