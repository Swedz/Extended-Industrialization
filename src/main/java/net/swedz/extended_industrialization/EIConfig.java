package net.swedz.extended_industrialization;

import net.swedz.tesseract.neoforge.config.annotation.ConfigComment;
import net.swedz.tesseract.neoforge.config.annotation.ConfigKey;
import net.swedz.tesseract.neoforge.config.annotation.Range;
import net.swedz.tesseract.neoforge.config.annotation.SubSection;

public interface EIConfig
{
	@ConfigKey("local_wireless_charging_station_range")
	@ConfigComment("The range for the local wireless charging station machine")
	@Range.Integer(min = 1, max = Integer.MAX_VALUE)
	default int localWirelessChargingStationRange()
	{
		return 32;
	}
	
	@ConfigKey("machine_chainer_max_connections")
	@ConfigComment("The maximum amount of connections a machine chainer can have")
	@Range.Integer(min = 1, max = 128)
	default int machineChainerMaxConnections()
	{
		return 64;
	}
	
	@ConfigKey("allow_upgrades_in_processing_array")
	@ConfigComment("Whether upgrades should be allowed in the Processing Array")
	default boolean allowUpgradesInProcessingArray()
	{
		return true;
	}
	
	@ConfigKey("farmer_fertilizer_max_random_ticks")
	@ConfigComment("The maximum amount of random ticks the farmer can do on a crop block in a single use of fertilizer")
	@Range.Integer(min = 1, max = 1000)
	default int farmerFertilizerMaxRandomTicks()
	{
		return 80;
	}
	
	@ConfigKey("batching_machines")
	@SubSection
	BatchingMachines batchingMachines();
	
	interface BatchingMachines
	{
		@ConfigKey("large_steam_macerator_size")
		@ConfigComment("The maximum batch size to use for the Large Steam Macerator")
		@Range.Integer(min = 1, max = Integer.MAX_VALUE)
		default int largeSteamMaceratorSize()
		{
			return 8;
		}
		
		@ConfigKey("large_steam_macerator_eu")
		@ConfigComment("The multiplier to use for the EU cost of the Large Steam Macerator")
		@Range.Double(min = 0.1D, max = Double.MAX_VALUE)
		default double largeSteamMaceratorEU()
		{
			return 0.75;
		}
		
		@ConfigKey("large_steam_furnace_size")
		@ConfigComment("The maximum batch size to use for the Large Steam Furnace")
		@Range.Integer(min = 1, max = Integer.MAX_VALUE)
		default int largeSteamFurnaceSize()
		{
			return 8;
		}
		
		@ConfigKey("large_steam_furnace_eu")
		@ConfigComment("The multiplier to use for the EU cost of the Large Steam Furnace")
		@Range.Double(min = 0.1D, max = Double.MAX_VALUE)
		default double largeSteamFurnaceEU()
		{
			return 0.75;
		}
		
		@ConfigKey("large_electric_macerator_size")
		@ConfigComment("The maximum batch size to use for the Large Electric Macerator")
		@Range.Integer(min = 1, max = Integer.MAX_VALUE)
		default int largeElectricMaceratorSize()
		{
			return 16;
		}
		
		@ConfigKey("large_electric_macerator_eu")
		@ConfigComment("The multiplier to use for the EU cost of the Large Electric Macerator")
		@Range.Double(min = 0.1D, max = Double.MAX_VALUE)
		default double largeElectricMaceratorEU()
		{
			return 0.75;
		}
		
		@ConfigKey("processing_array_eu")
		@ConfigComment("The multiplier to use for the EU cost of the Processing Array")
		@Range.Double(min = 0.1D, max = Double.MAX_VALUE)
		default double processingArrayEU()
		{
			return 1;
		}
	}
}
