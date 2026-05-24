package net.swedz.extended_industrialization;

import aztech.modern_industrialization.api.energy.CableTier;
import com.mojang.serialization.Codec;
import net.swedz.tesseract.api.Assert;
import net.swedz.tesseract.neoforge.compat.mi.serialization.MICodecs;
import net.swedz.tesseract.config.annotation.ConfigComment;
import net.swedz.tesseract.config.annotation.ConfigKey;
import net.swedz.tesseract.config.annotation.Range;
import net.swedz.tesseract.config.annotation.SubSection;

import java.util.Collections;
import java.util.Map;

public interface EIConfig
{
	@ConfigKey
	@ConfigComment("The maximum amount of connections a machine chainer can have")
	@Range.Integer(min = 1, max = 128)
	default int machineChainerMaxConnections()
	{
		return 64;
	}
	
	@ConfigKey
	@ConfigComment({
			"The multiplier applied to the transfer rate of machine chainers. The base transfer rate is equal to the max transfer rate of the cable tier of the chainer (as per the hull).",
			"Set to 0 for infinite transfer."
	})
	@Range.Integer(min = 0, max = 1000)
	default int machineChainerMaxTransferMultiplier()
	{
		return 3;
	}
	
	@ConfigKey
	@ConfigComment("Whether upgrades should be allowed in the Processing Array")
	default boolean allowUpgradesInProcessingArray()
	{
		return true;
	}
	
	@ConfigKey
	@ConfigComment({
			"The maximum size tier allowed for the Processing Array.",
			"WARNING: Changing this and then loading a world with a Processing Array that has a selected tier beyond this value",
			"         will crash. If you must change this for an existing world, I recommend downgrading all of the existing",
			"         Processing Arrays and then making the change to this config option.",
			"1 = 8, 2 = 16, 3 = 32, 4 = 64 (default)"
	})
	@Range.Integer(min = 1, max = 4)
	default int processingArrayMaxSize()
	{
		return 4;
	}
	
	@ConfigKey
	@ConfigComment("The maximum amount of random ticks the farmer can do on a crop block in a single use of fertilizer")
	@Range.Integer(min = 1, max = 1000)
	default int farmerFertilizerMaxRandomTicks()
	{
		return 80;
	}
	
	@ConfigKey
	@ConfigComment("The range for the tesla coil to transmit energy within")
	@Range.Integer(min = 1, max = Integer.MAX_VALUE)
	default int teslaCoilRange()
	{
		return 32;
	}
	
	@ConfigKey
	@SubSection
	LethalTeslaCoil lethalTeslaCoil();
	
	interface LethalTeslaCoil
	{
		@ConfigKey
		@ConfigComment("The range for the lethal tesla coil to damage entities within")
		@Range.Integer(min = 1, max = Integer.MAX_VALUE)
		default int range()
		{
			return 2;
		}
		
		@ConfigKey
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
	
	@ConfigKey
	@SubSection
	BatchingMachines batchingMachines();
	
	interface BatchingMachines
	{
		@ConfigKey
		@ConfigComment("The maximum batch size to use for the Large Steam Macerator")
		@Range.Integer(min = 1, max = Integer.MAX_VALUE)
		default int largeSteamMaceratorSize()
		{
			return 8;
		}
		
		@ConfigKey
		@ConfigComment("The multiplier to use for the EU cost of the Large Steam Macerator")
		@Range.Double(min = 0.1D, max = Double.MAX_VALUE)
		default double largeSteamMaceratorEU()
		{
			return 0.75;
		}
		
		@ConfigKey
		@ConfigComment("The maximum batch size to use for the Large Steam Furnace")
		@Range.Integer(min = 1, max = Integer.MAX_VALUE)
		default int largeSteamFurnaceSize()
		{
			return 8;
		}
		
		@ConfigKey
		@ConfigComment("The multiplier to use for the EU cost of the Large Steam Furnace")
		@Range.Double(min = 0.1D, max = Double.MAX_VALUE)
		default double largeSteamFurnaceEU()
		{
			return 0.75;
		}
		
		@ConfigKey
		@ConfigComment("The maximum batch size to use for the Large Electric Macerator")
		@Range.Integer(min = 1, max = Integer.MAX_VALUE)
		default int largeElectricMaceratorSize()
		{
			return 16;
		}
		
		@ConfigKey
		@ConfigComment("The multiplier to use for the EU cost of the Large Electric Macerator")
		@Range.Double(min = 0.1D, max = Double.MAX_VALUE)
		default double largeElectricMaceratorEU()
		{
			return 0.75;
		}
		
		@ConfigKey
		@ConfigComment("The multiplier to use for the EU cost of the Processing Array")
		@Range.Double(min = 0.1D, max = Double.MAX_VALUE)
		default double processingArrayEU()
		{
			return 1;
		}
	}
	
	@ConfigKey
	@SubSection
	RuntimeGeneratedRecipes runtimeGeneratedRecipes();
	
	interface RuntimeGeneratedRecipes
	{
		@ConfigKey
		@ConfigComment("Whether canning machine recipes should be generated automatically at runtime or not")
		default boolean canningMachine()
		{
			return true;
		}
		
		@ConfigKey
		@ConfigComment("Whether composter recipes should be generated automatically at runtime or not")
		default boolean composter()
		{
			return true;
		}
	}
	
	final class CableTierDamages
	{
		public static final Codec<CableTierDamages> CODEC = Codec.unboundedMap(
				MICodecs.CABLE_TIER,
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
