package net.swedz.extended_industrialization.item.nanosuit.decoration;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;

public final class WingNanoDecoration implements NanoSuitDecoration
{
	@Override
	public boolean isActiveFor(NanoSuitArmorItem item, ItemStack stack)
	{
		return item.isQuantum();
	}
	
	@Override
	public ArmorItem.Type armorType()
	{
		return ArmorItem.Type.CHESTPLATE;
	}
}
