package net.swedz.miextended.registry.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.miextended.registry.api.ModeledRegisteredObjectHolder;
import net.swedz.miextended.registry.api.registerable.ItemRegisterableWrapper;

import java.util.function.Consumer;
import java.util.function.Function;

public class ItemHolder<Type extends Item> extends ModeledRegisteredObjectHolder<Item, Type, ItemModelBuilder, ItemHolder<Type>> implements ItemLike
{
	private final ItemRegisterableWrapper<Type> registerItem;
	
	public ItemHolder(ResourceLocation id, String englishName, DeferredRegister.Items registerItems, Function<Item.Properties, Type> creator)
	{
		super(id, englishName);
		this.registerItem = new ItemRegisterableWrapper<>(registerItems, new Item.Properties(), creator);
	}
	
	public ItemHolder<Type> withProperties(Consumer<Item.Properties> action)
	{
		action.accept(registerItem.properties());
		return this;
	}
	
	@Override
	public ItemHolder<Type> register()
	{
		this.guaranteeUnlocked();
		
		registerItem.register(identifier, DeferredRegister.Items::registerItem);
		
		this.lock();
		return this.self();
	}
	
	@Override
	public Type get()
	{
		return registerItem.getOrThrow();
	}
	
	@Override
	public Item asItem()
	{
		return this.get();
	}
}
