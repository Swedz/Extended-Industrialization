package net.swedz.extended_industrialization.registry.items.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class PhotovoltaicCellItem extends Item
{
	private static final String SOLAR_TICKS_KEY = "solar_ticks";
	
	private final int euPerTick;
	private final int durationTicks;
	
	public PhotovoltaicCellItem(Properties properties, int euPerTick, int durationTicks)
	{
		super(properties.stacksTo(1).durability(0));
		this.euPerTick = euPerTick;
		this.durationTicks = durationTicks;
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
		CompoundTag tag = stack.getOrCreateTag();
		return tag.contains(SOLAR_TICKS_KEY, Tag.TAG_INT) ? tag.getInt(SOLAR_TICKS_KEY) : 0;
	}
	
	public int getSolarTicksRemaining(ItemStack stack)
	{
		return this.getDurationTicks() - this.getSolarTicks(stack);
	}
	
	public void incrementTick(ItemStack stack)
	{
		CompoundTag tag = stack.getOrCreateTag();
		int solarTicks = this.getSolarTicks(stack);
		tag.putInt(SOLAR_TICKS_KEY, solarTicks + 1);
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
}
