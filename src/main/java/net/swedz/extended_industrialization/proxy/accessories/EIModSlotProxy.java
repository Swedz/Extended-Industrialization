package net.swedz.extended_industrialization.proxy.accessories;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.tesseract.neoforge.proxy.Proxy;
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint;

import java.util.List;
import java.util.function.Predicate;

@ProxyEntrypoint
public class EIModSlotProxy implements Proxy
{
	public boolean isLoaded()
	{
		return false;
	}
	
	public List<ItemStack> getContents(Player player, Predicate<ItemStack> filter)
	{
		return List.of();
	}
}
