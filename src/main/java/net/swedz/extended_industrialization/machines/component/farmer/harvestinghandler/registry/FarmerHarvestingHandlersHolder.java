package net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler.registry;

import net.neoforged.bus.api.Event;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerBlockMap;
import net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler.HarvestingContext;
import net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler.HarvestingHandler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class FarmerHarvestingHandlersHolder
{
	private final List<HarvestingHandler> handlers;
	
	FarmerHarvestingHandlersHolder(List<HarvestingHandler> handlers)
	{
		this.handlers = handlers;
	}
	
	public List<HarvestingHandler> getHandlers()
	{
		return Collections.unmodifiableList(handlers);
	}
	
	public Optional<HarvestingHandler> getHandler(HarvestingContext context)
	{
		return handlers.stream()
				.filter((handler) -> handler.matches(context))
				.findFirst();
	}
	
	public List<FarmerListener<? extends Event>> getListeners(FarmerBlockMap farmerBlockMap)
	{
		return handlers.stream()
				.flatMap((h) -> h.getListeners(farmerBlockMap).stream())
				.toList();
	}
}
