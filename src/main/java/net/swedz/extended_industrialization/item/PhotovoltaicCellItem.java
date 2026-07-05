package net.swedz.extended_industrialization.item;

import aztech.modern_industrialization.api.energy.CableTier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.component.PhotovoltaicCell;

public final class PhotovoltaicCellItem extends Item
{
	public PhotovoltaicCellItem(Properties properties, CableTier tier, int euPerTick, int durationTicks)
	{
		super(properties
				.stacksTo(1)
				.durability(0)
				.component(EIComponents.PHOTOVOLTAIC_CELL, new PhotovoltaicCell(tier, euPerTick, durationTicks))
				.component(EIComponents.SOLAR_TICKS, 0));
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		var cell = stack.get(EIComponents.PHOTOVOLTAIC_CELL);
		return cell != null &&
			   !cell.lastsForever() &&
			   stack.getOrDefault(EIComponents.SOLAR_TICKS, 0) > 0;
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		int solarTicks = stack.getOrDefault(EIComponents.SOLAR_TICKS, 0);
		var cell = stack.get(EIComponents.PHOTOVOLTAIC_CELL);
		return Math.round(13 - (((float) solarTicks / cell.durationTicks()) * 13));
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		int solarTicks = stack.getOrDefault(EIComponents.SOLAR_TICKS, 0);
		var cell = stack.get(EIComponents.PHOTOVOLTAIC_CELL);
		int solarTicksRemaining = cell.durationTicks() - solarTicks;
		float hue = Math.max(0, (float) solarTicksRemaining / cell.durationTicks());
		return Mth.hsvToRgb(hue / 3, 1, 1);
	}
}
