package net.swedz.extended_industrialization.items.machineconfig;

import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.util.Simulation;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.swedz.extended_industrialization.mixin.mi.accessor.ConfigurableItemStackAccessor;

import java.util.List;

public record MachineConfig(Block machineBlock, List<Slot> slots, int itemSlotCount, int fluidSlotCount)
{
	public static final Codec<MachineConfig>                CODEC        = Codec
			.withAlternative(CompoundTag.CODEC, TagParser.AS_CODEC)
			.xmap(MachineConfig::fromNBT, MachineConfig::serialize);
	public static final StreamCodec<ByteBuf, MachineConfig> STREAM_CODEC = ByteBufCodecs.COMPOUND_TAG
			.map(MachineConfig::fromNBT, MachineConfig::serialize);
	
	public boolean matches(MachineBlockEntity machine)
	{
		return machine.getBlockState().getBlock() == machineBlock &&
			   machine.getInventory().getItemStacks().size() == itemSlotCount &&
			   machine.getInventory().getFluidStacks().size() == fluidSlotCount;
	}
	
	public static MachineConfig fromMachine(MachineBlockEntity machine)
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
	
	public boolean writeToMachine(MachineBlockEntity machine, Simulation simulation)
	{
		if(!this.matches(machine))
		{
			return false;
		}
		
		boolean success = true;
		
		for(MachineConfig.Slot slot : this.slots())
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
				if(itemStack.getAmount() <= slot.capacity())
				{
					if(simulation == Simulation.ACT)
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
	
	private static MachineConfig fromNBT(CompoundTag tag)
	{
		Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(tag.getString("machine_block")));
		
		List<MachineConfig.Slot> slots = Lists.newArrayList();
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
				slots.add(new MachineConfig.ItemSlot(index, capacity, lock));
				itemSlotCount++;
			}
			else if(typeId.equals("fluid"))
			{
				Fluid lock = BuiltInRegistries.FLUID.get(ResourceLocation.parse(slotTag.getString("lock")));
				slots.add(new MachineConfig.FluidSlot(index, lock));
				fluidSlotCount++;
			}
			else
			{
				throw new IllegalArgumentException("Malformed machine config nbt: %s".formatted(tag.toString()));
			}
		}
		
		return new MachineConfig(block, slots, itemSlotCount, fluidSlotCount);
	}
	
	public CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putString("machine_block", BuiltInRegistries.BLOCK.getKey(machineBlock).toString());
		
		ListTag slotsTag = new ListTag();
		for(Slot slot : slots)
		{
			slotsTag.add(slot.serialize());
		}
		tag.put("slots", slotsTag);
		
		return tag;
	}
	
	private record ItemSlot(
			int index, int capacity, Item lock
	) implements Slot<Item, ConfigurableItemStack>
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
	
	private record FluidSlot(int index, Fluid lock) implements Slot<Fluid, ConfigurableFluidStack>
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
	
	interface Slot<T, S extends AbstractConfigurableStack>
	{
		int index();
		
		int capacity();
		
		T lock();
		
		S stack(MIInventory inventory);
		
		CompoundTag serialize();
	}
}
