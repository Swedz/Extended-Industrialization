package net.swedz.miextended.datagen.server.provider.recipes;

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.fluids.MIEFluids;

public final class VanillaCompatRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public VanillaCompatRecipesServerDatagenProvider(DataGenerator generator)
	{
		super(generator, "MI Extended Datagen/Server/Recipes/Vanilla Compat", MIExtended.ID);
	}
	
	private void addHoneyWaxingRecipe(Item from, Item to)
	{
		this.addMachineRecipe("vanilla_recipes/mixer/waxing_with_honey", BuiltInRegistries.ITEM.getKey(from).getPath(), MIMachineRecipeTypes.MIXER, 2, 5 * 20, (r) -> r
				.addFluidInput(MIEFluids.HONEY, 200)
				.addItemInput(from, 1)
				.addItemOutput(to, 1));
	}
	
	@Override
	public void run()
	{
		this.addHoneyWaxingRecipe(Items.COPPER_BLOCK, Items.WAXED_COPPER_BLOCK);
		this.addHoneyWaxingRecipe(Items.CUT_COPPER, Items.WAXED_CUT_COPPER);
		this.addHoneyWaxingRecipe(Items.CUT_COPPER_SLAB, Items.WAXED_CUT_COPPER_SLAB);
		this.addHoneyWaxingRecipe(Items.CUT_COPPER_STAIRS, Items.WAXED_CUT_COPPER_STAIRS);
		this.addHoneyWaxingRecipe(Items.EXPOSED_COPPER, Items.WAXED_EXPOSED_COPPER);
		this.addHoneyWaxingRecipe(Items.EXPOSED_CUT_COPPER, Items.WAXED_EXPOSED_CUT_COPPER);
		this.addHoneyWaxingRecipe(Items.EXPOSED_CUT_COPPER_SLAB, Items.WAXED_EXPOSED_CUT_COPPER_SLAB);
		this.addHoneyWaxingRecipe(Items.EXPOSED_CUT_COPPER_STAIRS, Items.WAXED_EXPOSED_CUT_COPPER_STAIRS);
		this.addHoneyWaxingRecipe(Items.WEATHERED_COPPER, Items.WAXED_WEATHERED_COPPER);
		this.addHoneyWaxingRecipe(Items.WEATHERED_CUT_COPPER, Items.WAXED_WEATHERED_CUT_COPPER);
		this.addHoneyWaxingRecipe(Items.WEATHERED_CUT_COPPER_SLAB, Items.WAXED_WEATHERED_CUT_COPPER_SLAB);
		this.addHoneyWaxingRecipe(Items.WEATHERED_CUT_COPPER_STAIRS, Items.WAXED_WEATHERED_CUT_COPPER_STAIRS);
		this.addHoneyWaxingRecipe(Items.OXIDIZED_COPPER, Items.WAXED_OXIDIZED_COPPER);
		this.addHoneyWaxingRecipe(Items.OXIDIZED_CUT_COPPER, Items.WAXED_OXIDIZED_CUT_COPPER);
		this.addHoneyWaxingRecipe(Items.OXIDIZED_CUT_COPPER_SLAB, Items.WAXED_OXIDIZED_CUT_COPPER_SLAB);
		this.addHoneyWaxingRecipe(Items.OXIDIZED_CUT_COPPER_STAIRS, Items.WAXED_OXIDIZED_CUT_COPPER_STAIRS);
	}
}
