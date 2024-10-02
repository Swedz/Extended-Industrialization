package net.swedz.extended_industrialization.machines.guicomponent.universaltransformer;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.machines.component.TransformerTierComponent;

import java.util.List;

import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.*;

public final class UniversalTransformerSlotsClient implements GuiComponentClient
{
	public UniversalTransformerSlotsClient(RegistryFriendlyByteBuf buf)
	{
	}
	
	@Override
	public void readCurrentData(RegistryFriendlyByteBuf buf)
	{
	}
	
	@Override
	public void setupMenu(GuiComponent.MenuFacade menu)
	{
		class ClientSlot extends SlotWithBackground implements SlotTooltip
		{
			private final EIText tooltip;
			
			public ClientSlot(EIText tooltip, int index)
			{
				super(new SimpleContainer(1), 0, UniversalTransformerSlots.getSlotX(), UniversalTransformerSlots.getSlotY(index));
				this.tooltip = tooltip;
			}
			
			@Override
			public boolean mayPlace(ItemStack itemStack)
			{
				return TransformerTierComponent.getTierFromCasing(itemStack) != null;
			}
			
			@Override
			public int getMaxStackSize()
			{
				return 1;
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
				return line(tooltip);
			}
		}
		
		menu.addSlotToMenu(new ClientSlot(EIText.UNIVERSAL_TRANSFORMER_FROM_TIER_INPUT, 0), SlotGroup.CONFIGURABLE_STACKS);
		menu.addSlotToMenu(new ClientSlot(EIText.UNIVERSAL_TRANSFORMER_TO_TIER_INPUT, 1), SlotGroup.CONFIGURABLE_STACKS);
	}
	
	@Override
	public ClientComponentRenderer createRenderer(MachineScreen machineScreen)
	{
		return new ClientComponentRenderer()
		{
			private final ResourceLocation TRANSFORMER_DIRECTION = EI.id("textures/gui/container/universal_transformer_direction.png");
			
			private Rectangle getBox(int leftPos, int topPos)
			{
				return new Rectangle(leftPos - 31, topPos + 10, 31, 16 + 18 * 3);
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
				
				int textureX = leftPos - box.x() - box.w();
				graphics.blit(MachineScreen.BACKGROUND, box.x(), box.y(), textureX, 0, box.w(), box.h() - 4);
				graphics.blit(MachineScreen.BACKGROUND, box.x(), box.y() + box.h() - 4, textureX, 252, box.w(), 4);
				
				graphics.blit(TRANSFORMER_DIRECTION, leftPos - 22, topPos + 37, 0, 0, 16, 16, 16, 16);
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
