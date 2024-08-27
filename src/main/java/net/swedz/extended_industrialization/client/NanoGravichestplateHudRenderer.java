package net.swedz.extended_industrialization.client;

import aztech.modern_industrialization.MIText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitGravichestplateAbility;

public final class NanoGravichestplateHudRenderer
{
	public static void render(GuiGraphics graphics, DeltaTracker deltaTracker)
	{
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if(player != null)
		{
			ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
			if(chestplate.getItem() instanceof NanoSuitArmorItem item &&
			   item.hasAbility(NanoSuitGravichestplateAbility.class))
			{
				Component activeComponent = item.isActivated(chestplate) ?
						MIText.GravichestplateEnabled.text().withStyle(ChatFormatting.GREEN) :
						MIText.GravichestplateDisabled.text().withStyle(ChatFormatting.RED);
				graphics.drawString(mc.font, activeComponent, 4, 4, 0xF9FFFE);
				
				Component chargeComponent = MIText.EnergyFill.text(item.getStoredEnergy(chestplate) * 100 / item.getEnergyCapacity(chestplate));
				graphics.drawString(mc.font, chargeComponent, 4, 14, 0xF9FFFE);
			}
		}
	}
}
