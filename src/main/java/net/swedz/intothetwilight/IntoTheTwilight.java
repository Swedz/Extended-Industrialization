package net.swedz.intothetwilight;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.swedz.intothetwilight.datagen.DatagenListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(IntoTheTwilight.ID)
public final class IntoTheTwilight
{
	public static final String ID = "intothetwilight";
	
	public static ResourceLocation id(String name)
	{
		return new ResourceLocation(ID, name);
	}
	
	public static final Logger LOGGER = LoggerFactory.getLogger("Into the Twilight");
	
	public IntoTheTwilight(IEventBus modBus)
	{
		modBus.register(new DatagenListener());
	}
}
