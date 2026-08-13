package net.swedz.extended_industrialization.machines.guicomponent.beacon;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.client.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.client.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.client.machines.gui.MachineScreen;
import aztech.modern_industrialization.client.util.RenderHelper;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.swedz.extended_industrialization.EI;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGuiLine;
import net.swedz.tesseract.neoforge.compat.mi.helper.MultiblockInfoBackground;

import java.util.List;
import java.util.Optional;

public final class BeaconEffectsViewClient extends GuiComponentClient<Unit, BeaconEffectsView.Data>
{
	public BeaconEffectsViewClient(Unit params, BeaconEffectsView.Data data)
	{
		super(params, data);
	}
	
	@Override
	public ClientComponentRenderer createRenderer(MachineScreen screen)
	{
		return new Renderer();
	}
	
	private final class Renderer implements ClientComponentRenderer
	{
		private static final ResourceLocation ICON_BACKGROUND = ResourceLocation.withDefaultNamespace("container/beacon/button");
		private static final ResourceLocation BAR_TEXTURE     = EI.id("textures/gui/container/beacon_remaining_time_bar.png");
		
		private static final int ICONS_X       = 5;
		private static final int ICONS_Y       = 24;
		private static final int ICONS_PER_ROW = 6;
		private static final int ICON_ROWS     = 1;
		private static final int ICONS_WIDTH   = 166;
		private static final int ICONS_HEIGHT  = 24 * ICON_ROWS - 2;
		
		private static final int TIME_X      = ICONS_X + 33;
		private static final int TIME_Y      = ICONS_Y + ICONS_HEIGHT + 13;
		private static final int TIME_WIDTH  = 100;
		private static final int TIME_HEIGHT = 2;
		
		private int[] getIconPosition(int index, int total)
		{
			int midX = ICONS_WIDTH / 2;
			int midY = ICONS_HEIGHT / 2;
			
			int totalWidth = Math.min(total, ICONS_PER_ROW) * 24 - 2;
			int totalHeight = (int) Math.ceil(total / (double) ICONS_PER_ROW) * 24 - 2;
			
			int x = ((index % ICONS_PER_ROW) * 24) + midX - (totalWidth / 2);
			int y = ((index / ICONS_PER_ROW) * 24) + midY - (totalHeight / 2);
			
			return new int[]{x, y};
		}
		
		@Override
		public void renderBackground(GuiGraphics graphics, int left, int top)
		{
			MultiblockInfoBackground.renderBackground(graphics, left, top, ICONS_HEIGHT + 16);
			if(data.effects().isEmpty())
			{
				List<ModularMultiblockGuiLine> lines = Lists.newArrayList();
				if(!data.shapeValid())
				{
					lines.add(new ModularMultiblockGuiLine(MIText.MultiblockShapeInvalid.text(), ModularMultiblockGuiLine.RED));
				}
				else if(!data.beamActive())
				{
					lines.add(new ModularMultiblockGuiLine(EI.text().electricBeaconBeamObstructed(), ModularMultiblockGuiLine.RED));
				}
				MultiblockInfoBackground.renderText(graphics, left, top, lines);
			}
			
			this.renderEffects(graphics, left, top);
			this.renderRemainingTimeBar(graphics, left, top);
		}
		
		private void renderEffects(GuiGraphics graphics, int left, int top)
		{
			int x = left + ICONS_X;
			int y = top + ICONS_Y;
			
			var effects = data.effects();
			for(int index = 0; index < effects.size() && index < ICONS_PER_ROW * ICON_ROWS; index++)
			{
				var effect = effects.get(index);
				
				int[] iconPosition = getIconPosition(index, effects.size());
				int iconX = iconPosition[0] + x;
				int iconY = iconPosition[1] + y;
				
				graphics.blitSprite(ICON_BACKGROUND, iconX, iconY, 22, 22);
				graphics.blit(iconX + 2, iconY + 2, 0, 18, 18, Minecraft.getInstance().getMobEffectTextures().get(effect.effect()));
				
				if(effect.amplifier() >= 1 && effect.amplifier() <= 9)
				{
					var font = Minecraft.getInstance().font;
					var amplifierText = Component.translatable("enchantment.level." + (effect.amplifier() + 1));
					graphics.drawString(font, amplifierText, iconX + 22 - font.width(amplifierText) - 2, iconY + 22 - font.lineHeight - 1, 0xFFFFFF);
				}
			}
		}
		
		private void renderRemainingTimeBar(GuiGraphics graphics, int left, int top)
		{
			int x = left + TIME_X;
			int y = top + TIME_Y;
			
			graphics.blit(BAR_TEXTURE, x - 1, y - 1, 0, 2, TIME_WIDTH + 2, TIME_HEIGHT + 2, 102, 6);
			
			int barPixels = (int) ((float) data.remainingTicks() / data.totalTicks() * TIME_WIDTH);
			graphics.blit(BAR_TEXTURE, x, y, 0, 0, barPixels, TIME_HEIGHT, 102, 6);
		}
		
		@Override
		public boolean renderTooltip(MachineScreen screen, Font font, GuiGraphics graphics, int left, int top, int cursorX, int cursorY)
		{
			return this.renderEffectTooltip(screen, font, graphics, left, top, cursorX, cursorY) ||
				   this.renderRemainingTimeBarTooltip(screen, font, graphics, left, top, cursorX, cursorY);
		}
		
		private boolean renderEffectTooltip(MachineScreen screen, Font font, GuiGraphics graphics, int left, int top, int cursorX, int cursorY)
		{
			var effects = data.effects();
			int index = 0;
			for(var effect : effects)
			{
				int[] iconPosition = getIconPosition(index, effects.size());
				int iconX = iconPosition[0] + ICONS_X + 1;
				int iconY = iconPosition[1] + ICONS_Y + 1;
				if(RenderHelper.isPointWithinRectangle(iconX, iconY, 20, 20, cursorX - left, cursorY - top))
				{
					var effectText = effect.effect().value().getDisplayName().copy();
					if(effect.amplifier() >= 1 && effect.amplifier() <= 9)
					{
						effectText.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + (effect.amplifier() + 1)));
					}
					graphics.renderTooltip(font, effectText, cursorX, cursorY);
					return true;
				}
				index++;
			}
			return false;
		}
		
		private boolean renderRemainingTimeBarTooltip(MachineScreen screen, Font font, GuiGraphics graphics, int left, int top, int cursorX, int cursorY)
		{
			if(RenderHelper.isPointWithinRectangle(TIME_X, TIME_Y, TIME_WIDTH, TIME_HEIGHT, cursorX - left, cursorY - top))
			{
				List<Component> lines = Lists.newArrayList();
				lines.add(EI.text().electricBeaconRemainingTimeTooltip(data.remainingTicks()));
				lines.add(EI.text().electricBeaconEuCostTooltip(data.euCost()));
				graphics.renderTooltip(font, lines, Optional.empty(), cursorX, cursorY);
				return true;
			}
			return false;
		}
	}
}
