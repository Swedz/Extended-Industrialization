package net.swedz.miextended.datagen.server.provider.recipes;

import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.materials.Material;
import aztech.modern_industrialization.materials.part.PartTemplate;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.swedz.miextended.datagen.api.DatagenOutputTarget;
import net.swedz.miextended.datagen.api.DatagenProvider;
import net.swedz.miextended.datagen.api.object.DatagenRecipeWrapper;

import java.nio.file.Path;
import java.util.function.Consumer;

import static aztech.modern_industrialization.materials.property.MaterialProperty.*;

public abstract class RecipesServerDatagenProvider extends DatagenProvider
{
	protected RecipesServerDatagenProvider(DataGenerator generator, String name, String modId)
	{
		super(generator, name, modId);
	}
	
	protected boolean hasPart(Material material, PartTemplate part)
	{
		return material.getParts().containsKey(part.key());
	}
	
	protected void removeRecipe(String path, String name)
	{
		DatagenRecipeWrapper remove = new DatagenRecipeWrapper(this, path, name);
		remove.remove();
		remove.write();
	}
	
	protected void removeRecipeDirectly(String path)
	{
		JsonObject json = new JsonObject();
		JsonArray conditions = new JsonArray();
		JsonObject falseCondition = new JsonObject();
		falseCondition.addProperty("type", "neoforge:false");
		conditions.add(falseCondition);
		json.add("neoforge:conditions", conditions);
		this.writeJsonForce(DatagenOutputTarget.DATA_PACK, Path.of(path + ".json"), json);
	}
	
	protected void addMachineRecipe(String path, String name, MachineRecipeType recipeType, int eu, int duration, Consumer<MachineRecipeBuilder> recipeBuilder)
	{
		DatagenRecipeWrapper wrapper = new DatagenRecipeWrapper(this, path, name);
		MachineRecipeBuilder recipe = new MachineRecipeBuilder(recipeType, eu, duration);
		recipeBuilder.accept(recipe);
		wrapper.modernIndustrializationMachineRecipe(recipe);
		wrapper.write();
	}
	
	protected void addMaterialMachineRecipe(Material material, String name, MachineRecipeType recipeType, int eu, int duration, Consumer<MachineRecipeBuilder> recipeBuilder)
	{
		this.addMachineRecipe("materials/%s/%s".formatted(material.name, recipeType.getPath()), name, recipeType, eu, duration, recipeBuilder);
	}
	
	protected void addMaterialMachineRecipe(Material material, String name, MachineRecipeType recipeType, int eu, Consumer<MachineRecipeBuilder> recipeBuilder)
	{
		this.addMaterialMachineRecipe(material, name, recipeType, eu, (int) (200 * material.get(HARDNESS).timeFactor), recipeBuilder);
	}
}
