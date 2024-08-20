package net.swedz.extended_industrialization.items.machineconfig;

import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.util.Simulation;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.swedz.extended_industrialization.mixin.mi.accessor.ConfigurableItemStackAccessor;

import java.util.List;

public record MachineConfigSlots(
		List<MachineConfigSlot> slots, int itemSlotCount, int fluidSlotCount
) implements MachineConfigApplicable<MachineBlockEntity>
{
	public static final Codec<MachineConfigSlots> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.list(MachineConfigSlot.CODEC).fieldOf("slots").forGetter(MachineConfigSlots::slots)
			)
			.apply(instance, (slots) ->
			{
				int itemSlotCount = 0;
				int fluidSlotCount = 0;
				for(MachineConfigSlot slot : slots)
				{
					if(slot instanceof MachineConfigSlot.ItemSlot)
					{
						itemSlotCount++;
					}
					else if(slot instanceof MachineConfigSlot.FluidSlot)
					{
						fluidSlotCount++;
					}
				}
				return new MachineConfigSlots(slots, itemSlotCount, fluidSlotCount);
			})
	);
	
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
			if(slot instanceof MachineConfigSlot.ItemSlot itemSlot && itemSlot.capacity() >= 0)
			{
				ConfigurableItemStack itemStack = (ConfigurableItemStack) stack;
				if(itemStack.getAmount() <= itemSlot.capacity())
				{
					if(simulation.isActing())
					{
						ConfigurableItemStackAccessor capacityAccessor = (ConfigurableItemStackAccessor) stack;
						capacityAccessor.setAdjustedCapacity(itemSlot.capacity());
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
}
