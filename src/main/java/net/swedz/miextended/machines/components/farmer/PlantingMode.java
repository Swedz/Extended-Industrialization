package net.swedz.miextended.machines.components.farmer;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.swedz.miextended.machines.components.farmer.block.FarmerTile;
import net.swedz.miextended.text.MIEText;

import java.util.List;
import java.util.function.BiFunction;

public enum PlantingMode
{
	AS_NEEDED(MIEText.FARMER_PLANTING_AS_NEEDED.text(), false, (block, plantables) -> 0),
	ALTERNATING_LINES(MIEText.FARMER_PLANTING_ALTERNATING_LINES.text(), true, (block, plantables) -> block.line() % plantables.size());
	
	private final Component textComponent;
	
	private final boolean includeEmptyStacks;
	
	private final BiFunction<FarmerTile, List<PlantableConfigurableItemStack>, Integer> index;
	
	PlantingMode(Component textComponent, boolean includeEmptyStacks, BiFunction<FarmerTile, List<PlantableConfigurableItemStack>, Integer> index)
	{
		this.textComponent = textComponent;
		this.includeEmptyStacks = includeEmptyStacks;
		this.index = index;
	}
	
	public Component textComponent()
	{
		return textComponent;
	}
	
	public boolean includeEmptyStacks()
	{
		return includeEmptyStacks;
	}
	
	public int index(FarmerTile tile, List<PlantableConfigurableItemStack> plantables)
	{
		return index.apply(tile, plantables);
	}
	
	public static PlantingMode fromIndex(int index)
	{
		return values()[Mth.clamp(index, 0, values().length - 1)];
	}
	
	public static PlantingMode fromName(String name)
	{
		for(PlantingMode mode : values())
		{
			if(mode.name().equals(name))
			{
				return mode;
			}
		}
		return null;
	}
}
