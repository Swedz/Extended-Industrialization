package net.swedz.extended_industrialization.registry.items;

import com.google.common.collect.Sets;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.registry.api.CommonCapabilities;
import net.swedz.extended_industrialization.registry.api.CommonModelBuilders;
import net.swedz.extended_industrialization.registry.items.items.ElectricToolItem;
import net.swedz.extended_industrialization.registry.items.items.TinCanFoodItem;

import java.util.Set;
import java.util.function.Function;

public final class EIItems
{
	public static final class Registry
	{
		public static final  DeferredRegister.Items ITEMS   = DeferredRegister.createItems(EI.ID);
		private static final Set<ItemHolder>        HOLDERS = Sets.newHashSet();
		
		private static void init(IEventBus bus)
		{
			ITEMS.register(bus);
		}
		
		public static void include(ItemHolder holder)
		{
			HOLDERS.add(holder);
		}
	}
	
	public static void init(IEventBus bus)
	{
		Registry.init(bus);
	}
	
	public static final ItemHolder<Item>             TIN_CAN              = create("tin_can", "Tin Can", Item::new).withModel(CommonModelBuilders::generated).register();
	public static final ItemHolder<TinCanFoodItem>   CANNED_FOOD          = create("canned_food", "Canned Food", TinCanFoodItem::new).withProperties((p) -> p.food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3f).meat().fast().build())).withModel(CommonModelBuilders::generated).register();
	public static final ItemHolder<ElectricToolItem> ELETRIC_MINING_DRILL = create("electric_mining_drill", "Electric Mining Drill", (p) -> new ElectricToolItem(p, false)).tag(ItemTags.PICKAXES, ItemTags.SHOVELS).withCapabilities(CommonCapabilities::simpleEnergyItem).withModel(CommonModelBuilders::handheld).register();
	public static final ItemHolder<ElectricToolItem> ELETRIC_CHAINSAW     = create("electric_chainsaw", "Electric Chainsaw", (p) -> new ElectricToolItem(p, true)).tag(ItemTags.AXES).withCapabilities(CommonCapabilities::simpleEnergyItem).withModel(CommonModelBuilders::handheld).register();
	public static final ItemHolder<Item>             MULCH                = create("mulch", "Mulch", Item::new).withProperties((p) -> p.food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3f).fast().build())).withModel(CommonModelBuilders::generated).register();
	
	public static Set<ItemHolder> values()
	{
		return Set.copyOf(Registry.HOLDERS);
	}
	
	public static <Type extends Item> ItemHolder<Type> create(String id, String englishName, Function<Item.Properties, Type> creator)
	{
		ItemHolder<Type> holder = new ItemHolder<>(EI.id(id), englishName, Registry.ITEMS, creator);
		Registry.include(holder);
		return holder;
	}
}
