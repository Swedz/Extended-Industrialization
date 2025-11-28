package net.swedz.extended_industrialization.machines.component.craft.processingarray;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.MachineComponent;
import aztech.modern_industrialization.machines.components.DropableComponent;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.guicomponent.processingarraymachineslot.ProcessingArrayMachineSlot;
import net.swedz.tesseract.neoforge.compat.mi.api.ComponentStackHolder;

public final class ProcessingArrayMachineComponent implements MachineComponent.ServerOnly, DropableComponent, ComponentStackHolder
{
	public static final ResourceLocation ID = EI.id("processing_array_machine");
	
	private ItemStack machines = ItemStack.EMPTY;
	
	private MachineRecipeType machineRecipeType;
	
	public ItemStack getMachines()
	{
		return machines;
	}
	
	public boolean hasMachines()
	{
		return !machines.isEmpty() && machineRecipeType != null;
	}
	
	public int getMachineCount()
	{
		return machines.getCount();
	}
	
	public MachineRecipeType getMachineRecipeType()
	{
		return machineRecipeType;
	}
	
	public void setMachines(MachineBlockEntity be, ItemStack machines)
	{
		this.setStack(machines);
		be.setChanged();
		be.sync();
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		tag.put("machinesStack", machines.saveOptional(registries));
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
		machines = ItemStack.parseOptional(registries, tag.getCompound("machinesStack"));
		if(!machines.isEmpty())
		{
			machineRecipeType = ProcessingArrayMachineSlot.getMachine(machines).recipeType();
		}
	}
	
	@Override
	public ItemStack getDrop()
	{
		return machines;
	}
	
	@Override
	public ItemStack getStack()
	{
		return machines;
	}
	
	@Override
	public void setStack(ItemStack stack)
	{
		machines = stack;
		machineRecipeType = machines.isEmpty() ? null : ProcessingArrayMachineSlot.getMachine(machines).recipeType();
	}
}
