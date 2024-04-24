package net.swedz.extended_industrialization.registry.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.registry.api.ModeledRegisteredObjectHolder;
import net.swedz.extended_industrialization.registry.api.registerable.ItemRegisterableWrapper;

import java.util.function.Consumer;
import java.util.function.Function;

public class ItemHolder<Type extends Item> extends ModeledRegisteredObjectHolder<Item, Type, ItemModelBuilder, ItemHolder<Type>> implements ItemLike
{
	private final ItemRegisterableWrapper<Type> registerableItem;
	
	private SortOrder sortOrder = SortOrder.UNSORTED;
	
	public ItemHolder(ResourceLocation id, String englishName, DeferredRegister.Items registerItems, Function<Item.Properties, Type> creator)
	{
		super(id, englishName);
		this.registerableItem = new ItemRegisterableWrapper<>(registerItems, new Item.Properties(), creator);
	}
	
	public ItemRegisterableWrapper<Type> registerableItem()
	{
		return registerableItem;
	}
	
	public ItemHolder<Type> withProperties(Consumer<Item.Properties> action)
	{
		action.accept(registerableItem.properties());
		return this;
	}
	
	public SortOrder sortOrder()
	{
		return sortOrder;
	}
	
	public ItemHolder<Type> sorted(SortOrder sortOrder)
	{
		this.sortOrder = sortOrder;
		return this;
	}
	
	@Override
	public ItemHolder<Type> register()
	{
		this.guaranteeUnlocked();
		
		registerableItem.register(identifier, DeferredRegister.Items::registerItem);
		
		this.lock();
		return this.self();
	}
	
	@Override
	public Type get()
	{
		return registerableItem.getOrThrow();
	}
	
	@Override
	public Item asItem()
	{
		return this.get();
	}
}
