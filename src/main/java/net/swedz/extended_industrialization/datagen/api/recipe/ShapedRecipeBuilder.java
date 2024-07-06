package net.swedz.extended_industrialization.datagen.api.recipe;

import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import net.swedz.extended_industrialization.datagen.api.MachineRecipeBuilderWrapper;
import net.swedz.extended_industrialization.datagen.api.RecipeHelper;

import java.util.List;
import java.util.Map;

public class ShapedRecipeBuilder extends RecipeBuilder<ShapedRecipeBuilder>
{
	private final Map<Character, Ingredient> key     = Maps.newHashMap();
	private final List<String>               pattern = Lists.newArrayList();
	
	public ShapedRecipeBuilder define(char key, Ingredient ingredient)
	{
		if(this.key.put(key, ingredient) != null)
		{
			throw new IllegalStateException("Key mapping is already registered: " + key);
		}
		return this;
	}
	
	public ShapedRecipeBuilder define(char key, ItemLike item)
	{
		return this.define(key, Ingredient.of(item));
	}
	
	public ShapedRecipeBuilder define(char key, TagKey<Item> tag)
	{
		return this.define(key, Ingredient.of(tag));
	}
	
	public ShapedRecipeBuilder define(char key, String maybeTag)
	{
		return this.define(key, RecipeHelper.ingredient(maybeTag));
	}
	
	public ShapedRecipeBuilder pattern(String line)
	{
		pattern.add(line);
		return this;
	}
	
	@Override
	public void validate()
	{
		if(pattern.size() == 0 || pattern.size() > 3)
		{
			throw new IllegalArgumentException("Invalid length " + pattern.size());
		}
		for(String string : pattern)
		{
			if(string.length() != pattern.get(0).length())
			{
				throw new IllegalArgumentException("Pattern length mismatch: " + string.length() + ", expected " + pattern.get(0).length());
			}
		}
		for(String string : pattern)
		{
			for(int i = 0; i < string.length(); ++i)
			{
				if(string.charAt(i) != ' ' && !key.containsKey(string.charAt(i)))
				{
					throw new IllegalArgumentException("Key " + string.charAt(i) + " is missing a mapping.");
				}
			}
		}
		for(char c : key.keySet())
		{
			boolean ok = false;
			for(String string : pattern)
			{
				for(int i = 0; i < string.length(); ++i)
				{
					if(string.charAt(i) == c)
					{
						ok = true;
						break;
					}
				}
			}
			if(!ok)
			{
				throw new IllegalArgumentException("Key mapping '" + c + "' is not used in the pattern.");
			}
		}
	}
	
	@Override
	public MachineRecipeBuilderWrapper exportToMachine(MachineRecipeType machine, int eu, int duration, int division)
	{
		if(result.getCount() % division != 0)
		{
			throw new IllegalArgumentException("Output must be divisible by division");
		}
		
		MachineRecipeBuilder assemblerRecipe = new MachineRecipeBuilder(machine, eu, duration).addItemOutput(result.getItem(), result.getCount() / division);
		for(Map.Entry<Character, Ingredient> entry : key.entrySet())
		{
			int count = 0;
			for(String row : pattern)
			{
				for(char c : row.toCharArray())
				{
					if(c == entry.getKey())
					{
						count++;
					}
				}
			}
			
			if(count % division != 0)
			{
				throw new IllegalArgumentException("Input must be divisible by division");
			}
			
			assemblerRecipe.addItemInput(entry.getValue(), count / division, 1);
		}
		
		return new MachineRecipeBuilderWrapper(assemblerRecipe);
	}
	
	@Override
	public Recipe<?> convert()
	{
		return new ShapedRecipe(
				"",
				CraftingBookCategory.MISC,
				ShapedRecipePattern.of(key, pattern),
				result
		);
	}
}
