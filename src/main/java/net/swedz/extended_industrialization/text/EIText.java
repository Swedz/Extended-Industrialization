package net.swedz.extended_industrialization.text;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.swedz.extended_industrialization.EI;

public enum EIText
{
	SOLAR_EFFICIENCY("Solar Efficiency : %d %%"),
	CALCIFICATION_PERCENTAGE("Calcification : %d %%"),
	SOLAR_BOILER_CALCIFICATION("Will calcify and lose efficiency over time to a minimum of %s efficiency. Breaking and placing the boiler again will reset this."),
	WATER_PUMP_ENVIRONMENT_0("Invalid Pump Environment"),
	WATER_PUMP_ENVIRONMENT_1("Must be in Ocean or River biome."),
	FARMER_NOT_TILLING("Not Tilling"),
	FARMER_TILLING("Tilling"),
	FARMER_PLANTING_AS_NEEDED("As Needed"),
	FARMER_PLANTING_ALTERNATING_LINES("Alternating Lines"),
	FARMER_PLANTING_QUADRANTS("Quadrants"),
	MULCH_GANG_FOR_LIFE_0("I love mulch!"),
	MULCH_GANG_FOR_LIFE_1("Mulch is my favorite food!"),
	PROCESSING_ARRAY_SIZE("Machines: %d"),
	PROCESSING_ARRAY_MACHINE_INPUT("Insert electric crafting machines to run in parallel."),
	FLUID_FERTILIZERS("Fluid Fertilizers"),
	FLUID_FERTILIZERS_TIME("Cycle Time : %.1fs"),
	FLUID_FERTILIZERS_CONSUMES("Consumes : %dmb"),
	MULTIBLOCK_SHAPE_VALID("Shape Valid"),
	MULTIBLOCK_SHAPE_INVALID("Shape Invalid"),
	MULTIBLOCK_STATUS_ACTIVE("Status : Active"),
	MULTIBLOCK_STATUS_INACTIVE("Status : Inactive"),
	COILS_LEF_TIER("Runs LEF in batches of up to %d at %s the EU cost"),
	MACHINE_BATCHER_RECIPE("Can run %s recipes in batches"),
	MACHINE_BATCHER_RECIPE_FURNACE("Furnace"),
	MACHINE_BATCHER_RECIPE_MACERATOR("Macerator"),
	MACHINE_BATCHER_SIZE_AND_COST("Runs in batches of up to %d at %s the EU cost"),
	MACHINE_BATCHER_COILS("Batch size is determined by coil used"),
	MACHINE_CHAINER_CONNECTED_MACHINES("Connected Machines : %d / %d"),
	CONFIGURATION_PANEL_TITLE("Configure Machine"),
	CONFIGURATION_PANEL_DESCRIPTION("Click to open configuration panel."),
	UNIVERSAL_TRANSFORMER_FROM_TIER_INPUT("Casing for cable tier to convert from (LV by default)"),
	UNIVERSAL_TRANSFORMER_TO_TIER_INPUT("Casing for cable tier to convert to (LV by default)");
	
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
