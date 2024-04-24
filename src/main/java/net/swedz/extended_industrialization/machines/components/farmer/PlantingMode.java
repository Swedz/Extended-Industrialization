package net.swedz.extended_industrialization.machines.components.farmer;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.swedz.extended_industrialization.machines.components.farmer.block.FarmerTile;
import net.swedz.extended_industrialization.text.EIText;

import java.util.List;
import java.util.function.BiFunction;

public enum PlantingMode
{
	AS_NEEDED(EIText.FARMER_PLANTING_AS_NEEDED.text(), false, (block, plantables) -> 0),
	ALTERNATING_LINES(EIText.FARMER_PLANTING_ALTERNATING_LINES.text(), true, (block, plantables) -> block.line() % plantables.size()),
	QUADRANTS(EIText.FARMER_PLANTING_QUADRANTS.text(), true, (block, plantables) -> block.quadrant() % plantables.size());
	
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
