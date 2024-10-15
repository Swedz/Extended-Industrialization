package net.swedz.extended_industrialization.machines.component.itemslot;

import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.DropableComponent;
import aztech.modern_industrialization.machines.components.OverdriveComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.components.UpgradeComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class SimpleItemStackComponent implements IComponent, DropableComponent
{
	protected final String stackTagKey;
	
	protected ItemStack stack = ItemStack.EMPTY;
	
	public SimpleItemStackComponent(String stackTagKey)
	{
		this.stackTagKey = stackTagKey;
	}
	
	public ItemStack getStack()
	{
		return stack;
	}
	
	public void setStackServer(MachineBlockEntity machine, ItemStack stack)
	{
		this.stack = stack;
		machine.setChanged();
		machine.sync();
	}
	
	@Override
	public ItemStack getDrop()
	{
		return stack;
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		tag.put(stackTagKey, stack.saveOptional(registries));
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
		stack = ItemStack.parseOptional(registries, tag.getCompound(stackTagKey));
	}
	
	@Override
	public void writeClientNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
	}
	
	@Override
	public void readClientNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
	}
	
	public static SimpleItemStackComponent wrap(RedstoneControlComponent component)
	{
		return new SimpleItemStackComponent("redstoneModuleStack")
		{
			@Override
			public ItemStack getStack()
			{
				return component.getDrop();
			}
			
			@Override
			public void setStackServer(MachineBlockEntity machine, ItemStack stack)
			{
				component.setStackServer(machine, stack);
			}
			
			@Override
			public ItemStack getDrop()
			{
				return component.getDrop();
			}
			
			@Override
			public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
			{
				component.writeNbt(tag, registries);
			}
			
			@Override
			public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
			{
				component.readNbt(tag, registries, isUpgradingMachine);
			}
			
			@Override
			public void writeClientNbt(CompoundTag tag, HolderLookup.Provider registries)
			{
				component.writeClientNbt(tag, registries);
			}
			
			@Override
			public void readClientNbt(CompoundTag tag, HolderLookup.Provider registries)
			{
				component.readClientNbt(tag, registries);
			}
		};
	}
	
	public static SimpleItemStackComponent wrap(UpgradeComponent component)
	{
		return new SimpleItemStackComponent("upgradesItemStack")
		{
			@Override
			public ItemStack getStack()
			{
				return component.getDrop();
			}
			
			@Override
			public void setStackServer(MachineBlockEntity machine, ItemStack stack)
			{
				component.setStackServer(machine, stack);
			}
			
			@Override
			public ItemStack getDrop()
			{
				return component.getDrop();
			}
			
			@Override
			public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
			{
				component.writeNbt(tag, registries);
			}
			
			@Override
			public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
			{
				component.readNbt(tag, registries, isUpgradingMachine);
			}
			
			@Override
			public void writeClientNbt(CompoundTag tag, HolderLookup.Provider registries)
			{
				component.writeClientNbt(tag, registries);
			}
			
			@Override
			public void readClientNbt(CompoundTag tag, HolderLookup.Provider registries)
			{
				component.readClientNbt(tag, registries);
			}
		};
	}
	
	public static SimpleItemStackComponent wrap(OverdriveComponent component)
	{
		return new SimpleItemStackComponent("overdriveModuleStack")
		{
			@Override
			public ItemStack getStack()
			{
				return component.getDrop();
			}
			
			@Override
			public void setStackServer(MachineBlockEntity machine, ItemStack stack)
			{
				component.setStackServer(machine, stack);
			}
			
			@Override
			public ItemStack getDrop()
			{
				return component.getDrop();
			}
			
			@Override
			public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
			{
				component.writeNbt(tag, registries);
			}
			
			@Override
			public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
			{
				component.readNbt(tag, registries, isUpgradingMachine);
			}
			
			@Override
			public void writeClientNbt(CompoundTag tag, HolderLookup.Provider registries)
			{
				component.writeClientNbt(tag, registries);
			}
			
			@Override
			public void readClientNbt(CompoundTag tag, HolderLookup.Provider registries)
			{
				component.readClientNbt(tag, registries);
			}
		};
	}
	
	public static SimpleItemStackComponent wrap(CasingComponent component)
	{
		return new SimpleItemStackComponent("casing")
		{
			@Override
			public ItemStack getStack()
			{
				return component.getDrop();
			}
			
			@Override
			public void setStackServer(MachineBlockEntity machine, ItemStack stack)
			{
				component.setCasingServer(machine, stack);
			}
			
			@Override
			public ItemStack getDrop()
			{
				return component.getDrop();
			}
			
			@Override
			public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
			{
				component.writeNbt(tag, registries);
			}
			
			@Override
			public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
			{
				component.readNbt(tag, registries, isUpgradingMachine);
			}
			
			@Override
			public void writeClientNbt(CompoundTag tag, HolderLookup.Provider registries)
			{
				component.writeClientNbt(tag, registries);
			}
			
			@Override
			public void readClientNbt(CompoundTag tag, HolderLookup.Provider registries)
			{
				component.readClientNbt(tag, registries);
			}
		};
	}
}
