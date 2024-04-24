package net.swedz.miextended.datagen.server.provider.recipes;

import com.google.common.collect.Sets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.swedz.miextended.mi.hook.MIMachineHook;
import net.swedz.miextended.registry.fluids.MIEFluids;
import net.swedz.miextended.registry.items.MIEItems;

import java.util.Set;

public final class CanningMachineRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public CanningMachineRecipesServerDatagenProvider(GatherDataEvent event)
	{
		super(event);
	}
	
	private static void addFillingAndEmptyingRecipes(FluidStack fluidStack, Item emptyItem, Item fullItem, RecipeOutput output)
	{
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(fullItem);
		addMachineRecipe(
				"canning_machine/filling/%s".formatted(id.getNamespace()), id.getPath(), MIMachineHook.RecipeTypes.CANNING_MACHINE,
				2, 5 * 20,
				(r) -> r
						.addFluidInput(fluidStack.getFluid(), fluidStack.getAmount())
						.addItemInput(emptyItem, 1)
						.addItemOutput(fullItem, 1),
				output
		);
		addMachineRecipe(
				"canning_machine/emptying/%s".formatted(id.getNamespace()), id.getPath(), MIMachineHook.RecipeTypes.CANNING_MACHINE,
				2, 5 * 20,
				(r) -> r
						.addItemInput(fullItem, 1)
						.addItemOutput(emptyItem, 1)
						.addFluidOutput(fluidStack.getFluid(), fluidStack.getAmount()),
				output
		);
	}
	
	private static void addCannedFoodRecipe(Item foodItem, FoodProperties food, RecipeOutput output)
	{
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(foodItem);
		int count = (int) Math.ceil(food.getNutrition() / 2D);
		addMachineRecipe(
				"canning_machine/canned_food/%s".formatted(id.getNamespace()), id.getPath(), MIMachineHook.RecipeTypes.CANNING_MACHINE,
				2, 5 * 20,
				(r) -> r
						.addItemInput(MIEItems.TIN_CAN, count)
						.addItemInput(foodItem, 1)
						.addItemOutput(MIEItems.CANNED_FOOD, count),
				output
		);
	}
	
	private static void bucketRecipes(RecipeOutput output)
	{
		Set<Fluid> uniqueFluids = Sets.newHashSet();
		for(Fluid fluid : BuiltInRegistries.FLUID)
		{
			Fluid processedFluid = fluid instanceof FlowingFluid flowingFluid ? flowingFluid.getSource() : fluid;
			if(uniqueFluids.add(processedFluid))
			{
				Item fullItem = processedFluid.getBucket();
				if(fullItem != Items.AIR)
				{
					addFillingAndEmptyingRecipes(new FluidStack(processedFluid, 1000), Items.BUCKET, fullItem, output);
				}
			}
		}
	}
	
	private static void cannedFoodRecipes(RecipeOutput output)
	{
		for(Item item : BuiltInRegistries.ITEM)
		{
			if(item.isEdible() && item != MIEItems.CANNED_FOOD.asItem() && !(item instanceof BowlFoodItem) && !(item instanceof HoneyBottleItem))
			{
				FoodProperties food = item.getFoodProperties(item.getDefaultInstance(), null);
				addCannedFoodRecipe(item, food, output);
			}
		}
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		bucketRecipes(output);
		
		addFillingAndEmptyingRecipes(new FluidStack(MIEFluids.HONEY.asFluid(), 250), Items.GLASS_BOTTLE, Items.HONEY_BOTTLE, output);
		
		cannedFoodRecipes(output);
	}
}
