package net.swedz.miextended.datagen.server.provider.recipes;

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.registry.fluids.MIEFluids;

public final class VanillaCompatRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public VanillaCompatRecipesServerDatagenProvider(GatherDataEvent event)
	{
		super(event);
	}
	
	private static void addHoneyWaxingRecipe(Item from, Item to, RecipeOutput output)
	{
		addMachineRecipe(
				"vanilla_recipes/mixer/waxing_with_honey", BuiltInRegistries.ITEM.getKey(from).getPath(), MIMachineRecipeTypes.MIXER,
				2, 5 * 20,
				(r) -> r
						.addFluidInput(MIEFluids.HONEY, 200)
						.addItemInput(from, 1)
						.addItemOutput(to, 1),
				output
		);
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		addHoneyWaxingRecipe(Items.COPPER_BLOCK, Items.WAXED_COPPER_BLOCK, output);
		addHoneyWaxingRecipe(Items.CUT_COPPER, Items.WAXED_CUT_COPPER, output);
		addHoneyWaxingRecipe(Items.CUT_COPPER_SLAB, Items.WAXED_CUT_COPPER_SLAB, output);
		addHoneyWaxingRecipe(Items.CUT_COPPER_STAIRS, Items.WAXED_CUT_COPPER_STAIRS, output);
		addHoneyWaxingRecipe(Items.EXPOSED_COPPER, Items.WAXED_EXPOSED_COPPER, output);
		addHoneyWaxingRecipe(Items.EXPOSED_CUT_COPPER, Items.WAXED_EXPOSED_CUT_COPPER, output);
		addHoneyWaxingRecipe(Items.EXPOSED_CUT_COPPER_SLAB, Items.WAXED_EXPOSED_CUT_COPPER_SLAB, output);
		addHoneyWaxingRecipe(Items.EXPOSED_CUT_COPPER_STAIRS, Items.WAXED_EXPOSED_CUT_COPPER_STAIRS, output);
		addHoneyWaxingRecipe(Items.WEATHERED_COPPER, Items.WAXED_WEATHERED_COPPER, output);
		addHoneyWaxingRecipe(Items.WEATHERED_CUT_COPPER, Items.WAXED_WEATHERED_CUT_COPPER, output);
		addHoneyWaxingRecipe(Items.WEATHERED_CUT_COPPER_SLAB, Items.WAXED_WEATHERED_CUT_COPPER_SLAB, output);
		addHoneyWaxingRecipe(Items.WEATHERED_CUT_COPPER_STAIRS, Items.WAXED_WEATHERED_CUT_COPPER_STAIRS, output);
		addHoneyWaxingRecipe(Items.OXIDIZED_COPPER, Items.WAXED_OXIDIZED_COPPER, output);
		addHoneyWaxingRecipe(Items.OXIDIZED_CUT_COPPER, Items.WAXED_OXIDIZED_CUT_COPPER, output);
		addHoneyWaxingRecipe(Items.OXIDIZED_CUT_COPPER_SLAB, Items.WAXED_OXIDIZED_CUT_COPPER_SLAB, output);
		addHoneyWaxingRecipe(Items.OXIDIZED_CUT_COPPER_STAIRS, Items.WAXED_OXIDIZED_CUT_COPPER_STAIRS, output);
	}
}
