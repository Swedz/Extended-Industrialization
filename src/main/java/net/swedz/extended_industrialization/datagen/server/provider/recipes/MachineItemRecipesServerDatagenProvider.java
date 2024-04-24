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
import net.swedz.extended_industrialization.registry.items.EIItems;
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
	
	private static void addBronzeMachineRecipes(String machine, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output)
	{
		addBasicCraftingMachineRecipes(machine, "bronze", crafting, output);
	}
	
	private static void addSteelMachineRecipes(String machine, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output)
	{
		addBasicCraftingMachineRecipes(machine, "steel", crafting, output);
	}
	
	private static void addElectricMachineRecipes(String machine, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output)
	{
		addBasicCraftingMachineRecipes(machine, "electric", crafting, output);
	}
	
	private static void addSteelUpgradeMachineRecipes(String machine, RecipeOutput output)
	{
		ShapelessRecipeBuilder shapelessRecipeBuilder = new ShapelessRecipeBuilder()
				.with(machineBronze(machine))
				.with(MIItem.STEEL_UPGRADE)
				.setOutput(machineSteel(machine), 1);
		shapelessRecipeBuilder.offerTo(output, EI.id("machines/%s/craft/upgrade_steel".formatted(machine)));
		
		shapelessRecipeBuilder.exportToPacker().offerTo(output, EI.id("machines/%s/packer/upgrade_steel".formatted(machine)));
		
		shapelessRecipeBuilder.exportToUnpackerAndFlip().offerTo(output, EI.id("machines/%s/unpacker/downgrade_steel".formatted(machine)));
	}
	
	private static void addBronzeAndSteelMachineRecipes(String machine, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output)
	{
		addBronzeMachineRecipes(machine, crafting, output);
		addSteelUpgradeMachineRecipes(machine, output);
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
		addElectricMachineRecipes(
				"bending_machine",
				(builder) -> builder
						.define('A', MIItem.ANALOG_CIRCUIT)
						.define('P', MIItem.PISTON)
						.define('M', MIItem.MOTOR)
						.define('C', "modern_industrialization:basic_machine_hull")
						.define('c', MIMaterials.TIN.getPart(MIParts.CABLE))
						.pattern("APA")
						.pattern("MCM")
						.pattern("cPc"),
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
		addElectricMachineRecipes(
				"composter",
				(builder) -> builder
						.define('A', MIItem.ANALOG_CIRCUIT)
						.define('P', MITags.FLUID_PIPES)
						.define('M', MIItem.MOTOR)
						.define('C', "modern_industrialization:basic_machine_hull")
						.define('c', MIMaterials.TIN.getPart(MIParts.CABLE))
						.define('p', MIItem.PUMP)
						.pattern("APA")
						.pattern("MCM")
						.pattern("cpc"),
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
	
	private static void alloySmelter(RecipeOutput output)
	{
		addSteelMachineRecipes(
				"alloy_smelter",
				(builder) -> builder
						.define('I', EITags.itemForge("plates/invar"))
						.define('R', MIMaterials.BRONZE.getPart(MIParts.ROTOR))
						.define('C', MIMaterials.STEEL.getPart(MIParts.MACHINE_CASING))
						.define('P', MITags.FLUID_PIPES)
						.pattern("IRI")
						.pattern("ICI")
						.pattern("PPP"),
				output
		);
		addElectricMachineRecipes(
				"alloy_smelter",
				(builder) -> builder
						.define('W', MIMaterials.CUPRONICKEL.getPart(MIParts.WIRE_MAGNETIC))
						.define('R', MIMaterials.TIN.getPart(MIParts.ROTOR))
						.define('A', MIItem.ANALOG_CIRCUIT)
						.define('C', "modern_industrialization:basic_machine_hull")
						.define('c', MIMaterials.TIN.getPart(MIParts.CABLE))
						.pattern("WRW")
						.pattern("ACA")
						.pattern("cWc"),
				output
		);
	}
	
	private static void canningMachine(RecipeOutput output)
	{
		addSteelMachineRecipes(
				"canning_machine",
				(builder) -> builder
						.define('P', MITags.FLUID_PIPES)
						.define('T', MIMaterials.STEEL.getPart(MIParts.TANK))
						.define('R', MIMaterials.BRONZE.getPart(MIParts.ROTOR))
						.define('C', MIMaterials.STEEL.getPart(MIParts.MACHINE_CASING))
						.pattern("PTP")
						.pattern("RCR")
						.pattern("PPP"),
				output
		);
		addElectricMachineRecipes(
				"canning_machine",
				(builder) -> builder
						.define('P', MITags.FLUID_PIPES)
						.define('R', MIMaterials.TIN.getPart(MIParts.ROTOR))
						.define('p', MIItem.PUMP)
						.define('C', "modern_industrialization:basic_machine_hull")
						.define('A', MIItem.ANALOG_CIRCUIT)
						.define('c', MIItem.CONVEYOR)
						.pattern("PRP")
						.pattern("pCp")
						.pattern("AcA"),
				output
		);
	}
	
	private static void honeyExtractor(RecipeOutput output)
	{
		addSteelMachineRecipes(
				"honey_extractor",
				(builder) -> builder
						.define('R', MIMaterials.BRONZE.getPart(MIParts.ROD))
						.define('r', MIMaterials.BRONZE.getPart(MIParts.ROTOR))
						.define('T', MIMaterials.STEEL.getPart(MIParts.TANK))
						.define('C', MIMaterials.STEEL.getPart(MIParts.MACHINE_CASING))
						.define('P', MITags.FLUID_PIPES)
						.pattern("RrR")
						.pattern("TCT")
						.pattern("PPP"),
				output
		);
		addElectricMachineRecipes(
				"honey_extractor",
				(builder) -> builder
						.define('A', MIItem.ANALOG_CIRCUIT)
						.define('R', MIMaterials.TIN.getPart(MIParts.ROTOR))
						.define('G', EITags.itemForge("glass"))
						.define('C', "modern_industrialization:basic_machine_hull")
						.define('c', MIMaterials.TIN.getPart(MIParts.CABLE))
						.define('P', MIItem.PUMP)
						.pattern("ARA")
						.pattern("GCG")
						.pattern("cPc"),
				output
		);
	}
	
	private static void farmer(RecipeOutput output)
	{
		addBasicCraftingMachineRecipes(
				"farmer", "steam",
				(builder) -> builder
						.define('B', "modern_industrialization:bronze_plated_bricks")
						.define('C', EIItems.COMBINE)
						.define('A', MIItem.ANALOG_CIRCUIT)
						.define('M', MIItem.MOTOR)
						.define('P', "modern_industrialization:bronze_machine_casing_pipe")
						.pattern("BCB")
						.pattern("AMA")
						.pattern("BPB"),
				output
		);
		addBasicCraftingMachineRecipes(
				"farmer", "electric",
				(builder) -> builder
						.define('A', MIItem.ROBOT_ARM)
						.define('M', MIItem.LARGE_MOTOR)
						.define('C', "modern_industrialization:clean_stainless_steel_machine_casing")
						.define('F', EIItems.valueOf("steam_farmer"))
						.define('P', MIItem.LARGE_PUMP)
						.define('p', "modern_industrialization:stainless_steel_machine_casing_pipe")
						.pattern("AMA")
						.pattern("CFC")
						.pattern("PpP"),
				output
		);
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		bendingMachine(output);
		composter(output);
		solarBoiler(output);
		alloySmelter(output);
		canningMachine(output);
		honeyExtractor(output);
		farmer(output);
	}
}
