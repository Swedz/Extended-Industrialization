package net.swedz.extended_industrialization.machines.component.farmer.harvesting;

import net.neoforged.bus.api.Event;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerBlockMap;
import net.swedz.tesseract.neoforge.behavior.BehaviorHolder;

import java.util.List;

public final class FarmerHarvestableBehaviorHolder extends BehaviorHolder<HarvestableBehavior, HarvestingContext>
{
	public FarmerHarvestableBehaviorHolder(List<HarvestableBehavior> behaviors)
	{
		super(behaviors);
	}
	
	public List<FarmerListener<? extends Event>> listeners(FarmerBlockMap farmerBlockMap)
	{
		return behaviors.stream()
				.flatMap((h) -> h.getListeners(farmerBlockMap).stream())
				.toList();
	}
}
