package net.swedz.miextended;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MIExtended.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class MIExtendedClient
{
	@SubscribeEvent
	private static void init(FMLConstructModEvent __)
	{
		// Do nothing for now, but this could be useful
	}
}
