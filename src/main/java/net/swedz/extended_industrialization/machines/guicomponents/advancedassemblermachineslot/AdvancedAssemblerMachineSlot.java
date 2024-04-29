package net.swedz.extended_industrialization.machines.guicomponents.advancedassemblermachineslot;

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

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class AdvancedAssemblerMachineSlot
{
	public static final ResourceLocation ID = EI.id("advanced_assembler_machine_slot");
	
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
	
	public static final class Server implements GuiComponent.Server<Integer>
	{
		private final MachineBlockEntity machine;
		
		private final Supplier<Integer>                         getMaxMachines;
		private final Supplier<ItemStack>                       getMachineStack;
		private final BiConsumer<MachineBlockEntity, ItemStack> setMachineStack;
		
		public Server(MachineBlockEntity machine, Supplier<Integer> getMaxMachines, Supplier<ItemStack> getMachineStack, BiConsumer<MachineBlockEntity, ItemStack> setMachineStack)
		{
			this.machine = machine;
			this.getMaxMachines = getMaxMachines;
			this.getMachineStack = getMachineStack;
			this.setMachineStack = setMachineStack;
		}
		
		@Override
		public Integer copyData()
		{
			return getMaxMachines.get();
		}
		
		@Override
		public boolean needsSync(Integer cachedData)
		{
			return cachedData.equals(getMaxMachines.get());
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
							return getMachineStack.get();
						}
						
						@Override
						protected void setRealStack(ItemStack itemStack)
						{
							setMachineStack.accept(machine, itemStack);
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
