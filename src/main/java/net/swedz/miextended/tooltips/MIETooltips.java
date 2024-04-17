package net.swedz.miextended.tooltips;

import aztech.modern_industrialization.MI;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.swedz.miextended.text.MIEText;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public final class MIETooltips
{
	private static final List<String> EXTENDED_ITEM_IDS = Lists.newArrayList();
	
	public static void addExtendedItem(String id)
	{
		EXTENDED_ITEM_IDS.add(id);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onItemTooltip(ItemTooltipEvent event)
	{
		ResourceLocation key = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
		String itemId = key.getPath();
		if(key.getNamespace().equals(MI.ID) && EXTENDED_ITEM_IDS.contains(itemId))
		{
			event.getToolTip().add(MIEText.ADDED_BY_MIE.text().withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.BLUE));
		}
	}
}
