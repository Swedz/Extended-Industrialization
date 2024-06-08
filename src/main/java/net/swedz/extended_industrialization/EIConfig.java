package net.swedz.extended_industrialization;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod.EventBusSubscriber(modid = EI.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EIConfig
{
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	
	private static final ModConfigSpec.IntValue LOCAL_WIRELESS_CHARGING_STATION_RANGE = BUILDER
			.comment("The range for the local wireless charging station machine")
			.defineInRange("local_wireless_charging_station_range", 32, 0, Integer.MAX_VALUE);
	
	public static final ModConfigSpec SPEC = BUILDER.build();
	
	public static int localWirelessChargingStationRange;
	
	public static void loadConfig()
	{
		localWirelessChargingStationRange = LOCAL_WIRELESS_CHARGING_STATION_RANGE.get();
	}
	
	@SubscribeEvent
	static void onConfigLoad(ModConfigEvent event)
	{
		loadConfig();
	}
}
