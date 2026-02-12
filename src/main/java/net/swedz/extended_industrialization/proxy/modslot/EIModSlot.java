package net.swedz.extended_industrialization.proxy.modslot;

import com.google.common.collect.Lists;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.proxy.modslot.accessories.EIModSlotAccessoriesProxy;
import net.swedz.extended_industrialization.proxy.modslot.curios.EIModSlotCuriosProxy;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.List;
import java.util.function.Predicate;

public final class EIModSlot
{
	public static List<ItemStack> getContents(Player player, Predicate<ItemStack> filter)
	{
		List<ItemStack> contents = Lists.newArrayList();
		
		contents.addAll(Proxies.get(EIModSlotCuriosProxy.class).getContents(player, filter));
		contents.addAll(Proxies.get(EIModSlotAccessoriesProxy.class).getContents(player, filter));
		
		return contents;
	}
}
