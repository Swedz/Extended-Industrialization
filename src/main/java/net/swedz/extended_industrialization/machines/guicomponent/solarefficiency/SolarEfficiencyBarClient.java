package net.swedz.extended_industrialization.machines.guicomponent.solarefficiency;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.client.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.client.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.client.machines.gui.MachineScreen;
import aztech.modern_industrialization.client.util.RenderHelper;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;

import java.util.List;
import java.util.Optional;

public final class SolarEfficiencyBarClient extends GuiComponentClient<SolarEfficiencyBar.Params, SolarEfficiencyBar.Data>
{
	public SolarEfficiencyBarClient(SolarEfficiencyBar.Params params, SolarEfficiencyBar.Data data)
	{
		super(params, data);
	}
	
	@Override
	public ClientComponentRenderer createRenderer(MachineScreen machineScreen)
	{
		return new Renderer();
	}
	
	private final class Renderer implements ClientComponentRenderer
	{
		private static final ResourceLocation EFFICIENCY_BAR = MI.id("textures/gui/efficiency_bar.png");
		private static final ResourceLocation SOLAR_STATE    = EI.id("textures/gui/container/solar_state.png");
		
		private final int WIDTH = 100, HEIGHT = 2;
		
		@Override
		public void renderBackground(GuiGraphics guiGraphics, int x, int y)
		{
			guiGraphics.blit(
					EFFICIENCY_BAR,
					x + params.renderX() - 1, y + params.renderY() - 1,
					0, 2, WIDTH + 2, HEIGHT + 2, 102, 6
			);
			int barPixels = (int) ((float) data.efficiency() / 100 * WIDTH);
			guiGraphics.blit(
					EFFICIENCY_BAR,
					x + params.renderX(), y + params.renderY(),
					0, 0, barPixels, HEIGHT, 102, 6
			);
			guiGraphics.blit(SOLAR_STATE,
					x + params.renderX() - 20, y + params.renderY() + HEIGHT / 2 - 6,
					data.working() ? 0 : 12, 0, 12, 12, 24, 12
			);
		}
		
		@Override
		public void renderTooltip(MachineScreen screen, Font font, GuiGraphics guiGraphics, int x, int y, int cursorX, int cursorY)
		{
			if(RenderHelper.isPointWithinRectangle(params.renderX(), params.renderY(), WIDTH, HEIGHT, cursorX - x, cursorY - y))
			{
				List<Component> lines = Lists.newArrayList();
				lines.add(EI.text().solarEfficiency(data.efficiency()));
				if(data.calcification() > 0)
				{
					lines.add(EI.text().calcificationPercentage(data.calcification()));
				}
				if(data.energyProduced() > 0)
				{
					lines.add(EI.text().generatingEuPerTick(data.energyProduced()));
				}
				guiGraphics.renderTooltip(font, lines, Optional.empty(), cursorX, cursorY);
			}
		}
	}
}
