package net.swedz.extended_industrialization.machines.guicomponent.modularslots;

import aztech.modern_industrialization.inventory.BackgroundRenderedSlot;
import aztech.modern_industrialization.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.machines.gui.GuiComponent;
import aztech.modern_industrialization.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.machines.gui.MachineScreen;
import aztech.modern_industrialization.util.Rectangle;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public final class ModularSlotPanelClient implements GuiComponentClient
{
	private final int offsetY;
	
	private final List<ModularSlotPanel.Slot> slots       = Lists.newArrayList();
	private final List<Integer>               stackLimits = Lists.newArrayList();
	
	public ModularSlotPanelClient(RegistryFriendlyByteBuf buf)
	{
		offsetY = buf.readVarInt();
		
		int count = buf.readVarInt();
		for(int i = 0; i < count; i++)
		{
			slots.add(ModularSlotPanel.getSlot(buf.readResourceLocation()));
		}
		
		this.readCurrentData(buf);
	}
	
	@Override
	public void readCurrentData(RegistryFriendlyByteBuf buf)
	{
		stackLimits.clear();
		for(int i = 0; i < slots.size(); i++)
		{
			stackLimits.add(buf.readVarInt());
		}
	}
	
	@Override
	public void setupMenu(GuiComponent.MenuFacade menu)
	{
		for(int i = 0; i < slots.size(); i++)
		{
			int slotIndex = i;
			ModularSlotPanel.Slot slot = slots.get(slotIndex);
			Supplier<Integer> stackLimit = () -> stackLimits.get(slotIndex);
			
			class ClientSlot extends SlotWithBackground implements SlotTooltip
			{
				public ClientSlot()
				{
					super(new SimpleContainer(1), 0, ModularSlotPanel.getSlotX(menu.getGuiParams()), ModularSlotPanel.getSlotY(slotIndex) + offsetY);
				}
				
				@Override
				public boolean mayPlace(ItemStack stack)
				{
					return slot.insertionChecker().test(stack);
				}
				
				@Override
				public int getMaxStackSize()
				{
					return stackLimit.get();
				}
				
				@Override
				public int getBackgroundU()
				{
					return this.hasItem() ? 0 : slot.u();
				}
				
				@Override
				public int getBackgroundV()
				{
					return this.hasItem() ? 0 : slot.v();
				}
				
				@Override
				public Component getTooltip()
				{
					return slot.tooltip().get();
				}
			}
			menu.addSlotToMenu(new ClientSlot(), slot.group());
		}
	}
	
	@Override
	public ClientComponentRenderer createRenderer(MachineScreen machineScreen)
	{
		return new ClientComponentRenderer()
		{
			private Rectangle getBox(int leftPos, int topPos)
			{
				return new Rectangle(leftPos + machineScreen.getGuiParams().backgroundWidth, topPos + 10 + offsetY, 31, 14 + (slots.size() * 20));
			}
			
			@Override
			public void addExtraBoxes(List<Rectangle> rectangles, int leftPos, int topPos)
			{
				rectangles.add(this.getBox(leftPos, topPos));
			}
			
			@Override
			public void renderBackground(GuiGraphics graphics, int x, int y)
			{
				Rectangle box = this.getBox(x, y);
				int textureX = box.x() - x - box.w();
				graphics.blit(MachineScreen.BACKGROUND, box.x(), box.y(), textureX, 0, box.w(), box.h() - 4);
				graphics.blit(MachineScreen.BACKGROUND, box.x(), box.y() + box.h() - 4, textureX, 252, box.w(), 4);
			}
			
			@Override
			public void renderTooltip(MachineScreen screen, Font font, GuiGraphics graphics, int x, int y, int cursorX, int cursorY)
			{
				Slot slot = screen.getFocusedSlot();
				if(slot instanceof SlotTooltip tooltip)
				{
					if(!screen.getFocusedSlot().hasItem())
					{
						graphics.renderTooltip(font, tooltip.getTooltip(), cursorX, cursorY);
					}
				}
			}
		};
	}
	
	interface SlotTooltip
	{
		Component getTooltip();
	}
	
	public static class SlotWithBackground extends Slot implements BackgroundRenderedSlot
	{
		public SlotWithBackground(Container container, int index, int x, int y)
		{
			super(container, index, x, y);
		}
	}
}
