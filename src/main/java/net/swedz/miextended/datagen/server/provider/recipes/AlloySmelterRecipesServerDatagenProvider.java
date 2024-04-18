package net.swedz.miextended.datagen.server.provider.recipes;

import aztech.modern_industrialization.materials.Material;
import aztech.modern_industrialization.materials.MaterialRegistry;
import net.minecraft.data.DataGenerator;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.mi.hook.MIMachineHook;

import static aztech.modern_industrialization.materials.part.MIParts.*;

public final class AlloySmelterRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public AlloySmelterRecipesServerDatagenProvider(DataGenerator generator)
	{
		super(generator, "MI Extended Datagen/Server/Recipes/Alloy Smelter", MIExtended.ID);
	}
	
	private void addAlloySmelterRecipes(Material componentA, int amountA, Material componentB, int amountB, Material result, int amountResult)
	{
		this.addMaterialMachineRecipe(result, "dust", MIMachineHook.ALLOY_SMELTER, 4, 10 * 20, (r) -> r
				.addItemInput(componentA.getPart(DUST).getTaggedIngredient(), amountA, 1)
				.addItemInput(componentB.getPart(DUST).getTaggedIngredient(), amountB, 1)
				.addItemOutput(result.getPart(INGOT), amountResult));
		this.addMaterialMachineRecipe(result, "tiny_dust", MIMachineHook.ALLOY_SMELTER, 4, 10 * 20, (r) -> r
				.addItemInput(componentA.getPart(TINY_DUST).getTaggedIngredient(), amountA * 9, 1)
				.addItemInput(componentB.getPart(TINY_DUST).getTaggedIngredient(), amountB * 9, 1)
				.addItemOutput(result.getPart(INGOT), amountResult));
		this.addMaterialMachineRecipe(result, "ingot", MIMachineHook.ALLOY_SMELTER, 4, 10 * 20, (r) -> r
				.addItemInput(componentA.getPart(INGOT).getTaggedIngredient(), amountA, 1)
				.addItemInput(componentB.getPart(INGOT).getTaggedIngredient(), amountB, 1)
				.addItemOutput(result.getPart(INGOT), amountResult));
	}
	
	@Override
	public void run()
	{
		this.addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("tin"), 1,
				MaterialRegistry.getMaterial("copper"), 3,
				MaterialRegistry.getMaterial("bronze"), 4
		);
		this.addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("lead"), 1,
				MaterialRegistry.getMaterial("antimony"), 1,
				MaterialRegistry.getMaterial("battery_alloy"), 2
		);
		this.addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("copper"), 1,
				MaterialRegistry.getMaterial("nickel"), 1,
				MaterialRegistry.getMaterial("cupronickel"), 2
		);
		this.addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("iron"), 2,
				MaterialRegistry.getMaterial("nickel"), 1,
				MaterialRegistry.getMaterial("invar"), 3
		);
		this.addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("gold"), 1,
				MaterialRegistry.getMaterial("silver"), 1,
				MaterialRegistry.getMaterial("electrum"), 2
		);
	}
}