package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.materials.Material;
import aztech.modern_industrialization.materials.MaterialRegistry;
import com.google.common.collect.Lists;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EIMachines;

import java.util.List;

import static aztech.modern_industrialization.materials.part.MIParts.*;

public final class AlloySmelterRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public AlloySmelterRecipesServerDatagenProvider(GatherDataEvent event)
	{
		super(event);
	}
	
	private static Ingredient combine(Ingredient... ingredients)
	{
		List<Ingredient.Value> values = Lists.newArrayList();
		for(var ingredient : ingredients)
		{
			values.addAll(List.of(ingredient.getValues()));
		}
		return Ingredient.fromValues(values.stream());
	}
	
	private static void addAlloySmelterRecipes(
			String name,
			Ingredient tinyDustA,
			Ingredient nuggetA,
			Ingredient dustA,
			Ingredient ingotA,
			Ingredient blockA,
			int amountA,
			Ingredient tinyDustB,
			Ingredient nuggetB,
			Ingredient dustB,
			Ingredient ingotB,
			Ingredient blockB,
			int amountB,
			ItemLike resultIngot,
			int amountResult,
			RecipeOutput output
	)
	{
		var path = "materials/%s/%s".formatted(name, EIMachines.RecipeTypes.ALLOY_SMELTER.getPath());
		addMachineRecipe(
				path,
				"nugget",
				EIMachines.RecipeTypes.ALLOY_SMELTER,
				4,
				10 * 20,
				(r) -> r
						.addItemInput(combine(tinyDustA, nuggetA), amountA * 9, 1)
						.addItemInput(combine(tinyDustB, nuggetB), amountB * 9, 1)
						.addItemOutput(resultIngot, amountResult),
				output
		);
		addMachineRecipe(
				path,
				"ingot",
				EIMachines.RecipeTypes.ALLOY_SMELTER,
				4,
				10 * 20,
				(r) -> r
						.addItemInput(combine(dustA, ingotA), amountA, 1)
						.addItemInput(combine(dustB, ingotB), amountB, 1)
						.addItemOutput(resultIngot, amountResult),
				output
		);
		addMachineRecipe(
				path,
				"block",
				EIMachines.RecipeTypes.ALLOY_SMELTER,
				4,
				10 * 20 * 9,
				(r) -> r
						.addItemInput(blockA, amountA, 1)
						.addItemInput(blockB, amountB, 1)
						.addItemOutput(resultIngot, amountResult * 9),
				output
		);
	}
	
	private static void addAlloySmelterRecipes(Material componentA, int amountA, Material componentB, int amountB, Material result, int amountResult, RecipeOutput output)
	{
		addAlloySmelterRecipes(
				result.name,
				componentA.getPart(TINY_DUST).getTaggedIngredient(),
				componentA.getPart(NUGGET).getTaggedIngredient(),
				componentA.getPart(DUST).getTaggedIngredient(),
				componentA.getPart(INGOT).getTaggedIngredient(),
				componentA.getPart(BLOCK).getTaggedIngredient(),
				amountA,
				componentB.getPart(TINY_DUST).getTaggedIngredient(),
				componentB.getPart(NUGGET).getTaggedIngredient(),
				componentB.getPart(DUST).getTaggedIngredient(),
				componentB.getPart(INGOT).getTaggedIngredient(),
				componentB.getPart(BLOCK).getTaggedIngredient(),
				amountB,
				result.getPart(INGOT),
				amountResult,
				output
		);
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("tin"), 1,
				MaterialRegistry.getMaterial("copper"), 3,
				MaterialRegistry.getMaterial("bronze"), 4,
				output
		);
		addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("lead"), 1,
				MaterialRegistry.getMaterial("antimony"), 1,
				MaterialRegistry.getMaterial("battery_alloy"), 2,
				output
		);
		addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("copper"), 1,
				MaterialRegistry.getMaterial("nickel"), 1,
				MaterialRegistry.getMaterial("cupronickel"), 2,
				output
		);
		addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("iron"), 2,
				MaterialRegistry.getMaterial("nickel"), 1,
				MaterialRegistry.getMaterial("invar"), 3,
				output
		);
		addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("gold"), 1,
				MaterialRegistry.getMaterial("silver"), 1,
				MaterialRegistry.getMaterial("electrum"), 2,
				output
		);
	}
}
