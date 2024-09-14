package net.swedz.extended_industrialization.proxy.accessories;

import net.minecraft.world.entity.player.Player;
import net.swedz.tesseract.neoforge.proxy.Proxy;
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint;

@ProxyEntrypoint
public class EIAccessoriesProxy implements Proxy
{
	public boolean isLoaded()
	{
		return false;
	}
	
	public long chargeAccessories(Player player, long maxAmount)
	{
		return 0;
	}
}
