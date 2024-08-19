package net.swedz.extended_industrialization;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class EIConfig
{
	private static final ModConfigSpec.Builder BUILDER;
	
	private static final ModConfigSpec.IntValue     LOCAL_WIRELESS_CHARGING_STATION_RANGE;
	private static final ModConfigSpec.BooleanValue ALLOW_UPGRADES_IN_PROCESSING_ARRAY;
	private static final ModConfigSpec.IntValue     LARGE_STEAM_MACERATOR_SIZE;
	private static final ModConfigSpec.DoubleValue  LARGE_STEAM_MACERATOR_EU;
	private static final ModConfigSpec.IntValue     LARGE_STEAM_FURNACE_SIZE;
	private static final ModConfigSpec.DoubleValue  LARGE_STEAM_FURNACE_EU;
	private static final ModConfigSpec.IntValue     LARGE_ELECTRIC_MACERATOR_SIZE;
	private static final ModConfigSpec.DoubleValue  LARGE_ELECTRIC_MACERATOR_EU;
	private static final ModConfigSpec.DoubleValue  PROCESSING_ARRAY_EU;
	
	public static final ModConfigSpec SPEC;
	
	static
	{
		BUILDER = new ModConfigSpec.Builder();
		
		LOCAL_WIRELESS_CHARGING_STATION_RANGE = BUILDER
				.comment("The range for the local wireless charging station machine")
				.defineInRange("local_wireless_charging_station_range", 32, 0, Integer.MAX_VALUE);
		
		ALLOW_UPGRADES_IN_PROCESSING_ARRAY = BUILDER
				.comment("Whether upgrades should be allowed in the Processing Array")
				.define("allow_upgrades_in_processing_array", true);
		
		{
			BUILDER.push("batching_machines");
			
			LARGE_STEAM_MACERATOR_SIZE = BUILDER
					.comment("The maximum batch size to use for the Large Steam Macerator")
					.defineInRange("large_steam_macerator_size", 8, 1, Integer.MAX_VALUE);
			LARGE_STEAM_MACERATOR_EU = BUILDER
					.comment("The multiplier to use for the EU cost of the Large Steam Macerator")
					.defineInRange("large_steam_macerator_eu", 0.75D, 0.1D, Double.MAX_VALUE);
			
			LARGE_STEAM_FURNACE_SIZE = BUILDER
					.comment("The maximum batch size to use for the Large Steam Furnace")
					.defineInRange("large_steam_furnace_size", 8, 1, Integer.MAX_VALUE);
			LARGE_STEAM_FURNACE_EU = BUILDER
					.comment("The multiplier to use for the EU cost of the Large Steam Furnace")
					.defineInRange("large_steam_furnace_eu", 0.75D, 0.1D, Double.MAX_VALUE);
			
			LARGE_ELECTRIC_MACERATOR_SIZE = BUILDER
					.comment("The maximum batch size to use for the Large Electric Macerator")
					.defineInRange("large_electric_macerator_size", 16, 1, Integer.MAX_VALUE);
			LARGE_ELECTRIC_MACERATOR_EU = BUILDER
					.comment("The multiplier to use for the EU cost of the Large Electric Macerator")
					.defineInRange("large_electric_macerator_eu", 0.75D, 0.1D, Double.MAX_VALUE);
			
			PROCESSING_ARRAY_EU = BUILDER
					.comment("The multiplier to use for the EU cost of the Processing Array")
					.defineInRange("processing_array_eu", 1D, 0.1D, Double.MAX_VALUE);
			
			BUILDER.pop();
		}
		
		SPEC = BUILDER.build();
	}
	
	public static int     localWirelessChargingStationRange;
	public static boolean allowUpgradesInProcessingArray;
	public static int     largeSteamMaceratorBatchSize;
	public static double  largeSteamMaceratorEuCostMultiplier;
	public static int     largeSteamFurnaceBatchSize;
	public static double  largeSteamFurnaceEuCostMultiplier;
	public static int     largeElectricMaceratorBatchSize;
	public static double  largeElectricMaceratorEuCostMultiplier;
	public static double  processingArrayEuCostMultiplier;
	
	public static void loadConfig()
	{
		localWirelessChargingStationRange = LOCAL_WIRELESS_CHARGING_STATION_RANGE.get();
		allowUpgradesInProcessingArray = ALLOW_UPGRADES_IN_PROCESSING_ARRAY.get();
		largeSteamMaceratorBatchSize = LARGE_STEAM_MACERATOR_SIZE.get();
		largeSteamMaceratorEuCostMultiplier = LARGE_STEAM_MACERATOR_EU.get();
		largeSteamFurnaceBatchSize = LARGE_STEAM_FURNACE_SIZE.get();
		largeSteamFurnaceEuCostMultiplier = LARGE_STEAM_FURNACE_EU.get();
		largeElectricMaceratorBatchSize = LARGE_ELECTRIC_MACERATOR_SIZE.get();
		largeElectricMaceratorEuCostMultiplier = LARGE_ELECTRIC_MACERATOR_EU.get();
		processingArrayEuCostMultiplier = PROCESSING_ARRAY_EU.get();
	}
}
