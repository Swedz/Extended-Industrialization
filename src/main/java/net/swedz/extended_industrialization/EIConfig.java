package net.swedz.extended_industrialization;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class EIConfig
{
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	
	private static final ModConfigSpec.IntValue LOCAL_WIRELESS_CHARGING_STATION_RANGE = BUILDER
			.comment("The range for the local wireless charging station machine")
			.defineInRange("local_wireless_charging_station_range", 32, 0, Integer.MAX_VALUE);
	
	private static final ModConfigSpec.BooleanValue ALLOW_UPGRADES_IN_PROCESSING_ARRAY = BUILDER
			.comment("Whether upgrades should be allowed in the Processing Array")
			.define("allow_upgrades_in_processing_array", true);
	
	public static final ModConfigSpec SPEC = BUILDER.build();
	
	public static int     localWirelessChargingStationRange;
	public static boolean allowUpgradesInProcessingArray;
	
	public static void loadConfig()
	{
		localWirelessChargingStationRange = LOCAL_WIRELESS_CHARGING_STATION_RANGE.get();
		allowUpgradesInProcessingArray = ALLOW_UPGRADES_IN_PROCESSING_ARRAY.get();
	}
}
