package net.swedz.miextended.capabilities;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public final class BottleFluidHandler implements IFluidHandlerItem
{
	private ItemStack container;
	
	private final ItemStack itemContaining;
	private final ItemStack itemEmpty;
	private final Fluid     fluid;
	private final int       capacity;
	
	public BottleFluidHandler(ItemStack container, ItemStack itemContaining, ItemStack itemEmpty, Fluid fluid, int capacity)
	{
		this.container = container.copy();
		this.itemContaining = itemContaining.copyWithCount(1);
		this.itemEmpty = itemEmpty.copyWithCount(1);
		this.fluid = fluid;
		this.capacity = capacity;
	}
	
	public BottleFluidHandler(ItemStack container, Item itemContaining, Item itemEmpty, Fluid fluid, int capacity)
	{
		this(container, new ItemStack(itemContaining), new ItemStack(itemEmpty), fluid, capacity);
	}
	
	@Override
	public ItemStack getContainer()
	{
		return container;
	}
	
	@Override
	public int getTanks()
	{
		return 1;
	}
	
	@Override
	public FluidStack getFluidInTank(int tank)
	{
		if(tank == 0 && ItemStack.matches(container, itemContaining))
		{
			return new FluidStack(fluid, capacity);
		}
		return FluidStack.EMPTY;
	}
	
	@Override
	public int getTankCapacity(int tank)
	{
		return tank == 0 ? capacity : 0;
	}
	
	@Override
	public boolean isFluidValid(int tank, FluidStack stack)
	{
		return tank == 0 && stack.getFluid() == fluid;
	}
	
	@Override
	public int fill(FluidStack resource, FluidAction action)
	{
		if(!ItemStack.matches(container, itemEmpty) || resource.getFluid() != fluid || resource.getAmount() < capacity)
		{
			return 0;
		}
		if(action.execute())
		{
			container.shrink(1);
			container = itemContaining.copy();
		}
		return capacity;
	}
	
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action)
	{
		if(resource.getFluid() == fluid && ItemStack.matches(container, itemContaining))
		{
			return drain(capacity, action);
		}
		return FluidStack.EMPTY;
	}
	
	@Override
	public FluidStack drain(int maxDrain, FluidAction action)
	{
		if(ItemStack.matches(container, itemContaining) && maxDrain >= capacity)
		{
			if(action.execute())
			{
				container.shrink(1);
				container = itemEmpty.copy();
			}
			return new FluidStack(fluid, capacity);
		}
		return FluidStack.EMPTY;
	}
}
