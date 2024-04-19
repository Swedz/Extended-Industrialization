package net.swedz.miextended.datagen.server.provider.recipes;

import com.google.common.collect.Sets;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.fluids.MIEFluids;
import net.swedz.miextended.items.MIEItems;
import net.swedz.miextended.mi.hook.MIMachineHook;

import java.util.Set;

public final class CanningMachineRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public CanningMachineRecipesServerDatagenProvider(GatherDataEvent event)
	{
		super(event, "MI Extended Datagen/Server/Recipes/Canning Machine", MIExtended.ID);
	}
	
	private void addFillingAndEmptyingRecipes(FluidStack fluidStack, Item emptyItem, Item fullItem)
	{
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(fullItem);
		this.addMachineRecipe("canning_machine/filling/%s".formatted(id.getNamespace()), id.getPath(), MIMachineHook.CANNING_MACHINE, 2, 5 * 20, (r) -> r
				.addFluidInput(fluidStack.getFluid(), fluidStack.getAmount())
				.addItemInput(emptyItem, 1)
				.addItemOutput(fullItem, 1));
		this.addMachineRecipe("canning_machine/emptying/%s".formatted(id.getNamespace()), id.getPath(), MIMachineHook.CANNING_MACHINE, 2, 5 * 20, (r) -> r
				.addItemInput(fullItem, 1)
				.addItemOutput(emptyItem, 1)
				.addFluidOutput(fluidStack.getFluid(), fluidStack.getAmount()));
	}
	
	private void addCannedFoodRecipe(Item foodItem, FoodProperties food)
	{
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(foodItem);
		int count = (int) Math.ceil(food.getNutrition() / 2D);
		this.addMachineRecipe("canning_machine/canned_food/%s".formatted(id.getNamespace()), id.getPath(), MIMachineHook.CANNING_MACHINE, 2, 5 * 20, (r) -> r
				.addItemInput(MIEItems.TIN_CAN, count)
				.addItemInput(foodItem, 1)
				.addItemOutput(MIEItems.CANNED_FOOD, count));
	}
	
	private void bucketRecipes()
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
					this.addFillingAndEmptyingRecipes(new FluidStack(processedFluid, 1000), Items.BUCKET, fullItem);
				}
			}
		}
	}
	
	private void cannedFoodRecipes()
	{
		for(Item item : BuiltInRegistries.ITEM)
		{
			if(item.isEdible() && item != MIEItems.CANNED_FOOD && !(item instanceof BowlFoodItem) && !(item instanceof HoneyBottleItem))
			{
				FoodProperties food = item.getFoodProperties(item.getDefaultInstance(), null);
				this.addCannedFoodRecipe(item, food);
			}
		}
	}
	
	@Override
	public void run()
	{
		this.bucketRecipes();
		
		this.addFillingAndEmptyingRecipes(new FluidStack(MIEFluids.HONEY.asFluid(), 250), Items.GLASS_BOTTLE, Items.HONEY_BOTTLE);
		
		this.cannedFoodRecipes();
	}
}
