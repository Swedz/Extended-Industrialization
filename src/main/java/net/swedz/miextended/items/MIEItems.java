package net.swedz.miextended.items;

import com.google.common.collect.Sets;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.miextended.MIExtended;
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
	
	public static final Item TIN_CAN     = create().identifiable("tin_can", "Tin Can").withBasicModel().register().asItem();
	public static final Item CANNED_FOOD = create().identifiable("canned_food", "Canned Food").withCreator(TinCanFoodItem::new).withProperties((p) -> p.food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3f).meat().fast().build())).withBasicModel().register().asItem();
	
	private static MIEItemWrapper create()
	{
		return new MIEItemWrapper();
	}
	
	public static Set<MIEItemWrapper> all()
	{
		return Set.copyOf(ITEM_WRAPPERS);
	}
	
	static void include(MIEItemWrapper wrapper)
	{
		ITEMS.registerItem(wrapper.id(false), (p) -> wrapper.creator().create((MIEItemProperties) p), wrapper.properties());
		ITEM_WRAPPERS.add(wrapper);
	}
}
