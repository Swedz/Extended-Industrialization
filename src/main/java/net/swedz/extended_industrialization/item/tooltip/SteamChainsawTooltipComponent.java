package net.swedz.extended_industrialization.item.tooltip;

import aztech.modern_industrialization.machines.gui.MachineScreen;
import aztech.modern_industrialization.machines.guicomponents.ProgressBar;
import aztech.modern_industrialization.machines.guicomponents.ProgressBarClient;
import aztech.modern_industrialization.util.RenderHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.swedz.extended_industrialization.item.SteamChainsawItem;

public final class SteamChainsawTooltipComponent implements ClientTooltipComponent
{
	private final SteamChainsawItem.SteamChainsawTooltipData data;
	
	public SteamChainsawTooltipComponent(SteamChainsawItem.SteamChainsawTooltipData data)
	{
		this.data = data;
	}
	
	@Override
	public int getHeight()
	{
		return 20;
	}
	
	@Override
	public int getWidth(Font textRenderer)
	{
		return 40;
	}
	
	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics)
	{
		guiGraphics.blit(MachineScreen.SLOT_ATLAS, x, y, 0, 0, 18, 18, 256, 256);
		
		RenderHelper.renderAndDecorateItem(guiGraphics, font, data.variant().toStack((int) data.amount()), x + 1, y + 1);
		
		ProgressBar.Parameters progressParams = new ProgressBar.Parameters(0, 0, "furnace", true);
		ProgressBarClient.renderProgress(guiGraphics, x + 20, y, progressParams, (float) data.burnTicks() / data.maxBurnTicks());
	}
}