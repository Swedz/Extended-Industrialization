package net.swedz.miextended.items;

import com.google.common.collect.Sets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.miextended.MIExtended;

import java.util.Set;

public final class MIEItems
{
	private static final DeferredRegister.Items ITEMS         = DeferredRegister.createItems(MIExtended.ID);
	private static final Set<MIEItemWrapper>    ITEM_WRAPPERS = Sets.newHashSet();
	
	public static void init(IEventBus bus)
	{
		ITEMS.register(bus);
	}
	
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
