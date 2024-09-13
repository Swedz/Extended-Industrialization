package net.swedz.extended_industrialization.recipe;

import com.google.common.collect.Lists;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.EIOtherRegistries;
import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.component.RainbowDataComponent;

import java.util.List;
import java.util.Optional;

public final class RainbowableDyeRecipe extends CustomRecipe
{
	public RainbowableDyeRecipe(CraftingBookCategory category)
	{
		super(category);
	}
	
	private Optional<Match> find(CraftingInput input)
	{
		ItemStack dyeableItem = ItemStack.EMPTY;
		List<DyeItem> dyeItems = Lists.newArrayList();
		
		for(int i = 0; i < input.size(); i++)
		{
			ItemStack item = input.getItem(i);
			if(!item.isEmpty())
			{
				if(item.is(ItemTags.DYEABLE) && item.is(EITags.Items.RAINBOW_DYEABLE))
				{
					if(!dyeableItem.isEmpty())
					{
						return Optional.empty();
					}
					dyeableItem = item;
				}
				else
				{
					if(!(item.getItem() instanceof DyeItem dyeItem))
					{
						return Optional.empty();
					}
					dyeItems.add(dyeItem);
				}
			}
		}
		
		return Optional.of(new Match(dyeableItem, dyeItems));
	}
	
	@Override
	public boolean matches(CraftingInput input, Level level)
	{
		return this.find(input).map(Match::valid).orElse(false);
	}
	
	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
	{
		return this.find(input).map(Match::assemble).orElse(ItemStack.EMPTY);
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return width * height >= 2;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return EIOtherRegistries.RAINBOWABLE_DYE_SERIALIZER.get();
	}
	
	private record Match(ItemStack dyeableItem, List<DyeItem> dyeItems)
	{
		public boolean valid()
		{
			return !dyeableItem.isEmpty() && !dyeItems.isEmpty();
		}
		
		public boolean rainbow()
		{
			return dyeItems.size() == 3 &&
				   dyeItems.containsAll(List.of(
						   (DyeItem) Items.RED_DYE,
						   (DyeItem) Items.LIME_DYE,
						   (DyeItem) Items.BLUE_DYE
				   ));
		}
		
		public ItemStack assemble()
		{
			if(this.valid())
			{
				if(this.rainbow())
				{
					ItemStack result = dyeableItem.copy();
					result.remove(DataComponents.DYED_COLOR);
					result.set(EIComponents.RAINBOW, new RainbowDataComponent(true, true));
					return result;
				}
				else
				{
					return DyedItemColor.applyDyes(dyeableItem, dyeItems);
				}
			}
			else
			{
				return ItemStack.EMPTY;
			}
		}
	}
}