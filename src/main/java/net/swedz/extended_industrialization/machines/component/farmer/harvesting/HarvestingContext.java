package net.swedz.extended_industrialization.machines.component.farmer.harvesting;

import aztech.modern_industrialization.api.energy.CableTier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.extended_industrialization.datamap.EnchantmentModule;

import java.util.Optional;

public record HarvestingContext(
		Level level, BlockPos pos, BlockState state,
		Optional<EnchantmentModule> enchantment, CableTier tier
)
{
	public ItemStack enchantedItem()
	{
		var item = ItemStack.EMPTY;
		if(enchantment.isPresent())
		{
			item = new ItemStack(Items.DIAMOND_AXE);
			enchantment.get().applyEnchantment(level.registryAccess(), item, tier);
		}
		return item;
	}
}
