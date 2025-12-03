package net.swedz.extended_industrialization.client;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.config.MIClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;
import net.swedz.extended_industrialization.item.nanosuit.ability.NanoSuitGravichestplateAbility;

public final class NanoGravichestplateHudRenderer
{
	public static void render(GuiGraphics graphics, DeltaTracker deltaTracker)
	{
		var mc = Minecraft.getInstance();
		var player = mc.player;
		if(player != null)
		{
			var chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
			if(chestplate.getItem() instanceof NanoSuitArmorItem item &&
			   item.hasAbility(NanoSuitGravichestplateAbility.class))
			{
				graphics.pose().pushPose();
				graphics.pose().translate(0, MIClientConfig.INSTANCE.armorHudYPosition.getAsInt(), 0);
				
				var activeComponent = item.isActivated(chestplate) ?
						MIText.GravichestplateEnabled.text().withStyle(ChatFormatting.GREEN) :
						MIText.GravichestplateDisabled.text().withStyle(ChatFormatting.RED);
				graphics.drawString(mc.font, activeComponent, 4, 0, 0xF9FFFE);
				
				Component chargeComponent = MIText.EnergyFill.text(item.getStoredEnergy(chestplate) * 100 / item.getEnergyCapacity(chestplate));
				graphics.drawString(mc.font, chargeComponent, 4, 10, 0xF9FFFE);
				
				graphics.pose().popPose();
			}
		}
	}
}
