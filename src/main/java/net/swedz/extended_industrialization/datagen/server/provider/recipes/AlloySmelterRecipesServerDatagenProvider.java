package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.materials.Material;
import aztech.modern_industrialization.materials.MaterialRegistry;
import com.google.common.collect.Lists;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.crafting.Ingredient;
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
	
	private static void addAlloySmelterRecipes(Material componentA, int amountA, Material componentB, int amountB, Material result, int amountResult, RecipeOutput output)
	{
		addMaterialMachineRecipe(
				result, "ingot", EIMachines.RecipeTypes.ALLOY_SMELTER,
				4, 10 * 20,
				(r) -> r
						.addItemInput(combine(componentA.getPart(DUST).getTaggedIngredient(), componentA.getPart(INGOT).getTaggedIngredient()), amountA, 1)
						.addItemInput(combine(componentB.getPart(DUST).getTaggedIngredient(), componentB.getPart(INGOT).getTaggedIngredient()), amountB, 1)
						.addItemOutput(result.getPart(INGOT), amountResult),
				output
		);
		addMaterialMachineRecipe(
				result, "nugget", EIMachines.RecipeTypes.ALLOY_SMELTER,
				4, 10 * 20,
				(r) -> r
						.addItemInput(combine(componentA.getPart(TINY_DUST).getTaggedIngredient(), componentA.getPart(NUGGET).getTaggedIngredient()), amountA * 9, 1)
						.addItemInput(combine(componentB.getPart(TINY_DUST).getTaggedIngredient(), componentB.getPart(NUGGET).getTaggedIngredient()), amountB * 9, 1)
						.addItemOutput(result.getPart(INGOT), amountResult),
				output
		);
		addMaterialMachineRecipe(
				result, "block", EIMachines.RecipeTypes.ALLOY_SMELTER,
				4, 10 * 20 * 9,
				(r) -> r
						.addItemInput(componentA.getPart(BLOCK).getTaggedIngredient(), amountA, 1)
						.addItemInput(componentB.getPart(BLOCK).getTaggedIngredient(), amountB, 1)
						.addItemOutput(result.getPart(BLOCK), amountResult),
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
