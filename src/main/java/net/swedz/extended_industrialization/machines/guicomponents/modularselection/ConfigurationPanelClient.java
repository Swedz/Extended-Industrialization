package net.swedz.extended_industrialization.machines.guicomponents.modularselection;

import aztech.modern_industrialization.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.machines.gui.MachineScreen;
import aztech.modern_industrialization.util.Rectangle;
import aztech.modern_industrialization.util.TextHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.swedz.extended_industrialization.network.packets.ConfigureMachinePacket;
import net.swedz.extended_industrialization.text.EIText;

import java.util.ArrayList;
import java.util.List;

/**
 * This was stolen from {@link aztech.modern_industrialization.machines.guicomponents.ShapeSelectionClient} to make my own generic "configuration panel" component to be used for non-shape related configuring of machines.
 */
public final class ConfigurationPanelClient implements GuiComponentClient
{
	private final ConfigurationPanel.LineInfo[] lines;
	final         int[]                         currentData;
	private       Renderer                      renderer;
	
	public ConfigurationPanelClient(FriendlyByteBuf buf)
	{
		lines = new ConfigurationPanel.LineInfo[buf.readVarInt()];
		for(int i = 0; i < lines.length; ++i)
		{
			int numValues = buf.readVarInt();
			List<Component> components = new ArrayList<>();
			for(int j = 0; j < numValues; ++j)
			{
				components.add(buf.readComponent());
			}
			lines[i] = new ConfigurationPanel.LineInfo(numValues, components, buf.readBoolean());
		}
		currentData = new int[lines.length];
		
		readCurrentData(buf);
	}
	
	@Override
	public void readCurrentData(FriendlyByteBuf buf)
	{
		for(int i = 0; i < currentData.length; ++i)
		{
			currentData[i] = buf.readVarInt();
		}
	}
	
	@Override
	public ClientComponentRenderer createRenderer(MachineScreen machineScreen)
	{
		// Compute the max width of all the components!
		int maxWidth = 1;
		for(ConfigurationPanel.LineInfo line : lines)
		{
			for(Component tooltip : line.translations())
			{
				maxWidth = Math.max(maxWidth, Minecraft.getInstance().font.width(tooltip));
			}
		}
		
		return renderer = new Renderer(maxWidth);
	}
	
	Renderer getRenderer()
	{
		return renderer;
	}
	
	class Renderer implements ClientComponentRenderer
	{
		boolean isPanelOpen = false;
		private final int btnSize      = 12;
		private final int borderSize   = 3;
		private final int outerPadding = 5;
		private final int innerPadding = 5;
		
		private final int textMaxWidth;
		private final int panelWidth;
		
		private Renderer(int textMaxWidth)
		{
			this.textMaxWidth = textMaxWidth;
			this.panelWidth = borderSize + outerPadding + btnSize + innerPadding + textMaxWidth + innerPadding + btnSize + outerPadding;
		}
		
		private static int getVerticalPos(int lineId)
		{
			return 46 + 16 * lineId;
		}
		
		@Override
		public void addButtons(ButtonContainer container)
		{
			// Two buttons per line
			for(int i = 0; i < lines.length; ++i)
			{
				int iCopy = i;
				ConfigurationPanel.LineInfo line = lines[i];
				int baseU = line.useArrows() ? 174 : 150;
				int v = 58;
				
				// Left button
				container.addButton(
						-panelWidth + borderSize + outerPadding, getVerticalPos(i), btnSize, btnSize,
						(syncId) -> new ConfigureMachinePacket(syncId, iCopy, true).sendToServer(),
						List::of,
						(screen, button, guiGraphics, mouseX, mouseY, delta) ->
						{
							if(currentData[iCopy] == 0)
							{
								screen.blitButtonNoHighlight(button, guiGraphics, baseU, v + 12);
							}
							else
							{
								screen.blitButtonSmall(button, guiGraphics, baseU, v);
							}
						},
						() -> isPanelOpen
				);
				
				// Right button
				container.addButton(
						-btnSize - outerPadding, getVerticalPos(i), btnSize, btnSize,
						(syncId) -> new ConfigureMachinePacket(syncId, iCopy, false).sendToServer(),
						List::of,
						(screen, button, guiGraphics, mouseX, mouseY, delta) ->
						{
							if(currentData[iCopy] == line.numValues() - 1)
							{
								screen.blitButtonNoHighlight(button, guiGraphics, baseU + 12, v + 12);
							}
							else
							{
								screen.blitButtonSmall(button, guiGraphics, baseU + 12, v);
							}
						},
						() -> isPanelOpen
				);
			}
			
			// Big button to open panel
			container.addButton(
					-24, 17, 20, 20,
					(syncId) -> isPanelOpen = !isPanelOpen,
					() -> List.of(EIText.CONFIGURATION_PANEL_TITLE.text(), EIText.CONFIGURATION_PANEL_DESCRIPTION.text().setStyle(TextHelper.GRAY_TEXT)),
					(screen, button, guiGraphics, mouseX, mouseY, delta) -> screen.blitButton(button, guiGraphics, 138, 38)
			);
		}
		
		@Override
		public void renderBackground(GuiGraphics guiGraphics, int leftPos, int topPos)
		{
			Rectangle box = this.getBox(leftPos, topPos);
			
			guiGraphics.blit(MachineScreen.BACKGROUND, box.x(), box.y(), 0, 0, box.w(), box.h() - 4);
			guiGraphics.blit(MachineScreen.BACKGROUND, box.x(), box.y() + box.h() - 4, 0, 252, box.w(), 4);
			
			if(isPanelOpen)
			{
				RenderSystem.disableDepthTest();
				for(int i = 0; i < lines.length; ++i)
				{
					ConfigurationPanel.LineInfo line = lines[i];
					Component tooltip = line.translations().get(currentData[i]);
					int width = Minecraft.getInstance().font.width(tooltip);
					guiGraphics.drawString(
							Minecraft.getInstance().font, tooltip,
							box.x() + borderSize + outerPadding + btnSize + innerPadding + (textMaxWidth - width) / 2,
							topPos + getVerticalPos(i) + 2, 0x404040, false
					);
				}
				RenderSystem.enableDepthTest();
			}
		}
		
		public Rectangle getBox(int leftPos, int topPos)
		{
			if(isPanelOpen)
			{
				int topOffset = 10;
				return new Rectangle(leftPos - panelWidth, topPos + topOffset, panelWidth,
						getVerticalPos(lines.length - 1) - topOffset + btnSize + outerPadding + borderSize
				);
			}
			else
			{
				return new Rectangle(leftPos - 31, topPos + 10, 31, 34);
			}
		}
		
		@Override
		public void addExtraBoxes(List<Rectangle> rectangles, int leftPos, int topPos)
		{
			rectangles.add(this.getBox(leftPos, topPos));
		}
	}
}
