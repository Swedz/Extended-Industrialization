package net.swedz.extended_industrialization.mixin.client.mi;

import aztech.modern_industrialization.client.screen.MIHandledScreen;
import aztech.modern_industrialization.machines.gui.MachineMenuClient;
import aztech.modern_industrialization.machines.gui.MachineScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.swedz.extended_industrialization.EIConfig;
import net.swedz.extended_industrialization.machines.guicomponents.exposecabletier.ExposeCableTierGuiClient;
import net.swedz.extended_industrialization.EIText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MachineScreen.class)
public abstract class MachineScreenIncludeCableTierInTitleMixin extends MIHandledScreen<MachineMenuClient>
{
	public MachineScreenIncludeCableTierInTitleMixin(MachineMenuClient handler, Inventory inventory, Component title)
	{
		super(handler, inventory, title);
	}
	
	@Unique
	private Component getTieredTitle()
	{
		if(EIConfig.displayMachineVoltage)
		{
			ExposeCableTierGuiClient exposeCableTier = menu.getComponent(ExposeCableTierGuiClient.class);
			if(exposeCableTier != null)
			{
				return EIText.MACHINE_MENU_VOLTAGE_PREFIX.text(Component.translatable(exposeCableTier.getCableTier().shortEnglishKey())).append(title);
			}
		}
		return title;
	}
	
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
		graphics.drawString(font, this.getTieredTitle(), titleLabelX, titleLabelY, 4210752, false);
		graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 4210752, false);
	}
}
