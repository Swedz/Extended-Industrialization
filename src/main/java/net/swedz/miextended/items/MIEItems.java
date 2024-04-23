package net.swedz.miextended.items;

import com.google.common.collect.Sets;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.api.item.ItemCreator;
import net.swedz.miextended.items.items.ElectricToolItem;
import net.swedz.miextended.items.items.TinCanFoodItem;

import java.util.Set;

public final class MIEItems
{
	private static final DeferredRegister.Items ITEMS         = DeferredRegister.createItems(MIExtended.ID);
	private static final Set<MIEItemWrapper>    ITEM_WRAPPERS = Sets.newHashSet();
	
	public static void init(IEventBus bus)
	{
		ITEMS.register(bus);
	}
	
	public static final MIEItemWrapper<Item>             TIN_CAN              = create("tin_can", "Tin Can", Item::new).withBasicModel().register();
	public static final MIEItemWrapper<TinCanFoodItem>   CANNED_FOOD          = create("canned_food", "Canned Food", TinCanFoodItem::new).withProperties((p) -> p.food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3f).meat().fast().build())).withBasicModel().register();
	public static final MIEItemWrapper<ElectricToolItem> ELETRIC_MINING_DRILL = create("electric_mining_drill", "Electric Mining Drill", (p) -> new ElectricToolItem(p, false)).tag(ItemTags.PICKAXES, ItemTags.SHOVELS).withSimplyEnergyItemCapability().withHandheldModel().register();
	public static final MIEItemWrapper<ElectricToolItem> ELETRIC_CHAINSAW     = create("electric_chainsaw", "Electric Chainsaw", (p) -> new ElectricToolItem(p, true)).tag(ItemTags.AXES).withSimplyEnergyItemCapability().withHandheldModel().register();
	public static final MIEItemWrapper<Item>             MULCH                = create("mulch", "Mulch", Item::new).withProperties((p) -> p.food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3f).fast().build())).withBasicModel().register();
	
	private static <I extends Item> MIEItemWrapper<I> create(String id, String englishName, ItemCreator<I, MIEItemProperties> creator)
	{
		return new MIEItemWrapper<I>().identifiable(id, englishName).withCreator(creator);
	}
	
	public static Set<MIEItemWrapper> all()
	{
		return Set.copyOf(ITEM_WRAPPERS);
	}
	
	static <I extends Item> DeferredItem<I> include(MIEItemWrapper<I> wrapper)
	{
		ITEM_WRAPPERS.add(wrapper);
		return ITEMS.registerItem(wrapper.id(false), (p) -> wrapper.creator().create((MIEItemProperties) p), wrapper.properties());
	}
}
