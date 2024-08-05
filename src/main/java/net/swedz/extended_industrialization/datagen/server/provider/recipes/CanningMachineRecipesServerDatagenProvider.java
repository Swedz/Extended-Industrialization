package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.EIMachines;
import net.swedz.tesseract.neoforge.compat.mi.mixin.accessor.MIRecipeAccessor;

public final class CanningMachineRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public CanningMachineRecipesServerDatagenProvider(GatherDataEvent event)
	{
		super(event);
	}
	
	private static void addFillingAndEmptyingRecipes(String id, FluidStack fluidStack, Item emptyItem, Ingredient fullItemAsInput, ItemStack fullItemAsOutput, RecipeOutput output)
	{
		addMachineRecipe(
				"canning_machine/filling", id, EIMachines.RecipeTypes.CANNING_MACHINE,
				2, 5 * 20,
				(r) ->
				{
					r.addFluidInput(fluidStack.getFluid(), fluidStack.getAmount());
					r.addItemInput(emptyItem, 1);
					((MIRecipeAccessor) r).recipe().itemOutputs.add(new MachineRecipe.ItemOutput(ItemVariant.of(fullItemAsOutput), 1, 1f));
				},
				output
		);
		addMachineRecipe(
				"canning_machine/emptying", id, EIMachines.RecipeTypes.CANNING_MACHINE,
				2, 5 * 20,
				(r) -> r
						.addItemInput(fullItemAsInput, 1, 1f)
						.addItemOutput(emptyItem, 1)
						.addFluidOutput(fluidStack.getFluid(), fluidStack.getAmount()),
				output
		);
	}
	
	private static void addFillingAndEmptyingRecipes(String id, FluidStack fluidStack, Item emptyItem, Item fullItem, RecipeOutput output)
	{
		addMachineRecipe(
				"canning_machine/filling", id, EIMachines.RecipeTypes.CANNING_MACHINE,
				2, 5 * 20,
				(r) -> r
						.addFluidInput(fluidStack.getFluid(), fluidStack.getAmount())
						.addItemInput(emptyItem, 1)
						.addItemOutput(fullItem, 1),
				output
		);
		addMachineRecipe(
				"canning_machine/emptying", id, EIMachines.RecipeTypes.CANNING_MACHINE,
				2, 5 * 20,
				(r) -> r
						.addItemInput(fullItem, 1)
						.addItemOutput(emptyItem, 1)
						.addFluidOutput(fluidStack.getFluid(), fluidStack.getAmount()),
				output
		);
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		addFillingAndEmptyingRecipes(
				"minecraft/water_bottle",
				new FluidStack(Fluids.WATER, 350),
				Items.GLASS_BOTTLE,
				DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER), Items.POTION),
				PotionContents.createItemStack(Items.POTION, Potions.WATER),
				output
		);
		addFillingAndEmptyingRecipes(
				"minecraft/honey_bottle",
				new FluidStack(EIFluids.HONEY.asFluid(), 250),
				Items.GLASS_BOTTLE,
				Items.HONEY_BOTTLE,
				output
		);
	}
}
