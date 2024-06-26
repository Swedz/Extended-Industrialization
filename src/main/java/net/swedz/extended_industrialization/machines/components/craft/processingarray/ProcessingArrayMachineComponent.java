package net.swedz.extended_industrialization.machines.components.craft.processingarray;

import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.DropableComponent;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.guicomponents.processingarraymachineslot.ProcessingArrayMachineSlot;

public final class ProcessingArrayMachineComponent implements IComponent.ServerOnly, DropableComponent
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
		this.machines = machines;
		this.machineRecipeType = machines.isEmpty() ? null : ProcessingArrayMachineSlot.getMachine(machines).recipeType();
		be.setChanged();
		be.sync();
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
		tag.put("machinesStack", machines.save(new CompoundTag()));
	}
	
	@Override
	public void readNbt(CompoundTag tag, boolean isUpgradingMachine)
	{
		machines = ItemStack.of(tag.getCompound("machinesStack"));
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
}
