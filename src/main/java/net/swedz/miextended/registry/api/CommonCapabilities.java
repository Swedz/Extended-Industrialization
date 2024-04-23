package net.swedz.miextended.registry.api;

import aztech.modern_industrialization.api.energy.EnergyApi;
import dev.technici4n.grandpower.api.ISimpleEnergyItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class CommonCapabilities
{
	public static <Type extends Item> void simpleEnergyItem(Type item, RegisterCapabilitiesEvent event)
	{
		ISimpleEnergyItem simpleEnergyItem = (ISimpleEnergyItem) item;
		event.registerItem(
				EnergyApi.ITEM,
				(stack, ctx) -> ISimpleEnergyItem.createStorage(
						stack,
						simpleEnergyItem.getEnergyCapacity(stack),
						simpleEnergyItem.getEnergyMaxInput(stack),
						simpleEnergyItem.getEnergyMaxOutput(stack)
				),
				item
		);
	}
}
