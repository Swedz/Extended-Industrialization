package net.swedz.intothetwilight.datagen.api.object;

import aztech.modern_industrialization.machines.recipe.MIRecipeJson;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.netty.handler.codec.EncoderException;
import net.minecraft.Util;
import net.swedz.intothetwilight.datagen.api.DatagenOutputTarget;
import net.swedz.intothetwilight.datagen.api.DatagenProvider;

import java.lang.reflect.Field;

public class DatagenRecipeWrapper extends DatagenJsonObjectWrapper
{
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
	
	protected final String name;
	
	public DatagenRecipeWrapper(DatagenProvider provider, String path, String name)
	{
		super(provider, DatagenOutputTarget.DATA_PACK, (p) -> p.resolve("recipes").resolve(path).resolve(name + ".json"));
		this.name = name;
	}
	
	public void remove()
	{
		JsonArray conditions = new JsonArray();
		JsonObject falseCondition = new JsonObject();
		falseCondition.addProperty("type", "neoforge:false");
		conditions.add(falseCondition);
		this.get().add("neoforge:conditions", conditions);
	}
	
	public void modernIndustrializationMachineRecipe(MachineRecipeBuilder recipeBuilder)
	{
		MachineRecipe actualRecipe = getActualRecipe(recipeBuilder);
		MachineRecipeType recipeType = (MachineRecipeType) actualRecipe.getType();
		
		DataResult<JsonElement> result = recipeType.codec().encodeStart(JsonOps.INSTANCE, actualRecipe);
		JsonElement jsonElement = Util.getOrThrow(result, (error) -> new EncoderException("Failed to encode recipe '" + name + "': " + error));
		JsonObject json = jsonElement.getAsJsonObject();
		json.addProperty("type", recipeType.getId().toString());
		this.set(json);
	}
}
