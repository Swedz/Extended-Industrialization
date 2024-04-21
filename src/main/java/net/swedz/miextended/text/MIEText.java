package net.swedz.miextended.text;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.swedz.miextended.MIExtended;

public enum MIEText
{
	ADDED_BY_MIE("(Added by MI Extended)"),
	SOLAR_EFFICIENCY("Solar Efficiency : %d %%"),
	WATER_PUMP_ENVIRONMENT_0("Invalid Pump Environment"),
	WATER_PUMP_ENVIRONMENT_1("Must be in Ocean or River biome."),
	FARMER_NOT_TILLING("Not Tilling"),
	FARMER_TILLING("Tilling"),
	FARMER_PLANTING_AS_NEEDED("As Needed"),
	FARMER_PLANTING_ALTERNATING_LINES("Alternating Lines"),
	FARMER_PLANTING_QUADRANTS("Quadrants");
	
	private final String englishText;
	
	MIEText(String englishText)
	{
		this.englishText = englishText;
	}
	
	public String englishText()
	{
		return englishText;
	}
	
	public String getTranslationKey()
	{
		return "text.%s.%s".formatted(MIExtended.ID, this.name().toLowerCase());
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
