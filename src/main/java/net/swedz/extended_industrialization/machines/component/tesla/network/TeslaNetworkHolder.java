package net.swedz.extended_industrialization.machines.component.tesla.network;

/**
 * Injected into {@link net.minecraft.server.MinecraftServer}
 */
public interface TeslaNetworkHolder
{
	default TeslaNetworkCache getTeslaNetworks()
	{
		throw new UnsupportedOperationException("getTeslaNetworks() must be implemented");
	}
}
