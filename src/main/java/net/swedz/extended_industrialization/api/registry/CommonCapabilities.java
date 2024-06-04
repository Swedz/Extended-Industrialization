package net.swedz.extended_industrialization.api.registry;

import aztech.modern_industrialization.api.energy.EnergyApi;
import dev.technici4n.grandpower.api.ISimpleEnergyItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

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
	
	public static <Type extends Item> void bucketItem(Type item, RegisterCapabilitiesEvent event)
	{
		event.registerItem(Capabilities.FluidHandler.ITEM, (stack, context) -> new FluidBucketWrapper(stack), item);
	}
}
