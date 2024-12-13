package net.swedz.extended_industrialization.machines.recipe;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import aztech.modern_industrialization.machines.recipe.ProxyableMachineRecipeType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.datagen.api.RecipeHelper;

import java.util.List;

public final class ComposterMachineRecipeType extends ProxyableMachineRecipeType
{
	public ComposterMachineRecipeType(ResourceLocation id)
	{
		super(id);
	}
	
	private RecipeHolder<MachineRecipe> generate(Item item, float chance)
	{
		ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
		ResourceLocation id = EI.id("composter/generated/%s/%s_to_bonemeal".formatted(itemId.getNamespace(), itemId.getPath()));
		
		MachineRecipeBuilder recipe = new MachineRecipeBuilder(this, 2, 5 * 20);
		
		int amountNeeded = Math.max(1, (int) Math.floor((8 / chance) / 2));
		recipe.addItemInput(item, amountNeeded);
		recipe.addItemOutput(Items.BONE_MEAL, 2);
		
		return new RecipeHolder<>(id, RecipeHelper.getActualRecipe(recipe));
	}
	
	@Override
	protected void fillRecipeList(Level level, List<RecipeHolder<MachineRecipe>> recipeList)
	{
		recipeList.addAll(this.getManagerRecipes(level));
		
		for(Item item : BuiltInRegistries.ITEM)
		{
			float chance = ComposterBlock.getValue(item.getDefaultInstance());
			if(chance > 0f)
			{
				RecipeHolder<MachineRecipe> recipe = this.generate(item, chance);
				recipeList.add(recipe);
			}
		}
	}
}
