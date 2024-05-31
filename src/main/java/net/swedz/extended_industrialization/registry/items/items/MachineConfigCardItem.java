package net.swedz.extended_industrialization.registry.items.items;

import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class MachineConfigCardItem extends Item
{
	public MachineConfigCardItem(Properties properties)
	{
		super(properties);
	}
	
	private static MachineConfig readFromMachine(MachineBlockEntity machine)
	{
		Block block = machine.getBlockState().getBlock();
		
		MIInventory inventory = machine.getInventory();
		
		List<MachineConfig.Slot> slots = Lists.newArrayList();
		
		int itemIndex = 0;
		for(ConfigurableItemStack itemStack : inventory.getItemStacks())
		{
			slots.add(new MachineConfig.ItemSlot(itemIndex, itemStack.getAdjustedCapacity(), itemStack.getLockedInstance()));
			itemIndex++;
		}
		
		int fluidIndex = 0;
		for(ConfigurableFluidStack fluidStack : inventory.getFluidStacks())
		{
			slots.add(new MachineConfig.FluidSlot(fluidIndex, fluidStack.getLockedInstance()));
			fluidIndex++;
		}
		
		return new MachineConfig(block, slots, itemIndex, fluidIndex);
	}
	
	private static boolean writeToMachine(MachineBlockEntity machine, MachineConfig config, Simulation simulation)
	{
		if(!config.matches(machine))
		{
			return false;
		}
		
		boolean success = true;
		
		for(MachineConfig.Slot slot : config.slots())
		{
			AbstractConfigurableStack stack = slot.stack(machine.getInventory());
			if(slot.lock() != null && stack.canPlayerLock())
			{
				if(!stack.playerLock(slot.lock(), simulation))
				{
					success = false;
				}
			}
			if(slot.capacity() >= 0 && slot instanceof MachineConfig.ItemSlot)
			{
				ConfigurableItemStack itemStack = (ConfigurableItemStack) stack;
				// TODO set adjusted capacity using a mixin accessor
			}
		}
		
		return success;
	}
	
	private record MachineConfig(Block machineBlock, List<Slot> slots, int itemSlotCount, int fluidSlotCount)
	{
		public boolean matches(MachineBlockEntity machine)
		{
			return machine.getBlockState().getBlock() == machineBlock &&
					machine.getInventory().getItemStacks().size() == itemSlotCount &&
					machine.getInventory().getFluidStacks().size() == fluidSlotCount;
		}
		
		// TODO serialize & deserialize
		
		private record ItemSlot(int index, int capacity, Item lock) implements MachineConfig.Slot<Item, ConfigurableItemStack>
		{
			@Override
			public ConfigurableItemStack stack(MIInventory inventory)
			{
				return inventory.getItemStacks().get(index);
			}
		}
		
		private record FluidSlot(int index, Fluid lock) implements MachineConfig.Slot<Fluid, ConfigurableFluidStack>
		{
			@Override
			public int capacity()
			{
				return -1;
			}
			
			@Override
			public ConfigurableFluidStack stack(MIInventory inventory)
			{
				return inventory.getFluidStacks().get(index);
			}
		}
		
		interface Slot<T, S extends AbstractConfigurableStack>
		{
			int index();
			
			int capacity();
			
			T lock();
			
			S stack(MIInventory inventory);
		}
	}
}
