package net.swedz.extended_industrialization.machines.components.farmer.plantinghandler.registry;

import com.google.common.collect.Lists;
import net.swedz.extended_industrialization.machines.components.farmer.plantinghandler.PlantingHandler;
import net.swedz.extended_industrialization.machines.components.farmer.plantinghandler.handlers.SpecialPlantablePlantingHandler;
import net.swedz.extended_industrialization.machines.components.farmer.plantinghandler.handlers.VanillaPlantingHandler;

import java.util.List;
import java.util.function.Supplier;

public final class FarmerPlantingHandlers
{
	private static final List<Supplier<PlantingHandler>> HANDLERS = Lists.newArrayList();
	
	public static void register(Supplier<PlantingHandler> handler)
	{
		HANDLERS.add(handler);
	}
	
	public static FarmerPlantingHandlersHolder create()
	{
		return new FarmerPlantingHandlersHolder(HANDLERS.stream().map(Supplier::get).toList());
	}
	
	static
	{
		register(VanillaPlantingHandler::new);
		register(SpecialPlantablePlantingHandler::new);
	}
}
