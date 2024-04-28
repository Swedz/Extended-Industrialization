package net.swedz.extended_industrialization.text;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.swedz.extended_industrialization.EI;

public enum EIText
{
	SOLAR_EFFICIENCY("Solar Efficiency : %d %%"),
	WATER_PUMP_ENVIRONMENT_0("Invalid Pump Environment"),
	WATER_PUMP_ENVIRONMENT_1("Must be in Ocean or River biome."),
	FARMER_NOT_TILLING("Not Tilling"),
	FARMER_TILLING("Tilling"),
	FARMER_PLANTING_AS_NEEDED("As Needed"),
	FARMER_PLANTING_ALTERNATING_LINES("Alternating Lines"),
	FARMER_PLANTING_QUADRANTS("Quadrants"),
	MULCH_GANG_FOR_LIFE_0("I love mulch!"),
	MULCH_GANG_FOR_LIFE_1("Mulch is my favorite food!"),
	ADVANCED_ASSEMBLER_SIZE("Machines: %d"),
	FLUID_FERTILIZERS("Fluid Fertilizers"),
	FLUID_FERTILIZERS_TIME("Cycle Time : %.1fs"),
	FLUID_FERTILIZERS_CONSUMES("Consumes : %dmb"),
	MULTIBLOCK_SHAPE_VALID("Shape Valid"),
	MULTIBLOCK_SHAPE_INVALID("Shape Invalid"),
	MULTIBLOCK_STATUS_ACTIVE("Status : Active"),
	MULTIBLOCK_STATUS_INACTIVE("Status : Inactive");
	
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
