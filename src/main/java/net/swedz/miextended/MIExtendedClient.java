package net.swedz.miextended;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.swedz.miextended.tooltips.MIETooltips;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MIExtended.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class MIExtendedClient
{
	@SubscribeEvent
	private static void init(FMLConstructModEvent __)
	{
		NeoForge.EVENT_BUS.register(new MIETooltips());
	}
}
