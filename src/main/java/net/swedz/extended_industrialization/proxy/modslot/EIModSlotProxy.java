package net.swedz.extended_industrialization.proxy.modslot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public interface EIModSlotProxy
{
	default boolean isLoaded()
	{
		return false;
	}
	
	default List<ItemStack> getContents(Player player, Predicate<ItemStack> filter)
	{
		return List.of();
	}
}
