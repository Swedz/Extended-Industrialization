package net.swedz.extended_industrialization.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EIComponents;

public interface ToggleableItem
{
	default boolean getDefaultActivatedState()
	{
		return false;
	}
	
	default boolean isActivated(ItemStack stack)
	{
		return stack.getOrDefault(EIComponents.ACTIVATED.get(), this.getDefaultActivatedState());
	}
	
	default void setActivated(Player player, ItemStack stack, boolean activated)
	{
		stack.set(EIComponents.ACTIVATED.get(), activated);
	}
}
