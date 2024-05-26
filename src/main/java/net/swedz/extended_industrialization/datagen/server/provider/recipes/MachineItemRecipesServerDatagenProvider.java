package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.MIItem;
import aztech.modern_industrialization.MITags;
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
		return "%s:%s".formatted(EI.ID, tier == null ? machine : "%s_%s".formatted(tier, machine));
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
		String recipeId = machineTier == null ? "" : "/%s".formatted(machineTier);
		
		ShapedRecipeBuilder shapedRecipeBuilder = new ShapedRecipeBuilder();
		crafting.accept(shapedRecipeBuilder);
		shapedRecipeBuilder.setOutput(machine(machineName, machineTier), 1);
		shapedRecipeBuilder.offerTo(output, EI.id("machines/%s/craft%s".formatted(machineName, recipeId)));
		
		shapedRecipeBuilder.exportToAssembler().offerTo(output, EI.id("machines/%s/assembler%s".formatted(machineName, recipeId)));
	}
	
	private static void addBasicCraftingMachineRecipes(String machineName, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output)
	{
		addBasicCraftingMachineRecipes(machineName, null, crafting, output);
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
						.define('G', "modern_industrialization:copper_gear")
						.define('R', "modern_industrialization:copper_rod")
						.define('C', "modern_industrialization:bronze_machine_casing")
						.define('P', MITags.FLUID_PIPES)
						.pattern("GRG")
						.pattern("RCR")
						.pattern("PPP"),
				output
		);
		addElectricMachineRecipes(
				"bending_machine",
				(builder) -> builder
						.define('A', "modern_industrialization:analog_circuit")
						.define('P', "modern_industrialization:piston")
						.define('M', "modern_industrialization:motor")
						.define('C', "modern_industrialization:basic_machine_hull")
						.define('c', "modern_industrialization:tin_cable")
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
						.define('G', "modern_industrialization:copper_gear")
						.define('O', "modern_industrialization:bronze_barrel")
						.define('R', "modern_industrialization:copper_rotor")
						.define('C', "modern_industrialization:bronze_machine_casing")
						.define('P', MITags.FLUID_PIPES)
						.pattern("GOG")
						.pattern("RCR")
						.pattern("PPP"),
				output
		);
		addElectricMachineRecipes(
				"composter",
				(builder) -> builder
						.define('A', "modern_industrialization:analog_circuit")
						.define('P', MITags.FLUID_PIPES)
						.define('M', "modern_industrialization:motor")
						.define('C', "modern_industrialization:basic_machine_hull")
						.define('c', "modern_industrialization:tin_cable")
						.define('p', "modern_industrialization:pump")
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
						.define('B', "modern_industrialization:fire_clay_bricks")
						.define('C', "modern_industrialization:bronze_boiler")
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
						.define('B', "modern_industrialization:fire_clay_bricks")
						.define('C', "modern_industrialization:steel_boiler")
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
						.define('R', "modern_industrialization:bronze_rotor")
						.define('C', "modern_industrialization:steel_machine_casing")
						.define('P', MITags.FLUID_PIPES)
						.pattern("IRI")
						.pattern("ICI")
						.pattern("PPP"),
				output
		);
		addElectricMachineRecipes(
				"alloy_smelter",
				(builder) -> builder
						.define('W', "modern_industrialization:cupronickel_wire_magnetic")
						.define('R', "modern_industrialization:tin_rotor")
						.define('A', "modern_industrialization:analog_circuit")
						.define('C', "modern_industrialization:basic_machine_hull")
						.define('c', "modern_industrialization:tin_cable")
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
						.define('T', "modern_industrialization:steel_tank")
						.define('R', "modern_industrialization:bronze_rotor")
						.define('C', "modern_industrialization:steel_machine_casing")
						.pattern("PTP")
						.pattern("RCR")
						.pattern("PPP"),
				output
		);
		addElectricMachineRecipes(
				"canning_machine",
				(builder) -> builder
						.define('P', MITags.FLUID_PIPES)
						.define('R', "modern_industrialization:tin_rotor")
						.define('p', "modern_industrialization:pump")
						.define('C', "modern_industrialization:basic_machine_hull")
						.define('A', "modern_industrialization:analog_circuit")
						.define('c', "modern_industrialization:conveyor")
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
						.define('R', "modern_industrialization:bronze_rod")
						.define('r', "modern_industrialization:bronze_rotor")
						.define('T', "modern_industrialization:steel_tank")
						.define('C', "modern_industrialization:steel_machine_casing")
						.define('P', MITags.FLUID_PIPES)
						.pattern("RrR")
						.pattern("TCT")
						.pattern("PPP"),
				output
		);
		addElectricMachineRecipes(
				"honey_extractor",
				(builder) -> builder
						.define('A', "modern_industrialization:analog_circuit")
						.define('R', "modern_industrialization:tin_rotor")
						.define('G', EITags.itemForge("glass"))
						.define('C', "modern_industrialization:basic_machine_hull")
						.define('c', "modern_industrialization:tin_cable")
						.define('P', "modern_industrialization:pump")
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
						.define('C', "extended_industrialization:steel_combine")
						.define('A', "modern_industrialization:analog_circuit")
						.define('M', "modern_industrialization:motor")
						.define('P', "modern_industrialization:bronze_machine_casing_pipe")
						.pattern("BCB")
						.pattern("AMA")
						.pattern("BPB"),
				output
		);
		addBasicCraftingMachineRecipes(
				"farmer", "electric",
				(builder) -> builder
						.define('A', "modern_industrialization:robot_arm")
						.define('M', "modern_industrialization:large_motor")
						.define('C', "modern_industrialization:steel_machine_casing")
						.define('F', "extended_industrialization:steam_farmer")
						.define('P', "modern_industrialization:large_pump")
						.define('p', "modern_industrialization:steel_machine_casing_pipe")
						.pattern("AMA")
						.pattern("CFC")
						.pattern("PpP"),
				output
		);
	}
	
	private static void brewery(RecipeOutput output)
	{
		addSteelMachineRecipes(
				"brewery",
				(builder) -> builder
						.define('R', "modern_industrialization:bronze_rotor")
						.define('T', "modern_industrialization:steel_tank")
						.define('B', "minecraft:blaze_rod")
						.define('C', "modern_industrialization:steel_machine_casing")
						.define('P', MITags.FLUID_PIPES)
						.pattern("RTR")
						.pattern("BCB")
						.pattern("PPP"),
				output
		);
		addElectricMachineRecipes(
				"brewery",
				(builder) -> builder
						.define('R', "modern_industrialization:tin_rotor")
						.define('A', "modern_industrialization:analog_circuit")
						.define('B', "minecraft:blaze_rod")
						.define('C', "modern_industrialization:basic_machine_hull")
						.define('P', "modern_industrialization:pump")
						.pattern("RAR")
						.pattern("BCB")
						.pattern("PAP"),
				output
		);
	}
	
	private static void wasteCollector(RecipeOutput output)
	{
		addBronzeAndSteelMachineRecipes(
				"waste_collector",
				(builder) -> builder
						.define('B', "minecraft:iron_bars")
						.define('R', "modern_industrialization:copper_rotor")
						.define('C', "modern_industrialization:bronze_machine_casing")
						.define('P', MITags.FLUID_PIPES)
						.pattern("BBB")
						.pattern("RCR")
						.pattern("PPP"),
				output
		);
		addElectricMachineRecipes(
				"waste_collector",
				(builder) -> builder
						.define('B', "minecraft:iron_bars")
						.define('R', "modern_industrialization:tin_rotor")
						.define('C', "modern_industrialization:basic_machine_hull")
						.define('c', "modern_industrialization:tin_cable")
						.define('P', "modern_industrialization:pump")
						.pattern("BBB")
						.pattern("RCR")
						.pattern("cPc"),
				output
		);
	}
	
	private static void processingArray(RecipeOutput output)
	{
		addBasicCraftingMachineRecipes(
				"processing_array",
				(builder) -> builder
						.define('A', "modern_industrialization:robot_arm")
						.define('D', "modern_industrialization:digital_circuit")
						.define('C', "modern_industrialization:clean_stainless_steel_machine_casing")
						.define('S', "modern_industrialization:assembler")
						.pattern("ADA")
						.pattern("CSC")
						.pattern("ADA"),
				output
		);
	}
	
	private static void largeFurnace(RecipeOutput output)
	{
		addBasicCraftingMachineRecipes(
				"large_steam_furnace",
				(builder) -> builder
						.define('C', "modern_industrialization:bronze_curved_plate")
						.define('B', "modern_industrialization:bronze_plated_bricks")
						.define('F', "modern_industrialization:bronze_furnace")
						.define('M', "modern_industrialization:bronze_machine_casing")
						.define('R', "modern_industrialization:fire_clay_bricks")
						.pattern("CBC")
						.pattern("FMF")
						.pattern("RRR"),
				output
		);
		
		addBasicCraftingMachineRecipes(
				"large_electric_furnace",
				(builder) -> builder
						.define('C', "modern_industrialization:cupronickel_wire_magnetic")
						.define('E', "modern_industrialization:electronic_circuit")
						.define('F', "modern_industrialization:electric_furnace")
						.define('H', "modern_industrialization:advanced_machine_hull")
						.define('I', "modern_industrialization:heatproof_machine_casing")
						.pattern("CEC")
						.pattern("FHF")
						.pattern("IEI"),
				output
		);
	}
	
	private static void largeMacerator(RecipeOutput output)
	{
		addBasicCraftingMachineRecipes(
				"large_steam_macerator",
				(builder) -> builder
						.define('C', "modern_industrialization:bronze_curved_plate")
						.define('B', "modern_industrialization:bronze_plated_bricks")
						.define('F', "modern_industrialization:bronze_macerator")
						.define('M', "modern_industrialization:bronze_machine_casing")
						.pattern("CBC")
						.pattern("FMF")
						.pattern("CBC"),
				output
		);
		
		addBasicCraftingMachineRecipes(
				"large_electric_macerator",
				(builder) -> builder
						.define('I', "modern_industrialization:invar_rotary_blade")
						.define('E', "modern_industrialization:electronic_circuit")
						.define('M', "modern_industrialization:electric_macerator")
						.define('H', "modern_industrialization:advanced_machine_hull")
						.define('S', "extended_industrialization:steel_plated_bricks")
						.pattern("IEI")
						.pattern("MHM")
						.pattern("SES"),
				output
		);
	}
	
	private static void universalTransformer(RecipeOutput output)
	{
		addBasicCraftingMachineRecipes(
				"universal_transformer",
				(builder) -> builder
						.define('T', "modern_industrialization:tin_cable")
						.define('E', "modern_industrialization:electrum_cable")
						.define('A', "modern_industrialization:aluminum_cable")
						.define('H', "modern_industrialization:basic_machine_hull")
						.pattern("TEA")
						.pattern(" H ")
						.pattern("AET"),
				output
		);
	}
	
	private static void machineChainer(RecipeOutput output)
	{
		addBasicCraftingMachineRecipes(
				"machine_chainer",
				(builder) -> builder
						.define('M', "modern_industrialization:large_motor")
						.define('I', MITags.ITEM_PIPES)
						.define('U', "extended_industrialization:universal_transformer")
						.define('H', "modern_industrialization:advanced_machine_hull")
						.define('P', "modern_industrialization:large_pump")
						.define('F', MITags.FLUID_PIPES)
						.pattern("MIM")
						.pattern("UHU")
						.pattern("PFP"),
				output
		);
		
		addBasicCraftingMachineRecipes(
				"machine_chainer_relay",
				(builder) -> builder
						.define('P', EITags.itemForge("plates/iron"))
						.define('I', MITags.ITEM_PIPES)
						.define('C', "modern_industrialization:cupronickel_coil")
						.define('F', MITags.FLUID_PIPES)
						.pattern("PPP")
						.pattern("ICF")
						.pattern("PPP"),
				output
		);
	}
	
	private static void solarPanel(RecipeOutput output)
	{
		addBasicCraftingMachineRecipes(
				"lv_solar_panel",
				(builder) -> builder
						.define('R', "modern_industrialization:tin_rod")
						.define('C', "modern_industrialization:analog_circuit")
						.define('F', MITags.FLUID_PIPES)
						.define('H', "modern_industrialization:basic_machine_hull")
						.define('c', "modern_industrialization:tin_cable")
						.define('M', "modern_industrialization:motor")
						.pattern("RCR")
						.pattern("FHF")
						.pattern("cMc"),
				output
		);
		
		addBasicCraftingMachineRecipes(
				"mv_solar_panel",
				(builder) -> builder
						.define('R', "modern_industrialization:aluminum_rod")
						.define('C', "modern_industrialization:electronic_circuit")
						.define('F', MITags.FLUID_PIPES)
						.define('H', "modern_industrialization:advanced_machine_hull")
						.define('c', "modern_industrialization:electrum_cable")
						.define('M', "modern_industrialization:large_motor")
						.pattern("RCR")
						.pattern("FHF")
						.pattern("cMc"),
				output
		);
		
		addBasicCraftingMachineRecipes(
				"hv_solar_panel",
				(builder) -> builder
						.define('R', "modern_industrialization:stainless_steel_rod")
						.define('C', "modern_industrialization:digital_circuit")
						.define('F', MITags.FLUID_PIPES)
						.define('H', "modern_industrialization:turbo_machine_hull")
						.define('c', "modern_industrialization:aluminum_cable")
						.define('M', "modern_industrialization:advanced_motor")
						.pattern("RCR")
						.pattern("FHF")
						.pattern("cMc"),
				output
		);
	}
	
	private static void wirelessChargingStation(RecipeOutput output)
	{
		addBasicCraftingMachineRecipes(
				"local_wireless_charging_station",
				(builder) -> builder
						.define('C', "modern_industrialization:electronic_circuit")
						.define('c', "modern_industrialization:cupronickel_coil")
						.define('B', "modern_industrialization:silicon_battery")
						.define('H', "modern_industrialization:advanced_machine_hull")
						.define('w', "modern_industrialization:electrum_cable")
						.pattern("CcC")
						.pattern("BHB")
						.pattern("CwC"),
				output
		);
		
		addBasicCraftingMachineRecipes(
				"global_wireless_charging_station",
				(builder) -> builder
						.define('C', "modern_industrialization:digital_circuit")
						.define('c', "modern_industrialization:kanthal_coil")
						.define('B', "modern_industrialization:sodium_battery")
						.define('H', "modern_industrialization:turbo_machine_hull")
						.define('w', "modern_industrialization:aluminum_cable")
						.pattern("CcC")
						.pattern("BHB")
						.pattern("CwC"),
				output
		);
		
		addBasicCraftingMachineRecipes(
				"interdimensional_wireless_charging_station",
				(builder) -> builder
						.define('C', "modern_industrialization:processing_unit")
						.define('c', "modern_industrialization:superconductor_coil")
						.define('B', "modern_industrialization:cadmium_battery")
						.define('H', "modern_industrialization:highly_advanced_machine_hull")
						.define('w', "modern_industrialization:annealed_copper_cable")
						.pattern("CcC")
						.pattern("BHB")
						.pattern("CwC"),
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
		brewery(output);
		wasteCollector(output);
		processingArray(output);
		largeFurnace(output);
		largeMacerator(output);
		universalTransformer(output);
		machineChainer(output);
		solarPanel(output);
		wirelessChargingStation(output);
	}
}
