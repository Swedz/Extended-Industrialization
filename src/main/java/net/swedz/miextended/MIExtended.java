package net.swedz.miextended;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import net.swedz.miextended.api.capabilities.CapabilitiesListeners;
import net.swedz.miextended.api.isolatedlistener.IsolatedListeners;
import net.swedz.miextended.datagen.DatagenDelegator;
import net.swedz.miextended.datamaps.MIEDataMaps;
import net.swedz.miextended.registry.items.MIEItems;
import net.swedz.miextended.registry.items.ItemHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Mod(MIExtended.ID)
public final class MIExtended
{
	public static final String ID = "miextended";
	
	public static ResourceLocation id(String name)
	{
		return new ResourceLocation(ID, name);
	}
	
	public static final Logger LOGGER = LoggerFactory.getLogger("MI Extended");
	
	private static final Set<ResourceLocation> ITEMS_REGISTERED_BY_MI_BUT_ARE_FROM_MIE_ACTUALLY = Sets.newHashSet();
	
	public static void includeItemRegisteredByMI(ResourceLocation itemKey)
	{
		ITEMS_REGISTERED_BY_MI_BUT_ARE_FROM_MIE_ACTUALLY.add(itemKey);
	}
	
	public static boolean isItemRegisteredByMIButActuallyFromMIE(ResourceLocation itemKey)
	{
		return ITEMS_REGISTERED_BY_MI_BUT_ARE_FROM_MIE_ACTUALLY.contains(itemKey);
	}
	
	public MIExtended(IEventBus bus)
	{
		MIEItems.init(bus);
		
		IsolatedListeners.init();
		
		bus.register(new DatagenDelegator());
		
		bus.addListener(FMLCommonSetupEvent.class, (event) ->
		{
			MIEItems.values().forEach(ItemHolder::triggerRegistrationListener);
		});
		bus.addListener(RegisterCapabilitiesEvent.class, CapabilitiesListeners::triggerAll);
		
		bus.addListener(RegisterDataMapTypesEvent.class, (event) ->
				event.register(MIEDataMaps.FERTILIZER_POTENCY));
	}
}
