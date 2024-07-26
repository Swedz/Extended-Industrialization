package net.swedz.extended_industrialization.datagen.api;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

public record MachineRecipeBuilderWrapper(MachineRecipeBuilder recipe)
{
	public MachineRecipeBuilderWrapper flip()
	{
		MachineRecipe actualRecipe = RecipeHelper.getActualRecipe(recipe);
		
		MachineRecipeBuilder inversedRecipe = new MachineRecipeBuilder((MachineRecipeType) actualRecipe.getType(), actualRecipe.eu, actualRecipe.duration);
		
		for(MachineRecipe.ItemInput itemInput : actualRecipe.itemInputs)
		{
			inversedRecipe.addItemOutput(itemInput.ingredient().getItems()[0].getItem(), itemInput.amount(), itemInput.probability());
		}
		
		for(MachineRecipe.FluidInput fluidInput : actualRecipe.fluidInputs)
		{
			inversedRecipe.addFluidOutput(fluidInput.fluid(), (int) fluidInput.amount(), fluidInput.probability());
		}
		
		for(MachineRecipe.ItemOutput itemOutput : actualRecipe.itemOutputs)
		{
			inversedRecipe.addItemInput(itemOutput.variant().getItem(), itemOutput.amount(), itemOutput.probability());
		}
		
		for(MachineRecipe.FluidOutput fluidOutput : actualRecipe.fluidOutputs)
		{
			inversedRecipe.addFluidInput(fluidOutput.fluid(), (int) fluidOutput.amount(), fluidOutput.probability());
		}
		
		return new MachineRecipeBuilderWrapper(inversedRecipe);
	}
	
	public void offerTo(RecipeOutput output, ResourceLocation location)
	{
		output.accept(location, RecipeHelper.getActualRecipe(recipe), null);
	}
}
