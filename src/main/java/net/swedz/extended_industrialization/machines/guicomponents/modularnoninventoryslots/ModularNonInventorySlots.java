package net.swedz.extended_industrialization.machines.guicomponents.modularnoninventoryslots;

import aztech.modern_industrialization.inventory.HackySlot;
import aztech.modern_industrialization.inventory.SlotGroup;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.gui.GuiComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ModularNonInventorySlots
{
	public static final ResourceLocation ID = EI.id("modular_noninventory_slots");
	
	public static final class Server implements GuiComponent.Server<Data>
	{
		private final MachineBlockEntity machine;
		
		private final List<Slot>                              slots         = Lists.newArrayList();
		private final List<Consumer<GuiComponent.MenuFacade>> slotFactories = Lists.newArrayList();
		private final List<Supplier<Integer>>                 slotSizes     = Lists.newArrayList();
		
		public Server(MachineBlockEntity machine)
		{
			this.machine = machine;
		}
		
		public Server withSlot(int x, int y, ModularNonInventorySlotType type, Supplier<ItemStack> getStack, BiConsumer<MachineBlockEntity, ItemStack> setStack, Supplier<Integer> maxStackSize)
		{
			slots.add(new Slot(type, x, y));
			slotFactories.add((facade) -> facade.addSlotToMenu(
					new HackySlot(x, y)
					{
						@Override
						protected ItemStack getRealStack()
						{
							return getStack.get();
						}
						
						@Override
						protected void setRealStack(ItemStack stack)
						{
							setStack.accept(machine, stack);
						}
						
						@Override
						public boolean mayPlace(ItemStack stack)
						{
							return type.insertionChecker().test(stack);
						}
						
						@Override
						public int getMaxStackSize()
						{
							return maxStackSize.get();
						}
					},
					SlotGroup.CONFIGURABLE_STACKS
			));
			slotSizes.add(maxStackSize);
			return this;
		}
		
		private List<Integer> getSlotMaxStackSizes()
		{
			return slotSizes.stream().map(Supplier::get).toList();
		}
		
		@Override
		public Data copyData()
		{
			return new Data(this.getSlotMaxStackSizes());
		}
		
		@Override
		public boolean needsSync(Data cachedData)
		{
			return !this.getSlotMaxStackSizes().equals(cachedData.slotSizes());
		}
		
		@Override
		public void writeInitialData(FriendlyByteBuf buf)
		{
			buf.writeCollection(slots, Slot::write);
			this.writeCurrentData(buf);
		}
		
		@Override
		public void writeCurrentData(FriendlyByteBuf buf)
		{
			buf.writeCollection(this.getSlotMaxStackSizes(), FriendlyByteBuf::writeInt);
		}
		
		@Override
		public ResourceLocation getId()
		{
			return ID;
		}
		
		@Override
		public void setupMenu(GuiComponent.MenuFacade menu)
		{
			for(Consumer<GuiComponent.MenuFacade> slot : slotFactories)
			{
				slot.accept(menu);
			}
		}
	}
	
	private record Data(List<Integer> slotSizes)
	{
	}
	
	public record Slot(ModularNonInventorySlotType type, int x, int y)
	{
		public static Slot read(FriendlyByteBuf buf)
		{
			return new Slot(
					ModularNonInventorySlotType.get(buf.readResourceLocation()),
					buf.readInt(),
					buf.readInt()
			);
		}
		
		public static void write(FriendlyByteBuf buf, Slot slot)
		{
			buf.writeResourceLocation(slot.type().id());
			buf.writeInt(slot.x());
			buf.writeInt(slot.y());
		}
	}
}
