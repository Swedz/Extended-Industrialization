package net.swedz.extended_industrialization.machines.components.farmer.plantinghandler.registry;

import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.machines.components.farmer.plantinghandler.PlantingHandler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class FarmerPlantingHandlersHolder
{
	private final List<PlantingHandler> handlers;
	
	FarmerPlantingHandlersHolder(List<PlantingHandler> handlers)
	{
		this.handlers = handlers;
	}
	
	public List<PlantingHandler> getHandlers()
	{
		return Collections.unmodifiableList(handlers);
	}
	
	public Optional<PlantingHandler> getHandler(ItemStack stack)
	{
		return handlers.stream()
				.filter((handler) -> handler.matches(stack))
				.findFirst();
	}
}
