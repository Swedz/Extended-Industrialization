package net.swedz.extended_industrialization.mixin;

import net.minecraft.server.MinecraftServer;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkHolder;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworks;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MinecraftServer.class)
@Implements(@Interface(iface = TeslaNetworkHolder.class, prefix = "teslaNetwork$"))
public class TeslaNetworkHolderMinecraftServerMixin
{
	@Unique
	private final TeslaNetworks teslaNetworks = new TeslaNetworks();
	
	public TeslaNetworks teslaNetwork$getTeslaNetworks()
	{
		return teslaNetworks;
	}
}
