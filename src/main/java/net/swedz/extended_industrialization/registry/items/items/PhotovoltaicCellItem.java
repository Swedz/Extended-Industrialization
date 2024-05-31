package net.swedz.extended_industrialization.registry.items.items;

import aztech.modern_industrialization.api.energy.CableTier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.registry.attachments.EIAttachments;

public final class PhotovoltaicCellItem extends Item
{
	private final CableTier tier;
	private final int       euPerTick;
	private final int       durationTicks;
	
	public PhotovoltaicCellItem(Properties properties, CableTier tier, int euPerTick, int durationTicks)
	{
		super(properties.stacksTo(1).durability(0));
		this.tier = tier;
		this.euPerTick = euPerTick;
		this.durationTicks = durationTicks;
	}
	
	public CableTier getTier()
	{
		return tier;
	}
	
	public int getEuPerTick()
	{
		return euPerTick;
	}
	
	public int getDurationTicks()
	{
		return durationTicks;
	}
	
	public boolean lastsForever()
	{
		return this.getDurationTicks() == 0;
	}
	
	public int getSolarTicks(ItemStack stack)
	{
		return stack.getData(EIAttachments.SOLAR_TICKS);
	}
	
	public int getSolarTicksRemaining(ItemStack stack)
	{
		return this.getDurationTicks() - this.getSolarTicks(stack);
	}
	
	public void incrementTick(ItemStack stack)
	{
		int solarTicks = this.getSolarTicks(stack) + 1;
		if(solarTicks > this.getDurationTicks())
		{
			return;
		}
		stack.setData(EIAttachments.SOLAR_TICKS, solarTicks);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		int solarTicks = this.getSolarTicks(stack);
		return solarTicks > 0;
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13 - (((float) this.getSolarTicks(stack) / this.getDurationTicks()) * 13));
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		float hue = Math.max(0, (float) this.getSolarTicksRemaining(stack) / this.getDurationTicks());
		return Mth.hsvToRgb(hue / 3, 1, 1);
	}
	
	public static long calculateTotalEuProduced(int euPerTick, int durationTicks)
	{
		int dayLength = 12000;
		int energyProduced = 0;
		for(int tick = 0; tick <= durationTicks; tick++)
		{
			int time = tick % dayLength;
			long timeFromNoon = Math.abs(6000 - time);
			float efficiency = 0;
			if(time >= 4000 && time <= 8000)
			{
				efficiency = 1;
			}
			else if(time < 4000)
			{
				efficiency = (-1f / 16000000f) * time * time + (1f / 2000f) * time;
			}
			else if(time > 8000)
			{
				efficiency = (-1f / 16000000f) * time * time + (1f / 1000f) * time - 3f;
			}
			energyProduced += (euPerTick * efficiency);
		}
		return energyProduced;
	}
}
