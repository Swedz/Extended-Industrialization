package net.swedz.extended_industrialization.machines.recipe;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import aztech.modern_industrialization.machines.recipe.ProxyableMachineRecipeType;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.datagen.api.RecipeHelper;

import java.util.List;
import java.util.Set;

public final class CanningMachineRecipeType extends ProxyableMachineRecipeType
{
	public CanningMachineRecipeType(ResourceLocation id)
	{
		super(id);
	}
	
	private RecipeHolder<MachineRecipe> generateCannedFood(Item foodItem, FoodProperties food)
	{
		ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(foodItem);
		ResourceLocation id = EI.id("canning_machine/generated/canned_food/%s/%s".formatted(itemId.getNamespace(), itemId.getPath()));
		
		MachineRecipeBuilder recipe = new MachineRecipeBuilder(this, 2, 5 * 20);
		
		int count = (int) Math.ceil(food.nutrition() / 2D);
		recipe.addItemInput(EIItems.TIN_CAN, count);
		recipe.addItemInput(foodItem, 1);
		recipe.addItemOutput(EIItems.CANNED_FOOD, count);
		ItemStack remainingItemStack = ItemStack.EMPTY;
		if(food.usingConvertsTo().isPresent())
		{
			remainingItemStack = food.usingConvertsTo().get();
		}
		else
		{
			ItemStack foodItemStack = foodItem.getDefaultInstance();
			if(foodItemStack.hasCraftingRemainingItem())
			{
				remainingItemStack = foodItemStack.getCraftingRemainingItem();
			}
		}
		recipe.addItemOutput(remainingItemStack.getItem(), remainingItemStack.getCount());
		
		return new RecipeHolder<>(id, RecipeHelper.getActualRecipe(recipe));
	}
	
	private List<RecipeHolder<MachineRecipe>> buildCannedFood()
	{
		List<RecipeHolder<MachineRecipe>> recipes = Lists.newArrayList();
		
		for(Item item : BuiltInRegistries.ITEM)
		{
			if(item != EIItems.CANNED_FOOD.asItem() && item != Items.OMINOUS_BOTTLE)
			{
				ItemStack itemStack = item.getDefaultInstance();
				FoodProperties foodProperties = item.getFoodProperties(itemStack, null);
				if(foodProperties != null)
				{
					FoodProperties food = item.getFoodProperties(item.getDefaultInstance(), null);
					recipes.add(this.generateCannedFood(item, food));
				}
			}
		}
		
		return recipes;
	}
	
	private RecipeHolder<MachineRecipe> generateFillingBucket(FluidStack fluidStack, ResourceLocation itemId, Item fullItem, Item emptyItem)
	{
		ResourceLocation id = EI.id("canning_machine/generated/filling/%s/%s".formatted(itemId.getNamespace(), itemId.getPath()));
		
		MachineRecipeBuilder recipe = new MachineRecipeBuilder(this, 2, 5 * 20);
		
		recipe.addFluidInput(fluidStack.getFluid(), fluidStack.getAmount());
		recipe.addItemInput(emptyItem, 1);
		recipe.addItemOutput(fullItem, 1);
		
		return new RecipeHolder<>(id, RecipeHelper.getActualRecipe(recipe));
	}
	
	private RecipeHolder<MachineRecipe> generateEmptyingBucket(FluidStack fluidStack, ResourceLocation itemId, Item fullItem, Item emptyItem)
	{
		ResourceLocation id = EI.id("canning_machine/generated/emptying/%s/%s".formatted(itemId.getNamespace(), itemId.getPath()));
		
		MachineRecipeBuilder recipe = new MachineRecipeBuilder(this, 2, 5 * 20);
		
		recipe.addItemInput(fullItem, 1);
		recipe.addItemOutput(emptyItem, 1);
		recipe.addFluidOutput(fluidStack.getFluid(), fluidStack.getAmount());
		
		return new RecipeHolder<>(id, RecipeHelper.getActualRecipe(recipe));
	}
	
	private List<RecipeHolder<MachineRecipe>> buildBucket()
	{
		List<RecipeHolder<MachineRecipe>> recipes = Lists.newArrayList();
		
		Set<Fluid> uniqueFluids = Sets.newHashSet();
		for(Fluid fluid : BuiltInRegistries.FLUID)
		{
			Fluid processedFluid = fluid instanceof FlowingFluid flowingFluid ? flowingFluid.getSource() : fluid;
			if(uniqueFluids.add(processedFluid))
			{
				Item fullItem = processedFluid.getBucket();
				if(fullItem != Items.AIR)
				{
					ItemStack fullItemStack = fullItem.getDefaultInstance();
					ItemStack emptyItemStack = fullItemStack.getCraftingRemainingItem();
					Item emptyItem = emptyItemStack.isEmpty() ? Items.BUCKET : emptyItemStack.getItem();
					
					ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(fullItem);
					FluidStack fluidStack = new FluidStack(processedFluid, 1000);
					recipes.add(this.generateFillingBucket(fluidStack, itemId, fullItem, emptyItem));
					recipes.add(this.generateEmptyingBucket(fluidStack, itemId, fullItem, emptyItem));
				}
			}
		}
		
		return recipes;
	}
	
	@Override
	protected void fillRecipeList(Level level, List<RecipeHolder<MachineRecipe>> recipeList)
	{
		recipeList.addAll(this.getManagerRecipes(level));
		
		recipeList.addAll(this.buildCannedFood());
		recipeList.addAll(this.buildBucket());
	}
}
