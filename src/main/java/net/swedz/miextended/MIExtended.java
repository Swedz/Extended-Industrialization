package net.swedz.miextended;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.swedz.miextended.api.isolatedlistener.IsolatedListeners;
import net.swedz.miextended.datagen.DatagenDelegator;
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
	}
}
