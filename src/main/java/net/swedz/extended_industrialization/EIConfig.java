package net.swedz.extended_industrialization;

import aztech.modern_industrialization.api.energy.CableTier;
import com.mojang.serialization.Codec;
import net.swedz.tesseract.neoforge.api.Assert;
import net.swedz.tesseract.neoforge.config.annotation.ConfigComment;
import net.swedz.tesseract.neoforge.config.annotation.ConfigKey;
import net.swedz.tesseract.neoforge.config.annotation.Range;
import net.swedz.tesseract.neoforge.config.annotation.SubSection;

import java.util.Collections;
import java.util.Map;

public interface EIConfig
{
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
	
	@ConfigKey("tesla_coil_range")
	@ConfigComment("The range for the tesla coil to transmit energy within")
	@Range.Integer(min = 1, max = Integer.MAX_VALUE)
	default int teslaCoilRange()
	{
		return 32;
	}
	
	@ConfigKey("lethal_tesla_coil")
	@SubSection
	LethalTeslaCoil lethalTeslaCoil();
	
	interface LethalTeslaCoil
	{
		@ConfigKey("range")
		@ConfigComment("The range for the lethal tesla coil to damage entities within")
		@Range.Integer(min = 1, max = Integer.MAX_VALUE)
		default int range()
		{
			return 2;
		}
		
		@ConfigKey("damage")
		@ConfigComment({
				"The amount of damage dealt by the lethal tesla coil for a cable tier",
				"If no value is specified for a cable tier, the cable tier's EU value is divided by 16 to determine the damage dealt",
				"Range: >= 0.1"
		})
		default CableTierDamages damage()
		{
			return new CableTierDamages(Map.of(
					CableTier.SUPERCONDUCTOR, (double) Integer.MAX_VALUE
			));
		}
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
	
	final class CableTierDamages
	{
		public static final Codec<CableTierDamages> CODEC = Codec.unboundedMap(
				Codec.STRING.xmap(CableTier::getTier, (tier) -> tier.name),
				Codec.doubleRange(0.1D, Integer.MAX_VALUE)
		).xmap(CableTierDamages::new, (value) -> value.damages);
		
		private final Map<CableTier, Double> damages;
		
		private CableTierDamages(Map<CableTier, Double> damages)
		{
			this.damages = Collections.unmodifiableMap(damages);
		}
		
		public double get(CableTier cableTier)
		{
			Assert.notNull(cableTier);
			
			return damages.getOrDefault(cableTier, cableTier.eu / 16D);
		}
	}
}
