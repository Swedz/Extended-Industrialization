package net.swedz.extended_industrialization.machines.component.farmer.harvesting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.extended_industrialization.datamap.EnchantmentModule;

import java.util.Optional;

public record HarvestingContext(Level level, BlockPos pos, BlockState state, Optional<EnchantmentModule> enchantment)
{
	public ItemStack enchantedItem()
	{
		if(enchantment.isPresent())
		{
			var item = new ItemStack(Items.DIAMOND_AXE);
			var enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
			enchantments.set(enchantment.get().enchantment(level.registryAccess()), enchantment.get().level());
			item.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
			return item;
		}
		else
		{
			return ItemStack.EMPTY;
		}
	}
}
