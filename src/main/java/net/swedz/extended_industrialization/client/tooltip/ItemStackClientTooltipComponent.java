package net.swedz.extended_industrialization.client.tooltip;

import aztech.modern_industrialization.util.RenderHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.swedz.extended_industrialization.api.ItemStackTooltipComponent;

public record ItemStackClientTooltipComponent(ItemStackTooltipComponent component) implements ClientTooltipComponent
{
	@Override
	public int getHeight()
	{
		return 20;
	}
	
	@Override
	public int getWidth(Font font)
	{
		return 18;
	}
	
	@Override
	public void renderImage(Font font, int mouseX, int mouseY, GuiGraphics graphics)
	{
		RenderHelper.renderAndDecorateItem(graphics, font, component.stack(), mouseX, mouseY);
	}
}
