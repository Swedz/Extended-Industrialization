package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.materials.Material;
import aztech.modern_industrialization.materials.MaterialRegistry;
import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.mi.hook.MIMachineHook;

import static aztech.modern_industrialization.materials.part.MIParts.*;

public final class AlloySmelterRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public AlloySmelterRecipesServerDatagenProvider(GatherDataEvent event)
	{
		super(event);
	}
	
	private static void addAlloySmelterRecipes(Material componentA, int amountA, Material componentB, int amountB, Material result, int amountResult, RecipeOutput output)
	{
		addMaterialMachineRecipe(
				result, "dust", MIMachineHook.RecipeTypes.ALLOY_SMELTER,
				4, 10 * 20,
				(r) -> r
						.addItemInput(componentA.getPart(DUST).getTaggedIngredient(), amountA, 1)
						.addItemInput(componentB.getPart(DUST).getTaggedIngredient(), amountB, 1)
						.addItemOutput(result.getPart(INGOT), amountResult),
				output
		);
		addMaterialMachineRecipe(
				result, "tiny_dust", MIMachineHook.RecipeTypes.ALLOY_SMELTER,
				4, 10 * 20,
				(r) -> r
						.addItemInput(componentA.getPart(TINY_DUST).getTaggedIngredient(), amountA * 9, 1)
						.addItemInput(componentB.getPart(TINY_DUST).getTaggedIngredient(), amountB * 9, 1)
						.addItemOutput(result.getPart(INGOT), amountResult),
				output
		);
		addMaterialMachineRecipe(
				result, "ingot", MIMachineHook.RecipeTypes.ALLOY_SMELTER,
				4, 10 * 20,
				(r) -> r
						.addItemInput(componentA.getPart(INGOT).getTaggedIngredient(), amountA, 1)
						.addItemInput(componentB.getPart(INGOT).getTaggedIngredient(), amountB, 1)
						.addItemOutput(result.getPart(INGOT), amountResult),
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
