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
	
	private static final ModConfigSpec.BooleanValue DISPLAY_MACHINE_VOLTAGE = BUILDER
			.comment("Whether the voltage of a machine should be displayed. This includes displaying voltage of hatches and hulls")
			.define("display_machine_voltage", false);
	
	private static final ModConfigSpec.EnumValue<MachineEfficiencyHack> MACHINE_EFFICIENCY_HACK = BUILDER
			.comment(
					"The machine efficiency hack mode to use. Only applies to electric machines",
					"DISABLED = No change will be made to MI's efficiency behavior",
					"ALWAYS_MAX = The efficiency bar will always be forced to max",
					"USE_VOLTAGE = The speed of machines will be determined by their voltage (WARNING! This is designed specifically for pack creators, and existing recipes may not be accessible by all voltages, most notably EBF recipes. Use at your own risk. It is recommended when using this mode to modify recipes with higher EU costs to use the voltage recipe condition or the ebf coil recipe condition)"
			)
			.defineEnum("machine_efficiency_hack", MachineEfficiencyHack.DISABLED);
	
	public static final ModConfigSpec SPEC = BUILDER.build();
	
	public static boolean               requireWaterBiomeForPump;
	public static int                   localWirelessChargingStationRange;
	public static boolean               displayMachineVoltage;
	public static MachineEfficiencyHack machineEfficiencyHack;
	
	public static void loadConfig()
	{
		requireWaterBiomeForPump = REQUIRE_WATER_BIOME_FOR_PUMP.get();
		localWirelessChargingStationRange = LOCAL_WIRELESS_CHARGING_STATION_RANGE.get();
		displayMachineVoltage = DISPLAY_MACHINE_VOLTAGE.get();
		machineEfficiencyHack = MACHINE_EFFICIENCY_HACK.get();
	}
	
	@SubscribeEvent
	static void onConfigLoad(ModConfigEvent event)
	{
		loadConfig();
	}
	
	public enum MachineEfficiencyHack
	{
		DISABLED(false, false, false, false),
		ALWAYS_MAX(true, false, false, false),
		USE_VOLTAGE(true, true, true, true);
		
		private final boolean forceMaxEfficiency;
		private final boolean useVoltageForEfficiency;
		private final boolean preventsUpgrades;
		private final boolean hideEfficiency;
		
		MachineEfficiencyHack(boolean forceMaxEfficiency, boolean useVoltageForEfficiency, boolean preventsUpgrades, boolean hideEfficiency)
		{
			this.forceMaxEfficiency = forceMaxEfficiency;
			this.useVoltageForEfficiency = useVoltageForEfficiency;
			this.preventsUpgrades = preventsUpgrades;
			this.hideEfficiency = hideEfficiency;
		}
		
		public boolean forceMaxEfficiency()
		{
			return forceMaxEfficiency;
		}
		
		public boolean useVoltageForEfficiency()
		{
			return useVoltageForEfficiency;
		}
		
		public boolean preventsUpgrades()
		{
			return preventsUpgrades;
		}
		
		public boolean hideEfficiency()
		{
			return hideEfficiency;
		}
	}
}
