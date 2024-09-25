package net.swedz.extended_industrialization.machines.component.tesla;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Map;

public final class TeslaNetworks
{
	private final Map<TeslaNetworkKey, TeslaNetwork> networks = Maps.newHashMap();
	
	public TeslaNetwork get(TeslaNetworkKey key)
	{
		return networks.computeIfAbsent(key, TeslaNetwork::new);
	}
	
	public TeslaNetwork get(Level level, BlockPos pos)
	{
		return this.get(new TeslaNetworkKey(level, pos));
	}
	
	public void forget(TeslaNetwork network)
	{
		networks.remove(network.key());
	}
}
