package net.swedz.extended_industrialization.api;

import net.minecraft.world.item.ItemStack;

public interface ComponentStackHolder
{
	ItemStack getStack();
	
	void setStack(ItemStack stack);
}
