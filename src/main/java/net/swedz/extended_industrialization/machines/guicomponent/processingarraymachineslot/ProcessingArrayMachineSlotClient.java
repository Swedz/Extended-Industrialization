package net.swedz.extended_industrialization.machines.guicomponent.processingarraymachineslot;

import aztech.modern_industrialization.client.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.client.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.client.machines.gui.MachineScreen;
import aztech.modern_industrialization.inventory.BackgroundRenderedSlot;
import aztech.modern_industrialization.inventory.SlotGroup;
import aztech.modern_industrialization.machines.gui.GuiComponent;
import aztech.modern_industrialization.util.Rectangle;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;

import java.util.List;

public final class ProcessingArrayMachineSlotClient extends GuiComponentClient<Unit, Integer>
{
	public ProcessingArrayMachineSlotClient(Unit params, Integer data)
	{
		super(params, data);
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
				return data;
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
				return EI.text().processingArrayMachineInput();
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
				var box = this.getBox(leftPos, topPos);
				
				int textureX = box.x() - leftPos - box.w();
				graphics.blit(MachineScreen.BACKGROUND, box.x(), box.y(), textureX, 0, box.w(), box.h() - 4);
				graphics.blit(MachineScreen.BACKGROUND, box.x(), box.y() + box.h() - 4, textureX, 252, box.w(), 4);
			}
			
			@Override
			public boolean renderTooltip(MachineScreen screen, Font font, GuiGraphics graphics, int x, int y, int cursorX, int cursorY)
			{
				if(screen.getFocusedSlot() instanceof SlotTooltip slotTooltip && !screen.getFocusedSlot().hasItem())
				{
					graphics.renderTooltip(font, slotTooltip.getTooltip(), cursorX, cursorY);
					return true;
				}
				return false;
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
