package net.swedz.miextended.registry.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.miextended.registry.api.ModeledRegisteredObjectHolder;

import java.util.Optional;

public class ItemHolder<Type extends Item> extends ModeledRegisteredObjectHolder<Item, Type, Item.Properties, ItemModelBuilder, ItemHolder<Type>> implements ItemLike
{
	private final DeferredRegister.Items registerItems;
	
	private Optional<DeferredItem<Type>> deferredItem = Optional.empty();
	
	public ItemHolder(ResourceLocation id, String englishName, DeferredRegister.Items registerItems)
	{
		super(id, englishName);
		this.registerItems = registerItems;
	}
	
	@Override
	protected Item.Properties defaultProperties()
	{
		return new Item.Properties();
	}
	
	@Override
	public ItemHolder<Type> register()
	{
		DeferredItem<Type> item = registerItems.registerItem(identifier.id(), (properties) -> creator.apply(properties), properties);
		deferredItem = Optional.of(item);
		return this.self();
	}
	
	@Override
	public Type get()
	{
		return deferredItem.orElseThrow(() -> new IllegalStateException("Cannot get item that hasn't been registered yet")).get();
	}
	
	@Override
	public Item asItem()
	{
		return this.get();
	}
}
