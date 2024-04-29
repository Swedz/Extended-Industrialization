package net.swedz.extended_industrialization.machines.guicomponents.modularnoninventoryslots;

import aztech.modern_industrialization.inventory.BackgroundRenderedSlot;
import aztech.modern_industrialization.inventory.SlotGroup;
import aztech.modern_industrialization.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.machines.gui.GuiComponent;
import aztech.modern_industrialization.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.machines.gui.MachineScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ModularNonInventorySlotsClient implements GuiComponentClient
{
	private final List<ModularNonInventorySlots.Slot> slots;
	
	private List<Integer> slotSizes;
	
	public ModularNonInventorySlotsClient(FriendlyByteBuf buf)
	{
		slots = buf.readCollection(ArrayList::new, ModularNonInventorySlots.Slot::read);
		this.readCurrentData(buf);
	}
	
	@Override
	public void readCurrentData(FriendlyByteBuf buf)
	{
		slotSizes = buf.readCollection(ArrayList::new, FriendlyByteBuf::readInt);
	}
	
	@Override
	public void setupMenu(GuiComponent.MenuFacade menu)
	{
		for(int i = 0; i < slots.size(); i++)
		{
			ModularNonInventorySlots.Slot slot = slots.get(i);
			int slotIndex = i;
			menu.addSlotToMenu(
					new SlotWithBackground(new SimpleContainer(1), 0, slot.x(), slot.y())
					{
						@Override
						public boolean mayPlace(ItemStack stack)
						{
							return slot.type().insertionChecker().test(stack);
						}
						
						@Override
						public int getMaxStackSize()
						{
							return slotSizes.get(slotIndex);
						}
						
						@Override
						public int getBackgroundU()
						{
							return !this.hasItem() ? slot.type().u() : 0;
						}
						
						@Override
						public int getBackgroundV()
						{
							return !this.hasItem() ? slot.type().v() : 0;
						}
						
						@Override
						public Component getTooltip()
						{
							return slot.type().tooltip();
						}
					},
					SlotGroup.CONFIGURABLE_STACKS
			);
		}
	}
	
	@Override
	public ClientComponentRenderer createRenderer(MachineScreen machineScreen)
	{
		return new Renderer();
	}
	
	private static final class Renderer implements ClientComponentRenderer
	{
		@Override
		public void renderBackground(GuiGraphics guiGraphics, int x, int y)
		{
		}
		
		@Override
		public void renderTooltip(MachineScreen screen, Font font, GuiGraphics guiGraphics, int x, int y, int cursorX, int cursorY)
		{
			if(screen.getFocusedSlot() instanceof SlotTooltip st && !screen.getFocusedSlot().hasItem())
			{
				guiGraphics.renderTooltip(font, st.getTooltip(), cursorX, cursorY);
			}
		}
	}
	
	interface SlotTooltip
	{
		Component getTooltip();
	}
	
	private static abstract class SlotWithBackground extends Slot implements BackgroundRenderedSlot, SlotTooltip
	{
		public SlotWithBackground(Container container, int index, int x, int y)
		{
			super(container, index, x, y);
		}
	}
}
