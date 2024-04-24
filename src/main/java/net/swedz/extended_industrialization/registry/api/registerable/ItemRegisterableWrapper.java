package net.swedz.extended_industrialization.registry.api.registerable;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class ItemRegisterableWrapper<Type extends Item> extends RegisterableWrapper<Type, DeferredItem<Type>, DeferredRegister.Items, Item.Properties>
{
	public ItemRegisterableWrapper(DeferredRegister.Items register, Item.Properties properties, Function<Item.Properties, Type> creator)
	{
		super(register, properties, creator);
	}
}
