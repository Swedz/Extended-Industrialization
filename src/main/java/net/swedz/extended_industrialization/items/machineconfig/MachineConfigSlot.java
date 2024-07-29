package net.swedz.extended_industrialization.items.machineconfig;

import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public interface MachineConfigSlot<T, S extends AbstractConfigurableStack> extends MachineConfigSerializable
{
	int index();
	
	int capacity();
	
	T lock();
	
	S stack(MIInventory inventory);
	
	record ItemSlot(int index, int capacity, Item lock) implements MachineConfigSlot<Item, ConfigurableItemStack>
	{
		@Override
		public ConfigurableItemStack stack(MIInventory inventory)
		{
			return inventory.getItemStacks().get(index);
		}
		
		@Override
		public CompoundTag serialize()
		{
			CompoundTag tag = new CompoundTag();
			
			tag.putInt("index", index);
			tag.putString("type", "item");
			
			tag.putString("lock", BuiltInRegistries.ITEM.getKey(lock).toString());
			tag.putInt("capacity", capacity);
			
			return tag;
		}
	}
	
	record FluidSlot(int index, Fluid lock) implements MachineConfigSlot<Fluid, ConfigurableFluidStack>
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
		
		@Override
		public CompoundTag serialize()
		{
			CompoundTag tag = new CompoundTag();
			
			tag.putInt("index", index);
			tag.putString("type", "fluid");
			
			tag.putString("lock", BuiltInRegistries.FLUID.getKey(lock).toString());
			
			return tag;
		}
	}
}
