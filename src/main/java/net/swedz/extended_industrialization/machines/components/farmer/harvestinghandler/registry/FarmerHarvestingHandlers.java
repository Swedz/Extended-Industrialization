package net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.registry;

import com.google.common.collect.Lists;
import net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.HarvestingHandler;
import net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.handlers.CropBlockHarvestHandler;
import net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.handlers.SimpleTallCropHarvestHandler;
import net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.handlers.TreeBlockHarvestHandler;

import java.util.List;
import java.util.function.Supplier;

public final class FarmerHarvestingHandlers
{
	private static final List<Supplier<HarvestingHandler>> HANDLERS = Lists.newArrayList();
	
	public static void register(Supplier<HarvestingHandler> handler)
	{
		HANDLERS.add(handler);
	}
	
	public static FarmerHarvestingHandlersHolder create()
	{
		return new FarmerHarvestingHandlersHolder(HANDLERS.stream().map(Supplier::get).toList());
	}
	
	static
	{
		register(CropBlockHarvestHandler::new);
		register(SimpleTallCropHarvestHandler::new);
		register(TreeBlockHarvestHandler::new);
	}
}
