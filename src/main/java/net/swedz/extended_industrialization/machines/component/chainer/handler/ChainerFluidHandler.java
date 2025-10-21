package net.swedz.extended_industrialization.machines.component.chainer.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerLinks;
import net.swedz.extended_industrialization.machines.component.chainer.wrapper.SlotInventoryWrapper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class ChainerFluidHandler extends SlotChainerHandler<IFluidHandler> implements IFluidHandler
{
	public ChainerFluidHandler(ChainerLinks chainerLinks)
	{
		super(chainerLinks);
	}
	
	@Override
	public void invalidate()
	{
		List<SlotInventoryWrapper<IFluidHandler>> wrappers = Lists.newArrayList();
		int slots = 0;
		
		for(IFluidHandler handler : this.getMachineLinks().fluidHandlers())
		{
			int handlerSlots = handler.getTanks();
			wrappers.add(new SlotInventoryWrapper<>(handler, slots, handlerSlots));
			slots += handlerSlots;
		}
		
		this.wrappers = Collections.unmodifiableList(wrappers);
		this.wrappersSlotMap = Maps.newConcurrentMap();
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
		var wrapper = this.getWrapper(tank);
		return wrapper != null ? wrapper.handler().getFluidInTank(wrapper.toLocalSlot(tank)) : FluidStack.EMPTY;
	}
	
	@Override
	public int getTankCapacity(int tank)
	{
		var wrapper = this.getWrapper(tank);
		return wrapper != null ? wrapper.handler().getTankCapacity(wrapper.toLocalSlot(tank)) : 0;
	}
	
	@Override
	public boolean isFluidValid(int tank, FluidStack stack)
	{
		var wrapper = this.getWrapper(tank);
		return wrapper != null && wrapper.handler().isFluidValid(wrapper.toLocalSlot(tank), stack);
	}
	
	private static final class Bucket
	{
		private final SlotInventoryWrapper<IFluidHandler> wrapper;
		private final int                                 simulationResult;
		
		private Bucket(SlotInventoryWrapper<IFluidHandler> wrapper, int simulationResult)
		{
			this.wrapper = wrapper;
			this.simulationResult = simulationResult;
		}
	}
	
	@Override
	public int fill(FluidStack resource, FluidAction action)
	{
		if(!chainerLinks.doesAllowOperation())
		{
			return 0;
		}
		
		List<Bucket> buckets = Lists.newArrayList();
		var shuffledWrappers = Lists.newArrayList(wrappers);
		Collections.shuffle(shuffledWrappers);
		for(var wrapper : shuffledWrappers)
		{
			buckets.add(new Bucket(wrapper, wrapper.handler().fill(resource, FluidAction.SIMULATE)));
		}
		buckets.sort(Comparator.comparingInt((bucket) -> bucket.simulationResult));
		
		int amountFilled = 0;
		for(int index = 0; index < buckets.size(); index++)
		{
			var wrapper = buckets.get(index).wrapper;
			int remainingStorages = buckets.size() - index;
			int remainingAmountToInsert = resource.getAmount() - amountFilled;
			int amountToInsert = remainingAmountToInsert / remainingStorages;
			amountFilled += wrapper.handler().fill(resource.copyWithAmount(amountToInsert), action);
		}
		return amountFilled;
	}
	
	private FluidStack drain(Fluid fluid, int maxAmount, FluidAction action)
	{
		if(!chainerLinks.doesAllowOperation())
		{
			return FluidStack.EMPTY;
		}
		
		List<Bucket> buckets = Lists.newArrayList();
		var shuffledWrappers = Lists.newArrayList(wrappers);
		Collections.shuffle(shuffledWrappers);
		for(var wrapper : shuffledWrappers)
		{
			var simulationResult = fluid == null ?
					wrapper.handler().drain(maxAmount, FluidAction.SIMULATE) :
					wrapper.handler().drain(new FluidStack(fluid, maxAmount), FluidAction.SIMULATE);
			if(!simulationResult.isEmpty())
			{
				fluid = simulationResult.getFluid();
			}
			buckets.add(new Bucket(wrapper, simulationResult.getAmount()));
		}
		
		if(fluid == null || fluid == Fluids.EMPTY)
		{
			return FluidStack.EMPTY;
		}
		
		buckets.sort(Comparator.comparingInt((bucket) -> bucket.simulationResult));
		
		int amountTransferred = 0;
		for(int index = 0; index < buckets.size(); index++)
		{
			var wrapper = buckets.get(index).wrapper;
			int remainingStorages = buckets.size() - index;
			int remainingAmountToTransfer = maxAmount - amountTransferred;
			int amountToTansfer = remainingAmountToTransfer / remainingStorages;
			FluidStack transferred = wrapper.handler().drain(new FluidStack(fluid, amountToTansfer), action);
			amountTransferred += transferred.getAmount();
		}
		return new FluidStack(fluid, amountTransferred);
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
