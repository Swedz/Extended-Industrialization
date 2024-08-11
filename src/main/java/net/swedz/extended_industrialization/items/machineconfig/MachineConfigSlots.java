package net.swedz.extended_industrialization.items.machineconfig;

import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.util.Simulation;
import com.google.common.collect.Lists;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.swedz.extended_industrialization.mixin.mi.accessor.ConfigurableItemStackAccessor;

import java.util.List;

public record MachineConfigSlots(
		List<MachineConfigSlot> slots, int itemSlotCount, int fluidSlotCount
) implements MachineConfigSerializable, MachineConfigApplicable<MachineBlockEntity>
{
	public static MachineConfigSlots from(MachineBlockEntity machine)
	{
		MIInventory inventory = machine.getInventory();
		
		List<MachineConfigSlot> slots = Lists.newArrayList();
		
		int itemIndex = 0;
		for(ConfigurableItemStack itemStack : inventory.getItemStacks())
		{
			slots.add(new MachineConfigSlot.ItemSlot(itemIndex, itemStack.getAdjustedCapacity(), itemStack.getLockedInstance()));
			itemIndex++;
		}
		
		int fluidIndex = 0;
		for(ConfigurableFluidStack fluidStack : inventory.getFluidStacks())
		{
			slots.add(new MachineConfigSlot.FluidSlot(fluidIndex, fluidStack.getLockedInstance()));
			fluidIndex++;
		}
		
		return new MachineConfigSlots(slots, itemIndex, fluidIndex);
	}
	
	public static MachineConfigSlots deserialize(CompoundTag tag)
	{
		List<MachineConfigSlot> slots = Lists.newArrayList();
		int itemSlotCount = 0;
		int fluidSlotCount = 0;
		
		ListTag slotsTag = tag.getList("slots", Tag.TAG_COMPOUND);
		for(Tag slotTagTag : slotsTag)
		{
			CompoundTag slotTag = (CompoundTag) slotTagTag;
			int index = slotTag.getInt("index");
			String typeId = slotTag.getString("type");
			
			if(typeId.equals("item"))
			{
				int capacity = slotTag.getInt("capacity");
				Item lock = BuiltInRegistries.ITEM.get(ResourceLocation.parse(slotTag.getString("lock")));
				slots.add(new MachineConfigSlot.ItemSlot(index, capacity, lock));
				itemSlotCount++;
			}
			else if(typeId.equals("fluid"))
			{
				Fluid lock = BuiltInRegistries.FLUID.get(ResourceLocation.parse(slotTag.getString("lock")));
				slots.add(new MachineConfigSlot.FluidSlot(index, lock));
				fluidSlotCount++;
			}
			else
			{
				throw new IllegalArgumentException("Malformed machine config nbt: %s".formatted(tag.toString()));
			}
		}
		
		return new MachineConfigSlots(slots, itemSlotCount, fluidSlotCount);
	}
	
	@Override
	public boolean matches(MachineBlockEntity target)
	{
		return target.getInventory().getItemStacks().size() == itemSlotCount &&
			   target.getInventory().getFluidStacks().size() == fluidSlotCount;
	}
	
	@Override
	public boolean apply(Player player, MachineBlockEntity target, Simulation simulation)
	{
		if(!this.matches(target))
		{
			return false;
		}
		
		boolean success = true;
		
		for(MachineConfigSlot slot : slots)
		{
			AbstractConfigurableStack stack = slot.stack(target.getInventory());
			if(slot.lock() != null && stack.canPlayerLock())
			{
				if(!stack.playerLock(slot.lock(), simulation))
				{
					success = false;
				}
			}
			if(slot.capacity() >= 0 && slot instanceof MachineConfigSlot.ItemSlot)
			{
				ConfigurableItemStack itemStack = (ConfigurableItemStack) stack;
				if(itemStack.getAmount() <= slot.capacity())
				{
					if(simulation.isActing())
					{
						ConfigurableItemStackAccessor capacityAccessor = (ConfigurableItemStackAccessor) stack;
						capacityAccessor.setAdjustedCapacity(slot.capacity());
					}
				}
				else
				{
					success = false;
				}
			}
		}
		
		return success;
	}
	
	@Override
	public Tag serialize()
	{
		ListTag slotsTag = new ListTag();
		
		for(MachineConfigSlot slot : slots)
		{
			slotsTag.add(slot.serialize());
		}
		
		return slotsTag;
	}
}
