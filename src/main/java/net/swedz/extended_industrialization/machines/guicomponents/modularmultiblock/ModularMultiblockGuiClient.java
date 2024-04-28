package net.swedz.extended_industrialization.machines.guicomponents.modularmultiblock;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.machines.gui.MachineScreen;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class ModularMultiblockGuiClient implements GuiComponentClient
{
	private List<ModularMultiblockGuiLine> text;
	
	public ModularMultiblockGuiClient(FriendlyByteBuf buf)
	{
		this.readCurrentData(buf);
	}
	
	@Override
	public void readCurrentData(FriendlyByteBuf buf)
	{
		text = buf.readCollection(Lists::newArrayListWithCapacity, ModularMultiblockGuiLine::read);
	}
	
	@Override
	public ClientComponentRenderer createRenderer(MachineScreen machineScreen)
	{
		return new Renderer();
	}
	
	public final class Renderer implements ClientComponentRenderer
	{
		private static final ResourceLocation TEXTURE = MI.id("textures/gui/container/multiblock_info.png");
		
		@Override
		public void renderBackground(GuiGraphics graphics, int x, int y)
		{
			Minecraft minecraftClient = Minecraft.getInstance();
			graphics.blit(
					TEXTURE,
					x + ModularMultiblockGui.X, y + ModularMultiblockGui.Y, 0, 0,
					ModularMultiblockGui.W, ModularMultiblockGui.H, ModularMultiblockGui.W, ModularMultiblockGui.H
			);
			Font font = minecraftClient.font;
			
			int deltaY = 23;
			for(ModularMultiblockGuiLine line : text)
			{
				graphics.drawString(font, line.text(), x + 9, y + deltaY, line.color(), false);
				deltaY += 11;
			}
		}
	}
}
