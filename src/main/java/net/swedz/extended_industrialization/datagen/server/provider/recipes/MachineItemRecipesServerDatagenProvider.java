package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.MIItem;
import aztech.modern_industrialization.MITags;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import aztech.modern_industrialization.materials.MIMaterials;
import aztech.modern_industrialization.materials.part.MIParts;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.registry.items.EIItems;
import net.swedz.extended_industrialization.registry.tags.EITags;

import java.util.function.Consumer;

public final class MachineItemRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public MachineItemRecipesServerDatagenProvider(GatherDataEvent event)
	{
		super(event);
	}
	
	private static void addBasicCraftingMachineRecipes(String machineName, String machineTier, ItemLike machine, Consumer<ShapedRecipeBuilder> crafting, Consumer<MachineRecipeBuilder> assembling, RecipeOutput output)
	{
		ShapedRecipeBuilder shapedRecipeBuilder = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, machine);
		crafting.accept(shapedRecipeBuilder);
		shapedRecipeBuilder
				.unlockedBy(getHasName(machine), has(machine))
				.save(output, EI.id("machines/%s/craft/%s".formatted(machineName, machineTier)));
		
		addMachineRecipe(
				"machines/%s/assembler".formatted(machineName), machineTier, MIMachineRecipeTypes.ASSEMBLER,
				8, 10 * 20,
				(builder) ->
				{
					assembling.accept(builder);
					builder.addItemOutput(machine, 1);
				},
				output
		);
	}
	
	private static void addSteelUpgradeMachineRecipes(String machine, ItemLike machineBronze, ItemLike machineSteel, RecipeOutput output)
	{
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, machineSteel)
				.requires(machineBronze)
				.requires(MIItem.STEEL_UPGRADE)
				.unlockedBy(getHasName(machineBronze), has(machineBronze))
				.save(output, EI.id("machines/%s/craft/upgrade_steel".formatted(machine)));
		
		addMachineRecipe(
				"machines/%s/packer".formatted(machine), "upgrade_steel", MIMachineRecipeTypes.PACKER,
				2, 5 * 20,
				(r) -> r
						.addItemInput(machineBronze, 1)
						.addItemInput(MIItem.STEEL_UPGRADE, 1)
						.addItemOutput(machineSteel, 1),
				output
		);
		
		addMachineRecipe(
				"machines/%s/unpacker".formatted(machine), "downgrade_steel", MIMachineRecipeTypes.UNPACKER,
				2, 5 * 20,
				(r) -> r
						.addItemInput(machineSteel, 1)
						.addItemOutput(machineBronze, 1)
						.addItemOutput(MIItem.STEEL_UPGRADE, 1),
				output
		);
	}
	
	private static void addBronzeAndSteelMachineRecipes(String machine, Consumer<ShapedRecipeBuilder> crafting, Consumer<MachineRecipeBuilder> assembling, RecipeOutput output)
	{
		ItemLike machineBronze = EIItems.valueOf("bronze_%s".formatted(machine));
		ItemLike machineSteel = EIItems.valueOf("steel_%s".formatted(machine));
		
		addBasicCraftingMachineRecipes(machine, "bronze", machineBronze, crafting, assembling, output);
		
		addSteelUpgradeMachineRecipes(machine, machineBronze, machineSteel, output);
	}
	
	private static void addSteelMachineRecipes(String machine, Consumer<ShapedRecipeBuilder> crafting, Consumer<MachineRecipeBuilder> assembling, RecipeOutput output)
	{
		ItemLike machineSteel = EIItems.valueOf("steel_%s".formatted(machine));
		
		ShapedRecipeBuilder shapedRecipeBuilder = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, machineSteel);
		crafting.accept(shapedRecipeBuilder);
		shapedRecipeBuilder
				.unlockedBy(getHasName(machineSteel), has(machineSteel))
				.save(output, EI.id("machines/%s/craft/steel".formatted(machine)));
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		addBronzeAndSteelMachineRecipes(
				"bending_machine",
				(builder) -> builder
						.define('G', MIMaterials.COPPER.getPart(MIParts.GEAR))
						.define('R', MIMaterials.COPPER.getPart(MIParts.ROD))
						.define('C', MIMaterials.BRONZE.getPart(MIParts.MACHINE_CASING))
						.define('P', MITags.FLUID_PIPES)
						.pattern("GRG")
						.pattern("RCR")
						.pattern("PPP"),
				(builder) -> builder
						.addItemInput(MIMaterials.COPPER.getPart(MIParts.GEAR), 2)
						.addItemInput(MIMaterials.COPPER.getPart(MIParts.ROD), 3)
						.addItemInput(MIMaterials.BRONZE.getPart(MIParts.MACHINE_CASING), 1)
						.addItemInput(MITags.FLUID_PIPES, 3),
				output
		);
		
		addBronzeAndSteelMachineRecipes(
				"composter",
				(builder) -> builder
						.define('G', MIMaterials.COPPER.getPart(MIParts.GEAR))
						.define('O', MIMaterials.BRONZE.getPart(MIParts.BARREL))
						.define('R', MIMaterials.COPPER.getPart(MIParts.ROTOR))
						.define('C', MIMaterials.BRONZE.getPart(MIParts.MACHINE_CASING))
						.define('P', MITags.FLUID_PIPES)
						.pattern("GOG")
						.pattern("RCR")
						.pattern("PPP"),
				(builder) -> builder
						.addItemInput(MIMaterials.COPPER.getPart(MIParts.GEAR), 2)
						.addItemInput(MIMaterials.BRONZE.getPart(MIParts.BARREL), 1)
						.addItemInput(MIMaterials.COPPER.getPart(MIParts.ROTOR), 2)
						.addItemInput(MIMaterials.BRONZE.getPart(MIParts.MACHINE_CASING), 1)
						.addItemInput(MITags.FLUID_PIPES, 3),
				output
		);
		
		addBronzeAndSteelMachineRecipes(
				"solar_boiler",
				(builder) -> builder
						.define('G', EITags.forgeItem("glass"))
						.define('S', MIMaterials.SILVER.getPart(MIParts.PLATE))
						.define('C', Items.FURNACE)
						.define('B', MIBlock.BLOCK_FIRE_CLAY_BRICKS)
						.define('T', MIMaterials.BRONZE.getPart(MIParts.TANK))
						.pattern("GSG")
						.pattern("SCS")
						.pattern("BTB"),
				(builder) -> builder
						.addItemInput(EITags.forgeItem("glass"), 3)
						.addItemInput(MIMaterials.SILVER.getPart(MIParts.PLATE), 2)
						.addItemInput(Items.FURNACE, 1)
						.addItemInput(MIBlock.BLOCK_FIRE_CLAY_BRICKS, 2)
						.addItemInput(MIMaterials.BRONZE.getPart(MIParts.TANK), 1),
				output
		);
	}
}
