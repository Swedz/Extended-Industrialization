package net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler.registry;

import com.google.common.collect.Lists;
import net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler.HarvestingHandler;
import net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler.handlers.CropBlockHarvestHandler;
import net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler.handlers.NetherWartHarvestHandler;
import net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler.handlers.SimpleTallCropHarvestHandler;
import net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler.handlers.TreeHarvestHandler;

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
		register(NetherWartHarvestHandler::new);
		register(SimpleTallCropHarvestHandler::new);
		register(TreeHarvestHandler::new);
	}
}
