package net.swedz.extended_industrialization.mixin;

import net.minecraft.server.MinecraftServer;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkHolder;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkCache;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MinecraftServer.class)
@Implements(@Interface(iface = TeslaNetworkHolder.class, prefix = "teslaNetwork$"))
public class TeslaNetworkHolderMinecraftServerMixin
{
	@Unique
	private final TeslaNetworkCache teslaNetworks = new TeslaNetworkCache();
	
	public TeslaNetworkCache teslaNetwork$getTeslaNetworks()
	{
		return teslaNetworks;
	}
}
