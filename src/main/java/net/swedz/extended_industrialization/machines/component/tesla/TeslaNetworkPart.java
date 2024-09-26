package net.swedz.extended_industrialization.machines.component.tesla;

import aztech.modern_industrialization.api.energy.CableTier;
import net.minecraft.core.BlockPos;
import net.swedz.tesseract.neoforge.proxy.Proxies;
import net.swedz.tesseract.neoforge.proxy.builtin.TesseractProxy;

public interface TeslaNetworkPart
{
	boolean hasNetwork();
	
	TeslaNetworkKey getNetworkKey();
	
	void setNetwork(TeslaNetworkKey key);
	
	default TeslaNetwork getNetwork()
	{
		if(!this.hasNetwork())
		{
			throw new IllegalStateException("No network has been set");
		}
		TesseractProxy proxy = Proxies.get(TesseractProxy.class);
		if(!proxy.hasServer())
		{
			throw new IllegalStateException("Cannot get network without a server");
		}
		return proxy.getServer().getTeslaNetworks().get(this.getNetworkKey());
	}
	
	BlockPos getPosition();
	
	CableTier getCableTier();
}
