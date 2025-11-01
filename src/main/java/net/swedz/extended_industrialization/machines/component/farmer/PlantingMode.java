package net.swedz.extended_industrialization.machines.component.farmer;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerTile;

import java.util.List;
import java.util.function.BiFunction;

public enum PlantingMode
{
	AS_NEEDED(EI.text().farmerPlantingAsNeeded(), false, (block, plantables) -> 0),
	ALTERNATING_LINES(EI.text().farmerPlantingAlternatingLines(), true, (block, plantables) -> block.line() % plantables.size()),
	QUADRANTS(EI.text().farmerPlantingQuadrants(), true, (block, plantables) -> block.quadrant() % plantables.size());
	
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
