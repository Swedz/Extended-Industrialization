package net.swedz.extended_industrialization.lootmodifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public final class AutoSmeltLootModifier extends LootModifier
{
	public static final MapCodec<AutoSmeltLootModifier> CODEC = RecordCodecBuilder.mapCodec((instance) ->
			LootModifier.codecStart(instance).apply(instance, AutoSmeltLootModifier::new));
	
	public AutoSmeltLootModifier(LootItemCondition[] conditions)
	{
		super(conditions);
	}
	
	/**
	 * <p>Smelt an item using the smelting recipe.</p>
	 *
	 * <p>Operates the same as {@link net.minecraft.world.level.storage.loot.functions.SmeltItemFunction}. That code is
	 * reused here like this so we don't get warning logs about failing to smelt items that cannot be smelted.</p>
	 *
	 * @param stack   the item to smelt
	 * @param context the loot context
	 * @return the smelted item or the input item if no smelting recipe exists for it
	 */
	private static ItemStack smelt(ItemStack stack, LootContext context)
	{
		var recipe = context.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), context.getLevel());
		if(recipe.isPresent())
		{
			var result = recipe.get().value().getResultItem(context.getLevel().registryAccess());
			if(!result.isEmpty())
			{
				return result.copyWithCount(stack.getCount() * result.getCount());
			}
		}
		return stack;
	}
	
	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> items, LootContext context)
	{
		ObjectArrayList<ItemStack> modifiedItems = new ObjectArrayList<>();
		for(var stack : items)
		{
			modifiedItems.add(smelt(stack, context));
		}
		return modifiedItems;
	}
	
	@Override
	public MapCodec<? extends IGlobalLootModifier> codec()
	{
		return CODEC;
	}
}
