package net.swedz.extended_industrialization.machines.guicomponents.processingarraymachineslot;

import aztech.modern_industrialization.inventory.BackgroundRenderedSlot;
import aztech.modern_industrialization.inventory.SlotGroup;
import aztech.modern_industrialization.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.machines.gui.GuiComponent;
import aztech.modern_industrialization.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.machines.gui.MachineScreen;
import aztech.modern_industrialization.util.Rectangle;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EIText;

import java.util.List;

public final class ProcessingArrayMachineSlotClient implements GuiComponentClient
{
	private int maxMachines;
	
	public ProcessingArrayMachineSlotClient(RegistryFriendlyByteBuf buf)
	{
		this.readCurrentData(buf);
	}
	
	@Override
	public void readCurrentData(RegistryFriendlyByteBuf buf)
	{
		maxMachines = buf.readInt();
	}
	
	@Override
	public void setupMenu(GuiComponent.MenuFacade menu)
	{
		class ClientSlot extends SlotWithBackground implements SlotTooltip
		{
			public ClientSlot()
			{
				super(new SimpleContainer(1), 0, ProcessingArrayMachineSlot.getSlotX(menu.getGuiParams()), ProcessingArrayMachineSlot.getSlotY());
			}
			
			@Override
			public boolean mayPlace(ItemStack itemStack)
			{
				return ProcessingArrayMachineSlot.isMachine(itemStack);
			}
			
			@Override
			public int getMaxStackSize()
			{
				return maxMachines;
			}
			
			@Override
			public int getBackgroundU()
			{
				return 18;
			}
			
			@Override
			public int getBackgroundV()
			{
				return 80;
			}
			
			@Override
			public Component getTooltip()
			{
				return EIText.PROCESSING_ARRAY_MACHINE_INPUT.text().withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xA9A9A9)));
			}
		}
		
		menu.addSlotToMenu(new ClientSlot(), SlotGroup.CONFIGURABLE_STACKS);
	}
	
	@Override
	public ClientComponentRenderer createRenderer(MachineScreen machineScreen)
	{
		return new ClientComponentRenderer()
		{
			private Rectangle getBox(int leftPos, int topPos)
			{
				return new Rectangle(leftPos + machineScreen.getGuiParams().backgroundWidth, topPos + 10 + ProcessingArrayMachineSlot.getSlotY() - 19, 31, 34);
			}
			
			@Override
			public void addExtraBoxes(List<Rectangle> rectangles, int leftPos, int topPos)
			{
				rectangles.add(this.getBox(leftPos, topPos));
			}
			
			@Override
			public void renderBackground(GuiGraphics graphics, int leftPos, int topPos)
			{
				Rectangle box = this.getBox(leftPos, topPos);
				
				int textureX = box.x() - leftPos - box.w();
				graphics.blit(MachineScreen.BACKGROUND, box.x(), box.y(), textureX, 0, box.w(), box.h() - 4);
				graphics.blit(MachineScreen.BACKGROUND, box.x(), box.y() + box.h() - 4, textureX, 252, box.w(), 4);
			}
			
			@Override
			public void renderTooltip(MachineScreen screen, Font font, GuiGraphics graphics, int x, int y, int cursorX, int cursorY)
			{
				if(screen.getFocusedSlot() instanceof SlotTooltip st && !screen.getFocusedSlot().hasItem())
				{
					graphics.renderTooltip(font, st.getTooltip(), cursorX, cursorY);
				}
			}
		};
	}
	
	interface SlotTooltip
	{
		Component getTooltip();
	}
	
	private static class SlotWithBackground extends Slot implements BackgroundRenderedSlot
	{
		public SlotWithBackground(Container container, int index, int x, int y)
		{
			super(container, index, x, y);
		}
	}
}
