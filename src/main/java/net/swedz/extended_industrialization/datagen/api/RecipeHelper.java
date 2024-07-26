package net.swedz.extended_industrialization.datagen.api;

import aztech.modern_industrialization.machines.recipe.MIRecipeJson;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;

import java.lang.reflect.Field;

public final class RecipeHelper
{
	public static MachineRecipe getActualRecipe(MachineRecipeBuilder recipeBuilder)
	{
		try
		{
			Field fieldRecipe = MIRecipeJson.class.getDeclaredField("recipe");
			fieldRecipe.setAccessible(true);
			MachineRecipe actualRecipe = (MachineRecipe) fieldRecipe.get(recipeBuilder);
			fieldRecipe.setAccessible(false);
			return actualRecipe;
		}
		catch (NoSuchFieldException | IllegalAccessException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	public static Ingredient ingredient(String maybeTag)
	{
		if(maybeTag.startsWith("#"))
		{
			return Ingredient.of(ItemTags.create(ResourceLocation.parse(maybeTag.substring(1))));
		}
		else
		{
			return Ingredient.of(BuiltInRegistries.ITEM.get(ResourceLocation.parse(maybeTag)));
		}
	}
}
