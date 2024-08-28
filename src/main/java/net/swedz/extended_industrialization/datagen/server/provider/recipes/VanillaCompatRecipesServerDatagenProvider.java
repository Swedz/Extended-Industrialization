package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EITags;

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
						.addFluidInput(EIFluids.HONEY, 1)
						.addItemInput(from, 1)
						.addItemOutput(to, 1),
				output
		);
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		addMachineRecipe(
				"vanilla_recipes/blast_furnace", "honey_from_block", MIMachineRecipeTypes.BLAST_FURNACE,
				2, 10 * 20,
				(r) -> r
						.addItemInput(Items.HONEY_BLOCK, 1)
						.addFluidOutput(EIFluids.HONEY, 1000),
				output
		);
		
		addMachineRecipe(
				"vanilla_recipes/blast_furnace", "crystallized_honey", MIMachineRecipeTypes.BLAST_FURNACE,
				2, 10 * 20,
				(r) -> r
						.addFluidInput(EIFluids.HONEY, 250)
						.addItemOutput(EIItems.CRYSTALLIZED_HONEY, 3),
				output
		);
		
		addMachineRecipe(
				"vanilla_recipes/blast_furnace", "netherite_dust_to_ingot", MIMachineRecipeTypes.BLAST_FURNACE,
				32, 40 * 20,
				(r) -> r
						.addItemInput(EITags.itemCommon("dusts/netherite"), 1)
						.addItemOutput(Items.NETHERITE_INGOT, 1),
				output
		);
		
		addMachineRecipe(
				"vanilla_recipes/blast_furnace", "blazing_essence", MIMachineRecipeTypes.BLAST_FURNACE,
				2, 10 * 20,
				(r) -> r
						.addItemInput(Items.BLAZE_POWDER, 1)
						.addFluidOutput(EIFluids.BLAZING_ESSENCE, 20),
				output
		);
		
		addMachineRecipe(
				"vanilla_recipes/macerator", "crystallized_honey_to_sugar", MIMachineRecipeTypes.MACERATOR,
				2, 5 * 20,
				(r) -> r
						.addItemInput(EIItems.CRYSTALLIZED_HONEY, 1)
						.addItemOutput(Items.SUGAR, 3),
				output
		);
		
		addMachineRecipe(
				"vanilla_recipes/macerator", "netherite_ingot_to_dust", MIMachineRecipeTypes.MACERATOR,
				2, 40 * 20,
				(r) -> r
						.addItemInput(Items.NETHERITE_INGOT, 1)
						.addItemOutput(EIItems.NETHERITE_DUST, 1),
				output
		);
		
		addMachineRecipe(
				"vanilla_recipes/vacuum_freezer", "honey_block", MIMachineRecipeTypes.VACUUM_FREEZER,
				16, 10 * 20,
				(r) -> r
						.addFluidInput(EIFluids.HONEY, 1000)
						.addItemOutput(Items.HONEY_BLOCK, 1),
				output
		);
		
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
