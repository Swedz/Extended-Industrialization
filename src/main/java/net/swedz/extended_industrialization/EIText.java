package net.swedz.extended_industrialization;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum EIText
{
	CALCIFICATION_PERCENTAGE("Calcification : %d %%"),
	COILS_LEF_TIER("Runs LEF in batches of up to %d at %s the EU cost."),
	FARMER_NOT_TILLING("Not Tilling"),
	FARMER_PLANTING_ALTERNATING_LINES("Alternating Lines"),
	FARMER_PLANTING_AS_NEEDED("As Needed"),
	FARMER_PLANTING_QUADRANTS("Quadrants"),
	FARMER_TILLING("Tilling"),
	FLUID_FERTILIZERS("Fluid Fertilizers"),
	FLUID_FERTILIZERS_CONSUMES("Consumes : %dmb"),
	FLUID_FERTILIZERS_TIME("Cycle Time : %.1fs"),
	GENERATING_EU_PER_TICK("Generating : %d EU/t"),
	MACHINE_BATCHER_COILS("Batch size and cost is determined by coil used."),
	MACHINE_CHAINER_CONNECTED_MACHINES("Connected Machines : %d / %d"),
	MACHINE_CONFIG_CARD_APPLY_FAILED("Failed to apply machine configuration to machine."),
	MACHINE_CONFIG_CARD_APPLY_SUCCESS("Applied machine configuration to machine from card."),
	MACHINE_CONFIG_CARD_CLEAR("Cleared machine configuration from card."),
	MACHINE_CONFIG_CARD_CONFIGURED("Configured (%s)"),
	MACHINE_CONFIG_CARD_HELP_1("Machine configuration:"),
	MACHINE_CONFIG_CARD_HELP_2("- Shift right-click a machine to save its"),
	MACHINE_CONFIG_CARD_HELP_3("  settings in the card."),
	MACHINE_CONFIG_CARD_HELP_4("- Right-click a machine to apply the settings"),
	MACHINE_CONFIG_CARD_HELP_5("  from the card."),
	MACHINE_CONFIG_CARD_HELP_6("Clear using shift-right click on air."),
	MACHINE_CONFIG_CARD_SAVE("Saved machine configuration to card."),
	MULCH_GANG_FOR_LIFE_0("I love mulch!"),
	MULCH_GANG_FOR_LIFE_1("Mulch is my favorite food!"),
	PHOTOVOLTAIC_CELL_EU("Will produce up to %s when placed in a Solar Panel."),
	PHOTOVOLTAIC_CELL_REMAINING_OPERATION_TIME("Remaining Operation Time : %s"),
	PHOTOVOLTAIC_CELL_REMAINING_OPERATION_TIME_MINUTES("Remaining Operation Time : %s minute(s)"),
	PROCESSING_ARRAY_BATCH_SIZE("Batch size is determined by the amount of machines provided to it."),
	PROCESSING_ARRAY_EU_COST_MULTIPLIER("Runs at %s the EU cost."),
	PROCESSING_ARRAY_MACHINE_INPUT("Insert electric crafting machines to run in parallel."),
	PROCESSING_ARRAY_RECIPE("Can run recipes of any single block electric crafting machine provided to it in batches."),
	PROCESSING_ARRAY_SIZE("Machines: %d"),
	SOLAR_BOILER_CALCIFICATION("Will calcify and lose efficiency over time to a minimum of %s efficiency when not using %s. Breaking and placing the boiler again will reset calcification."),
	SOLAR_EFFICIENCY("Solar Efficiency : %d %%"),
	SOLAR_PANEL_DISTILLED_WATER("By supplying %s to the Solar Panel, the Photovoltaic Cell in its slot will last 2x as long!"),
	SOLAR_PANEL_PHOTOVOLTAIC_CELL("To produce energy, the Solar Panel needs a matching tier Photovoltaic Cell in its inventory."),
	SOLAR_PANEL_SUNLIGHT("Energy generation rates are determined by how high the sun is in the sky and if the sky is visible."),
	STEAM_CHAINSAW_1("1) Right click still or flowing water to fill."),
	STEAM_CHAINSAW_2("2) Place fuel inside the chainsaw (right click)."),
	STEAM_CHAINSAW_3("3) Enjoy Silk Touch."),
	STEAM_CHAINSAW_4("4) Toggle Silk Touch with shift-right click."),
	UNIVERSAL_TRANSFORMER_FROM_TIER_INPUT("Casing for cable tier to convert from (LV by default)."),
	UNIVERSAL_TRANSFORMER_TO_TIER_INPUT("Casing for cable tier to convert to (LV by default).");
	
	private final String englishText;
	
	EIText(String englishText)
	{
		this.englishText = englishText;
	}
	
	public String englishText()
	{
		return englishText;
	}
	
	public String getTranslationKey()
	{
		return "text.%s.%s".formatted(EI.ID, this.name().toLowerCase());
	}
	
	public MutableComponent text()
	{
		return Component.translatable(this.getTranslationKey());
	}
	
	public MutableComponent text(Object... args)
	{
		return Component.translatable(this.getTranslationKey(), args);
	}
}
