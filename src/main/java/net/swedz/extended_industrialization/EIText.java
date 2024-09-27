package net.swedz.extended_industrialization;

import net.swedz.tesseract.neoforge.tooltip.TranslatableTextEnum;

public enum EIText implements TranslatableTextEnum
{
	ACTIVATED("Activated"),
	BREWERY_BREWS_MULTIPLE("Brews %s potions at a time."),
	BREWERY_REQUIRES_BLAZING_ESSENCE("Requires %s to brew potions."),
	CALCIFICATION_PERCENTAGE("Calcification: %d %%"),
	CHARGING_STATION_GLOBAL("Charges items in your inventory while within the same dimension."),
	CHARGING_STATION_INTERDIMENSIONAL("Charges items in your inventory while in any dimension."),
	CHARGING_STATION_RANGED("Charges items in your inventory while within %d blocks."),
	COILS_LEF_TIER("Runs LEF in batches of up to %d at %s the EU cost."),
	DEACTIVATED("Deactivated"),
	DYEABLE_AND_TRIMMABLE_HELP("- Can be dyed and trimmed!"),
	DYEABLE_HELP("- Can be dyed!"),
	ELECTRIC_TOOL_3_BY_3_TOGGLED_OFF("Disabled 3x3 Mining"),
	ELECTRIC_TOOL_3_BY_3_TOGGLED_ON("Enabled 3x3 Mining"),
	ELECTRIC_TOOL_HELP_1("Tool configuration:"),
	ELECTRIC_TOOL_HELP_2("- Press %s + %s to swap between Fortune and Silk Touch."),
	ELECTRIC_TOOL_HELP_2_LOOTING("- Press %s + %s to swap between Fortune/Looting and Silk Touch."),
	ELECTRIC_TOOL_HELP_3("- Use %s + %s to change mining speed."),
	ELECTRIC_TOOL_HELP_4("- Press %s to toggle 3x3 mining."),
	FARMER_NOT_TILLING("Not Tilling"),
	FARMER_PLANTING_ALTERNATING_LINES("Alternating Lines"),
	FARMER_PLANTING_AS_NEEDED("As Needed"),
	FARMER_PLANTING_QUADRANTS("Quadrants"),
	FARMER_TASK("  - %s: %s"),
	FARMER_TASK_FERTILIZING("Fertilizing"),
	FARMER_TASK_FERTILIZING_DESCRIPTION("When supplied with a valid fluid fertilizer, it will randomly bonemeal crops and saplings."),
	FARMER_TASK_HARVESTING("Harvesting"),
	FARMER_TASK_HARVESTING_DESCRIPTION("When there is enough output space provided, it will harvest fully grown crops and trees."),
	FARMER_TASK_HYDRATING("Hydrating"),
	FARMER_TASK_HYDRATING_DESCRIPTION("When supplied with water, tilled soil will be hydrated."),
	FARMER_TASK_PLANTING("Planting"),
	FARMER_TASK_PLANTING_DESCRIPTION("When supplied with crops or saplings, it will plant them on valid soil. Using different planting modes will plant them in different arrangements."),
	FARMER_TASK_TILLING("Tilling"),
	FARMER_TASK_TILLING_DESCRIPTION("When enabled, dirt blocks will be turned into farmland. This will not work unless water is supplied."),
	FARMER_TASK_TOOLTIP("Can perform the following tasks using %s:"),
	FARMER_TILLING("Tilling"),
	FLUID_FERTILIZERS("Fluid Fertilizers"),
	FLUID_FERTILIZERS_CONSUMES("Consumes: %dmb"),
	FLUID_FERTILIZERS_TIME("Cycle Time: %.1fs"),
	GENERATING_EU_PER_TICK("Generating: %d EU/t"),
	HONEY_EXTRACTOR_HELP("When placed facing into a beehive, honey will be extracted in fluid form."),
	KEY_ALT("Alt"),
	KEY_MOUSE_SCROLL("Mouse Scroll"),
	MACHINE_BATCHER_COILS("Batch size and cost is determined by coil used."),
	MACHINE_CHAINER_CONNECTED_MACHINES("Connected Machines: %d / %d"),
	MACHINE_CHAINER_HELP_1("Connects up to %d consecutive machines in a horizontal line in the direction it is facing."),
	MACHINE_CHAINER_HELP_2("Accepts items, fluids, and energy and distributes them to connected machines."),
	MACHINE_CHAINER_HELP_3("Can connect to other machine chainers, but it must not link back to itself."),
	MACHINE_CHAINER_PROBLEM_AT("Problem at: %s"),
	MACHINE_CONFIG_CARD_APPLY_FAILED("Failed to apply machine configuration to machine."),
	MACHINE_CONFIG_CARD_APPLY_SUCCESS("Applied machine configuration to machine from card."),
	MACHINE_CONFIG_CARD_CLEAR("Cleared machine configuration from card."),
	MACHINE_CONFIG_CARD_CONFIGURED("Configured (%s)"),
	MACHINE_CONFIG_CARD_HELP_1("- Press %s + %s on a machine to save its settings in the card."),
	MACHINE_CONFIG_CARD_HELP_2("- Use %s on a machine to apply the settings from the card."),
	MACHINE_CONFIG_CARD_HELP_3("- (Optional) Hold in off-hand when placing machines to automatically apply settings."),
	MACHINE_CONFIG_CARD_HELP_4("- Clear using %s + %s on air."),
	MACHINE_CONFIG_CARD_SAVE("Saved machine configuration to card."),
	MINING_AREA("Area: %s"),
	MINING_AREA_1_BY_1("1x1"),
	MINING_AREA_3_BY_3("3x3"),
	MINING_MODE("Mode: %s"),
	MINING_SPEED("Speed: %s"),
	MULCH_GANG_FOR_LIFE_0("I love mulch!"),
	MULCH_GANG_FOR_LIFE_1("Mulch is my favorite food!"),
	NANO_SUIT_CREATIVE_FLIGHT("Creative Flight: %s"),
	NANO_SUIT_HELP_1("Armor information:"),
	NANO_SUIT_HELP_CREATIVE_FLIGHT("- Press %s while equipped to toggle Creative Flight."),
	NANO_SUIT_HELP_NIGHT_VISION("- Press %s while equipped to toggle Night Vision."),
	NANO_SUIT_HELP_SPEED("- Press %s while equipped to toggle the Speed Boost."),
	NANO_SUIT_NIGHT_VISION("Night Vision: %s"),
	NANO_SUIT_NIGHT_VISION_TOGGLED_OFF("Disabled Night Vision"),
	NANO_SUIT_NIGHT_VISION_TOGGLED_ON("Enabled Night Vision"),
	NANO_SUIT_SPEED("Speed: %s"),
	NANO_SUIT_SPEED_TOGGLED_OFF("Disabled Speed Boost"),
	NANO_SUIT_SPEED_TOGGLED_ON("Enabled Speed Boost"),
	PHOTOVOLTAIC_CELL_EU("Will produce up to %s when placed in a Solar Panel."),
	PHOTOVOLTAIC_CELL_REMAINING_OPERATION_TIME("Remaining Operation Time: %s"),
	PHOTOVOLTAIC_CELL_REMAINING_OPERATION_TIME_MINUTES("Remaining Operation Time: %s minute(s)"),
	PROCESSING_ARRAY_BATCH_SIZE("Batch size is determined by the amount of machines provided to it."),
	PROCESSING_ARRAY_EU_COST_MULTIPLIER("Runs at %s the EU cost."),
	PROCESSING_ARRAY_MACHINE_INPUT("Insert electric crafting machines to run in parallel."),
	PROCESSING_ARRAY_RECIPE("Can run recipes of any single block electric crafting machine provided to it in batches."),
	PROCESSING_ARRAY_SIZE("Machines: %d"),
	RAINBOW("Rainbow"),
	SOLAR_BOILER_CALCIFICATION("Will calcify and lose efficiency over time to a minimum of %s efficiency when not using %s. Using an axe on the boiler will reset its calcification."),
	SOLAR_EFFICIENCY("Solar Efficiency: %d %%"),
	SOLAR_PANEL_DISTILLED_WATER("By supplying %s to the Solar Panel, the Photovoltaic Cell in its slot will last 2x as long!"),
	SOLAR_PANEL_PHOTOVOLTAIC_CELL("To produce energy, the Solar Panel needs a matching tier Photovoltaic Cell in its inventory."),
	SOLAR_PANEL_SUNLIGHT("Energy generation rates are determined by how high the sun is in the sky and if the sky is visible."),
	STEAM_CHAINSAW_1("- Press %s on still or flowing water to fill."),
	STEAM_CHAINSAW_2("- Place fuel inside the chainsaw using %s."),
	STEAM_CHAINSAW_3("- Toggle Silk Touch with %s + %s."),
	TESLA_CALIBRATOR_CLEAR("Cleared selection from tesla calibrator."),
	TESLA_CALIBRATOR_LINKED("Linked to %s"),
	TESLA_CALIBRATOR_LINK_FAILED_NO_SELECTION("Failed to link receiver because no transmitter is selected."),
	TESLA_CALIBRATOR_LINK_SUCCESS("Linked receiver to selected transmitter."),
	TESLA_CALIBRATOR_SELECTED("Selected transmitter for calibration."),
	TESLA_RECEIVER_LINKED("Linked to %s"),
	TESLA_RECEIVER_MISMATCHING_VOLTAGE("Missing %s hull"),
	TESLA_RECEIVER_NO_LINK("Not linked to any transmitter"),
	TESLA_RECEIVER_UNLOADED_TRANSMITTER("Transmitter is not loaded"),
	TESLA_TRANSMITTER_RECEIVERS("Receivers: %d"),
	TESLA_TRANSMITTER_NO_ENERGY_HATCHES("No energy hatches provided"),
	TESLA_TRANSMITTER_NO_NETWORK("No network found"),
	TESLA_TRANSMITTER_MISMATCHING_HATCHES("All energy hatches must be of the same voltage."),
	TESLA_TRANSMITTER_VOLTAGE("Transmitting %s power"),
	UNIVERSAL_TRANSFORMER_FROM_TIER_INPUT("Casing for cable tier to convert from (LV by default)."),
	UNIVERSAL_TRANSFORMER_TO_TIER_INPUT("Casing for cable tier to convert to (LV by default)."),
	WASTE_COLLECTOR_HELP("When placed underneath animals, manure will be collected.");
	
	private final String englishText;
	
	EIText(String englishText)
	{
		this.englishText = englishText;
	}
	
	@Override
	public String englishText()
	{
		return englishText;
	}
	
	@Override
	public String getTranslationKey()
	{
		return "text.%s.%s".formatted(EI.ID, this.name().toLowerCase());
	}
}
