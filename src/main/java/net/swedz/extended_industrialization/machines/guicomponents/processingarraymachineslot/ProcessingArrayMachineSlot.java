package net.swedz.extended_industrialization.machines.guicomponents.processingarraymachineslot;

import aztech.modern_industrialization.inventory.HackySlot;
import aztech.modern_industrialization.inventory.SlotGroup;
import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.blockentities.ElectricCraftingMachineBlockEntity;
import aztech.modern_industrialization.machines.gui.GuiComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.components.craft.processingarray.ProcessingArrayMachineComponent;

import java.util.function.Supplier;

public final class ProcessingArrayMachineSlot
{
	public static final ResourceLocation ID = EI.id("processing_array_machine_slot");
	
	public static int getSlotX(MachineGuiParameters guiParameters)
	{
		return guiParameters.backgroundWidth + 6;
	}
	
	public static int getSlotY()
	{
		return 86;
	}
	
	public static boolean isMachine(ItemStack itemStack)
	{
		return itemStack.getItem() instanceof BlockItem blockItem &&
			   blockItem.getBlock() instanceof MachineBlock machineBlock &&
			   machineBlock.getBlockEntityInstance() instanceof ElectricCraftingMachineBlockEntity;
	}
	
	public static ElectricCraftingMachineBlockEntity getMachine(ItemStack itemStack)
	{
		return (ElectricCraftingMachineBlockEntity) ((MachineBlock) ((BlockItem) itemStack.getItem()).getBlock()).getBlockEntityInstance();
	}
	
	public static final class Server implements GuiComponent.Server<Integer>
	{
		private final MachineBlockEntity machine;
		
		private final Supplier<Integer> getMaxMachines;
		
		private final ProcessingArrayMachineComponent machines;
		
		public Server(MachineBlockEntity machine, Supplier<Integer> getMaxMachines, ProcessingArrayMachineComponent machines)
		{
			this.machine = machine;
			this.getMaxMachines = getMaxMachines;
			this.machines = machines;
		}
		
		@Override
		public Integer copyData()
		{
			return getMaxMachines.get();
		}
		
		@Override
		public boolean needsSync(Integer cachedData)
		{
			return !cachedData.equals(getMaxMachines.get());
		}
		
		@Override
		public void writeInitialData(FriendlyByteBuf buf)
		{
			this.writeCurrentData(buf);
		}
		
		@Override
		public void writeCurrentData(FriendlyByteBuf buf)
		{
			buf.writeInt(getMaxMachines.get());
		}
		
		@Override
		public ResourceLocation getId()
		{
			return ID;
		}
		
		@Override
		public void setupMenu(GuiComponent.MenuFacade menu)
		{
			menu.addSlotToMenu(
					new HackySlot(getSlotX(machine.guiParams), getSlotY())
					{
						@Override
						protected ItemStack getRealStack()
						{
							return machines.getMachines();
						}
						
						@Override
						protected void setRealStack(ItemStack itemStack)
						{
							machines.setMachines(machine, itemStack);
						}
						
						@Override
						public boolean mayPlace(ItemStack itemStack)
						{
							return isMachine(itemStack);
						}
						
						@Override
						public int getMaxStackSize()
						{
							return getMaxMachines.get();
						}
					},
					SlotGroup.CONFIGURABLE_STACKS
			);
		}
	}
}
