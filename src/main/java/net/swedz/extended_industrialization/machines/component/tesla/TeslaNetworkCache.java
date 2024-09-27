package net.swedz.extended_industrialization.machines.component.tesla;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.swedz.extended_industrialization.api.WorldPos;

import java.util.Map;

public final class TeslaNetworkCache
{
	private final Map<WorldPos, TeslaNetwork> networks = Maps.newHashMap();
	
	public TeslaNetwork get(WorldPos key)
	{
		return networks.computeIfAbsent(key, (k) -> new TeslaNetwork(this, key));
	}
	
	public TeslaNetwork get(Level level, BlockPos pos)
	{
		return this.get(new WorldPos(level, pos));
	}
	
	void forget(TeslaNetwork network)
	{
		networks.remove(network.key());
	}
}
