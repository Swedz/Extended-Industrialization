package net.swedz.extended_industrialization.lootmodifier;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;

public record ItemStylizedPredicate() implements ItemSubPredicate
{
	public static final ItemStylizedPredicate INSTANCE = new ItemStylizedPredicate();
	
	public static final Codec<ItemStylizedPredicate> CODEC = Codec.unit(INSTANCE);
	
	private boolean isDyedIfPossible(ItemStack stack)
	{
		return !stack.is(ItemTags.DYEABLE) ||
			   stack.has(DataComponents.DYED_COLOR);
	}
	
	private boolean isTrimmedIfPossible(ItemStack stack)
	{
		return !stack.is(ItemTags.TRIMMABLE_ARMOR) ||
			   stack.has(DataComponents.TRIM);
	}
	
	@Override
	public boolean matches(ItemStack stack)
	{
		return this.isDyedIfPossible(stack) &&
			   this.isTrimmedIfPossible(stack);
	}
}
