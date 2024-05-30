package net.swedz.extended_industrialization.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.swedz.extended_industrialization.EI;

@Mod.EventBusSubscriber(modid = EI.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EIConfig
{
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	
	private static final ModConfigSpec.BooleanValue REQUIRE_WATER_BIOME_FOR_PUMP = BUILDER
			.comment("Whether water pumps require a water biome (river or ocean) to operate")
			.define("require_water_biome_for_pump", true);
	
	private static final ModConfigSpec.IntValue LOCAL_WIRELESS_CHARGING_STATION_RANGE = BUILDER
			.comment("The range for the local wireless charging station machine")
			.defineInRange("local_wireless_charging_station_range", 32, 0, Integer.MAX_VALUE);
	
	private static final ModConfigSpec.BooleanValue DISPLAY_MACHINE_VOLTAGE_IN_UI = BUILDER
			.comment("Whether the voltage of a machine should be included in the title")
			.define("display_machine_voltage_in_ui", false);
	
	public static final ModConfigSpec SPEC = BUILDER.build();
	
	public static boolean requireWaterBiomeForPump;
	public static int     localWirelessChargingStationRange;
	public static boolean displayMachineVoltageInUI;
	
	@SubscribeEvent
	static void onConfigLoad(ModConfigEvent event)
	{
		requireWaterBiomeForPump = REQUIRE_WATER_BIOME_FOR_PUMP.get();
		localWirelessChargingStationRange = LOCAL_WIRELESS_CHARGING_STATION_RANGE.get();
		displayMachineVoltageInUI = DISPLAY_MACHINE_VOLTAGE_IN_UI.get();
	}
}
