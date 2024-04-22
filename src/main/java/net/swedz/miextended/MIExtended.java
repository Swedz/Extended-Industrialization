package net.swedz.miextended;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.swedz.miextended.api.capabilities.CapabilitiesListeners;
import net.swedz.miextended.api.isolatedlistener.IsolatedListeners;
import net.swedz.miextended.datagen.DatagenDelegator;
import net.swedz.miextended.items.MIEItemWrapper;
import net.swedz.miextended.items.MIEItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MIExtended.ID)
public final class MIExtended
{
	public static final String ID = "miextended";
	
	public static ResourceLocation id(String name)
	{
		return new ResourceLocation(ID, name);
	}
	
	public static final Logger LOGGER = LoggerFactory.getLogger("MI Extended");
	
	public MIExtended(IEventBus bus)
	{
		MIEItems.init(bus);
		
		IsolatedListeners.init();
		
		bus.register(new DatagenDelegator());
		
		bus.addListener(FMLCommonSetupEvent.class, (event) ->
				MIEItems.all().forEach(MIEItemWrapper::runItemRegistrationListener));
		bus.addListener(RegisterCapabilitiesEvent.class, CapabilitiesListeners::triggerAll);
	}
}
