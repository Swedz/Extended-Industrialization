package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.machines.recipe.MIRecipeJson;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.materials.Material;
import aztech.modern_industrialization.materials.part.PartTemplate;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;

import java.lang.reflect.Field;
import java.util.function.Consumer;

import static aztech.modern_industrialization.materials.property.MaterialProperty.*;

public abstract class RecipesServerDatagenProvider extends RecipeProvider
{
	protected RecipesServerDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput());
	}
	
	private static MachineRecipe getActualRecipe(MachineRecipeBuilder recipeBuilder)
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
	
	protected static boolean hasPart(Material material, PartTemplate part)
	{
		return material.getParts().containsKey(part.key());
	}
	
	protected static void addMachineRecipe(String path, String name, MachineRecipeType recipeType, int eu, int duration, Consumer<MachineRecipeBuilder> recipeBuilder, RecipeOutput output)
	{
		MachineRecipeBuilder recipe = new MachineRecipeBuilder(recipeType, eu, duration);
		recipeBuilder.accept(recipe);
		output.accept(EI.id(path + "/" + name), getActualRecipe(recipe), null);
	}
	
	protected static void addMaterialMachineRecipe(Material material, String name, MachineRecipeType recipeType, int eu, int duration, Consumer<MachineRecipeBuilder> recipeBuilder, RecipeOutput output)
	{
		addMachineRecipe("materials/%s/%s".formatted(material.name, recipeType.getPath()), name, recipeType, eu, duration, recipeBuilder, output);
	}
	
	protected static void addMaterialMachineRecipe(Material material, String name, MachineRecipeType recipeType, int eu, Consumer<MachineRecipeBuilder> recipeBuilder, RecipeOutput output)
	{
		addMaterialMachineRecipe(material, name, recipeType, eu, (int) (200 * material.get(HARDNESS).timeFactor), recipeBuilder, output);
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
