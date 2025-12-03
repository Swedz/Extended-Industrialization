package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.materials.Material;
import aztech.modern_industrialization.materials.part.PartTemplate;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.tesseract.neoforge.compat.mi.recipe.MIMachineRecipeBuilder;

import java.util.function.Consumer;

import static aztech.modern_industrialization.materials.property.MaterialProperty.*;

public abstract class RecipesServerDatagenProvider extends RecipeProvider
{
	protected RecipesServerDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider());
	}
	
	protected static boolean hasPart(Material material, PartTemplate part)
	{
		return material.getParts().containsKey(part.key());
	}
	
	protected static void addMachineRecipe(String path, String name, MachineRecipeType recipeType, int eu, int duration, Consumer<MIMachineRecipeBuilder> recipeBuilder, RecipeOutput output)
	{
		var recipe = new MIMachineRecipeBuilder(recipeType, eu, duration);
		recipeBuilder.accept(recipe);
		recipe.offerTo(output, EI.id(path + "/" + name));
	}
	
	protected static void addMaterialMachineRecipe(Material material, String name, MachineRecipeType recipeType, int eu, int duration, Consumer<MIMachineRecipeBuilder> recipeBuilder, RecipeOutput output)
	{
		addMachineRecipe("materials/%s/%s".formatted(material.name, recipeType.getPath()), name, recipeType, eu, duration, recipeBuilder, output);
	}
	
	protected static void addMaterialMachineRecipe(Material material, String name, MachineRecipeType recipeType, int eu, Consumer<MIMachineRecipeBuilder> recipeBuilder, RecipeOutput output)
	{
		addMaterialMachineRecipe(material, name, recipeType, eu, (int) (200 * material.get(HARDNESS).timeFactor), recipeBuilder, output);
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
