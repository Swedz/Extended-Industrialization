package net.swedz.extended_industrialization.datagen.api.recipe;

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.swedz.extended_industrialization.datagen.api.MachineRecipeBuilderWrapper;
import net.swedz.extended_industrialization.datagen.api.RecipeHelper;

import java.util.List;

public class ShapelessRecipeBuilder extends RecipeBuilder<ShapelessRecipeBuilder>
{
	private final List<Ingredient> input = Lists.newArrayList();
	
	public ShapelessRecipeBuilder with(Ingredient ingredient)
	{
		input.add(ingredient);
		return this;
	}
	
	public ShapelessRecipeBuilder with(ItemLike item)
	{
		return this.with(Ingredient.of(item));
	}
	
	public ShapelessRecipeBuilder with(TagKey<Item> tag)
	{
		return this.with(Ingredient.of(tag));
	}
	
	public ShapelessRecipeBuilder with(String maybeTag)
	{
		return this.with(RecipeHelper.ingredient(maybeTag));
	}
	
	@Override
	public MachineRecipeBuilderWrapper exportToMachine(MachineRecipeType machine, int eu, int duration, int division)
	{
		if(result.getCount() % division != 0)
		{
			throw new IllegalArgumentException("Output must be divisible by division");
		}
		
		MachineRecipeBuilder assemblerRecipe = new MachineRecipeBuilder(machine, eu, duration).addItemOutput(result.getItem(), result.getCount() / division);
		for(Ingredient ingredient : input)
		{
			int count = 0;
			for(Ingredient other : input)
			{
				if(ingredient.equals(other))
				{
					count++;
				}
			}
			
			if(count % division != 0)
			{
				throw new IllegalArgumentException("Input must be divisible by division");
			}
			
			assemblerRecipe.addItemInput(ingredient, count / division, 1);
		}
		
		return new MachineRecipeBuilderWrapper(assemblerRecipe);
	}
	
	public MachineRecipeBuilderWrapper exportToPacker()
	{
		return this.exportToMachine(MIMachineRecipeTypes.PACKER, 2, 5 * 20, 1);
	}
	
	public MachineRecipeBuilderWrapper exportToUnpackerAndFlip()
	{
		return this.exportToMachine(MIMachineRecipeTypes.UNPACKER, 2, 5 * 20, 1).flip();
	}
	
	@Override
	public void validate()
	{
		if(input.size() == 0 || input.size() > 3)
		{
			throw new IllegalArgumentException("Invalid length " + input.size());
		}
	}
	
	@Override
	public Recipe<?> convert()
	{
		return new ShapelessRecipe(
				"",
				CraftingBookCategory.MISC,
				result,
				NonNullList.copyOf(input)
		);
	}
}
