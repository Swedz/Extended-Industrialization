package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.MIItem;
import aztech.modern_industrialization.MITags;
import aztech.modern_industrialization.materials.MIMaterials;
import aztech.modern_industrialization.materials.part.MIParts;
import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.datagen.api.recipe.ShapedRecipeBuilder;
import net.swedz.extended_industrialization.datagen.api.recipe.ShapelessRecipeBuilder;
import net.swedz.extended_industrialization.registry.tags.EITags;

import java.util.function.Consumer;

public final class MachineItemRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public MachineItemRecipesServerDatagenProvider(GatherDataEvent event)
	{
		super(event);
	}
	
	private static String machine(String machine, String tier)
	{
		return "%s:%s_%s".formatted(EI.ID, tier, machine);
	}
	
	private static String machineBronze(String machine)
	{
		return machine(machine, "bronze");
	}
	
	private static String machineSteel(String machine)
	{
		return machine(machine, "steel");
	}
	
	private static void addBasicCraftingMachineRecipes(String machineName, String machineTier, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output)
	{
		ShapedRecipeBuilder shapedRecipeBuilder = new ShapedRecipeBuilder();
		crafting.accept(shapedRecipeBuilder);
		shapedRecipeBuilder.setOutput(machine(machineName, machineTier), 1);
		shapedRecipeBuilder.offerTo(output, EI.id("machines/%s/craft/%s".formatted(machineName, machineTier)));
		
		shapedRecipeBuilder.exportToAssembler().offerTo(output, EI.id("machines/%s/assembler/%s".formatted(machineName, machineTier)));
	}
	
	private static void addSteelUpgradeMachineRecipes(String machine, String machineBronze, String machineSteel, RecipeOutput output)
	{
		ShapelessRecipeBuilder shapelessRecipeBuilder = new ShapelessRecipeBuilder()
				.with(machineBronze)
				.with(MIItem.STEEL_UPGRADE)
				.setOutput(machineSteel, 1);
		shapelessRecipeBuilder.offerTo(output, EI.id("machines/%s/craft/upgrade_steel".formatted(machine)));
		
		shapelessRecipeBuilder.exportToPacker().offerTo(output, EI.id("machines/%s/packer/upgrade_steel".formatted(machine)));
		
		shapelessRecipeBuilder.exportToUnpackerAndFlip().offerTo(output, EI.id("machines/%s/unpacker/downgrade_steel".formatted(machine)));
	}
	
	private static void addBronzeAndSteelMachineRecipes(String machine, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output)
	{
		addBasicCraftingMachineRecipes(machine, "bronze", crafting, output);
		
		addSteelUpgradeMachineRecipes(machine, machineBronze(machine), machineSteel(machine), output);
	}
	
	private static void addSteelMachineRecipes(String machine, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output)
	{
		ShapedRecipeBuilder shapedRecipeBuilder = new ShapedRecipeBuilder();
		crafting.accept(shapedRecipeBuilder);
		shapedRecipeBuilder.setOutput(machineSteel(machine), 1);
		shapedRecipeBuilder.offerTo(output, EI.id("machines/%s/craft/steel".formatted(machine)));
	}
	
	private static void bendingMachine(RecipeOutput output)
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
				output
		);
	}
	
	private static void composter(RecipeOutput output)
	{
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
				output
		);
	}
	
	private static void solarBoiler(RecipeOutput output)
	{
		addBronzeAndSteelMachineRecipes(
				"solar_boiler",
				(builder) -> builder
						.define('G', EITags.itemForge("glass"))
						.define('S', EITags.itemForge("plates/silver"))
						.define('B', MIBlock.BLOCK_FIRE_CLAY_BRICKS)
						.define('C', ingredient("modern_industrialization:bronze_boiler"))
						.pattern("GGG")
						.pattern("SSS")
						.pattern("BCB"),
				output
		);
		addSteelMachineRecipes(
				"solar_boiler",
				(builder) -> builder
						.define('G', EITags.itemForge("glass"))
						.define('S', EITags.itemForge("plates/silver"))
						.define('B', MIBlock.BLOCK_FIRE_CLAY_BRICKS)
						.define('C', ingredient("modern_industrialization:steel_boiler"))
						.pattern("GGG")
						.pattern("SSS")
						.pattern("BCB"),
				output
		);
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		bendingMachine(output);
		composter(output);
		solarBoiler(output);
	}
}
