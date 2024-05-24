package net.swedz.extended_industrialization.machines;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import aztech.modern_industrialization.machines.components.OverclockComponent;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.guicomponents.ProgressBar;
import aztech.modern_industrialization.machines.guicomponents.RecipeEfficiencyBar;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.machines.init.SingleBlockCraftingMachines;
import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import com.google.common.collect.Maps;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.hook.mi.MIMachineHookHelper;
import net.swedz.extended_industrialization.machines.blockentities.MachineChainerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentities.SolarBoilerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentities.SolarPanelMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentities.UniversalTransformerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentities.brewery.ElectricBreweryMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentities.brewery.SteamBreweryMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentities.fluidharvesting.ElectricFluidHarvestingMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentities.fluidharvesting.SteamFluidHarvestingMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentities.multiblock.LargeElectricFurnaceBlockEntity;
import net.swedz.extended_industrialization.machines.blockentities.multiblock.ProcessingArrayBlockEntity;
import net.swedz.extended_industrialization.machines.blockentities.multiblock.farmer.ElectricFarmerBlockEntity;
import net.swedz.extended_industrialization.machines.blockentities.multiblock.farmer.SteamFarmerBlockEntity;
import net.swedz.extended_industrialization.machines.blockentities.multiblock.multiplied.ElectricMultipliedCraftingMultiblockBlockEntity;
import net.swedz.extended_industrialization.machines.blockentities.multiblock.multiplied.SteamMultipliedCraftingMultiblockBlockEntity;
import net.swedz.extended_industrialization.machines.components.craft.multiplied.EuCostTransformers;
import net.swedz.extended_industrialization.machines.components.fluidharvesting.honeyextractor.HoneyExtractorBehavior;
import net.swedz.extended_industrialization.machines.components.fluidharvesting.wastecollector.WasteCollectorBehavior;
import net.swedz.extended_industrialization.registry.blocks.EIBlocks;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;

import java.util.List;
import java.util.Map;

import static aztech.modern_industrialization.machines.init.SingleBlockCraftingMachines.*;
import static aztech.modern_industrialization.machines.models.MachineCasings.*;

public final class EIMachines
{
	public static final class Casings
	{
		public static MachineCasing
				BRONZE_PIPE,
				STEEL_PIPE,
				STEEL_PLATED_BRICKS;
	}
	
	public static void casings()
	{
		Casings.BRONZE_PIPE = MachineCasings.create("bronze_pipe");
		Casings.STEEL_PIPE = MachineCasings.create("steel_pipe");
		Casings.STEEL_PLATED_BRICKS = MachineCasings.create("steel_plated_bricks");
	}
	
	public static void blastFurnaceTiers(List<ElectricBlastFurnaceBlockEntity.Tier> list)
	{
	}
	
	public static void multiblocks()
	{
		MIMachineHookHelper.registerMultiblockMachine(
				"Steam Farmer", "steam_farmer", "farmer",
				BRONZE_PLATED_BRICKS, true, true, false,
				SteamFarmerBlockEntity::new,
				(__) -> SteamFarmerBlockEntity.registerReiShapes()
		);
		
		MIMachineHookHelper.registerMultiblockMachine(
				"Electric Farmer", "electric_farmer", "farmer",
				STEEL, true, true, false,
				ElectricFarmerBlockEntity::new,
				(__) -> ElectricFarmerBlockEntity.registerReiShapes()
		);
		
		MIMachineHookHelper.registerMultiblockMachine(
				"Processing Array", "processing_array", "processing_array",
				CLEAN_STAINLESS_STEEL, true, false, false,
				ProcessingArrayBlockEntity::new,
				(__) -> ProcessingArrayBlockEntity.registerReiShapes()
		);
		
		{
			SimpleMember fireClayBricks = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("fire_clay_bricks")));
			SimpleMember bronzePlatedBricks = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("bronze_plated_bricks")));
			HatchFlags hatches = new HatchFlags.Builder().with(HatchType.ITEM_INPUT, HatchType.ITEM_OUTPUT, HatchType.FLUID_INPUT).build();
			ShapeTemplate shape = new ShapeTemplate.Builder(BRONZE_PLATED_BRICKS)
					.add3by3(-1, fireClayBricks, false, hatches)
					.add3by3(0, bronzePlatedBricks, true, HatchFlags.NO_HATCH)
					.add3by3(1, bronzePlatedBricks, false, HatchFlags.NO_HATCH)
					.build();
			MIMachineHookHelper.registerMultiblockMachine(
					"Large Steam Furnace", "large_steam_furnace", "large_furnace",
					BRONZE_PLATED_BRICKS, true, false, false,
					(bep) -> new SteamMultipliedCraftingMultiblockBlockEntity(
							bep, "large_steam_furnace", new ShapeTemplate[]{shape},
							OverclockComponent.getDefaultCatalysts(),
							MIMachineRecipeTypes.FURNACE, 8, EuCostTransformers.percentage(() -> 0.75f)
					)
			);
			ReiMachineRecipes.registerMultiblockShape("large_steam_furnace", shape);
			ReiMachineRecipes.registerWorkstation("bronze_furnace", EI.id("large_steam_furnace"));
			ReiMachineRecipes.registerWorkstation("steel_furnace", EI.id("large_steam_furnace"));
		}
		
		MIMachineHookHelper.registerMultiblockMachine(
				"Large Electric Furnace", "large_electric_furnace", "large_furnace",
				HEATPROOF, true, false, false,
				LargeElectricFurnaceBlockEntity::new
		);
		ReiMachineRecipes.registerWorkstation("bronze_furnace", EI.id("large_electric_furnace"));
		ReiMachineRecipes.registerWorkstation("steel_furnace", EI.id("large_electric_furnace"));
		ReiMachineRecipes.registerWorkstation("electric_furnace", EI.id("large_electric_furnace"));
		
		{
			SimpleMember bronzePlatedBricks = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("bronze_plated_bricks")));
			HatchFlags hatches = new HatchFlags.Builder().with(HatchType.ITEM_INPUT, HatchType.ITEM_OUTPUT, HatchType.FLUID_INPUT).build();
			ShapeTemplate shape = new ShapeTemplate.Builder(BRONZE_PLATED_BRICKS).add3by3LevelsRoofed(-1, 1, bronzePlatedBricks, hatches).build();
			MIMachineHookHelper.registerMultiblockMachine(
					"Large Steam Macerator", "large_steam_macerator", "large_macerator",
					BRONZE_PLATED_BRICKS, true, false, false,
					(bep) -> new SteamMultipliedCraftingMultiblockBlockEntity(
							bep, "large_steam_macerator", new ShapeTemplate[]{shape},
							OverclockComponent.getDefaultCatalysts(),
							MIMachineRecipeTypes.MACERATOR, 8, EuCostTransformers.percentage(() -> 0.75f)
					)
			);
			ReiMachineRecipes.registerMultiblockShape("large_steam_macerator", shape);
			ReiMachineRecipes.registerWorkstation("bronze_macerator", EI.id("large_steam_macerator"));
			ReiMachineRecipes.registerWorkstation("steel_macerator", EI.id("large_steam_macerator"));
		}
		
		{
			SimpleMember steelPlatedBricks = SimpleMember.forBlock(EIBlocks.STEEL_PLATED_BRICKS);
			HatchFlags hatches = new HatchFlags.Builder().with(HatchType.ITEM_INPUT, HatchType.ITEM_OUTPUT, HatchType.ENERGY_INPUT).build();
			ShapeTemplate shape = new ShapeTemplate.Builder(Casings.STEEL_PLATED_BRICKS).add3by3LevelsRoofed(-1, 1, steelPlatedBricks, hatches).build();
			MIMachineHookHelper.registerMultiblockMachine(
					"Large Electric Macerator", "large_electric_macerator", "large_macerator",
					Casings.STEEL_PLATED_BRICKS, true, false, false,
					(bep) -> new ElectricMultipliedCraftingMultiblockBlockEntity(
							bep, "large_electric_macerator", new ShapeTemplate[]{shape},
							MachineTier.MULTIBLOCK,
							MIMachineRecipeTypes.MACERATOR, 16, EuCostTransformers.percentage(() -> 0.75f)
					)
			);
			ReiMachineRecipes.registerMultiblockShape("large_electric_macerator", shape);
			ReiMachineRecipes.registerWorkstation("bronze_macerator", EI.id("large_electric_macerator"));
			ReiMachineRecipes.registerWorkstation("steel_macerator", EI.id("large_electric_macerator"));
			ReiMachineRecipes.registerWorkstation("electric_macerator", EI.id("large_electric_macerator"));
		}
	}
	
	public static final class RecipeTypes
	{
		public static MachineRecipeType
				BENDING_MACHINE,
				ALLOY_SMELTER,
				CANNING_MACHINE,
				COMPOSTER;
		
		private static final Map<MachineRecipeType, String> RECIPE_TYPE_NAMES = Maps.newHashMap();
		
		public static Map<MachineRecipeType, String> getNames()
		{
			return RECIPE_TYPE_NAMES;
		}
		
		private static MachineRecipeType create(String englishName, String id)
		{
			MachineRecipeType recipeType = MIMachineRecipeTypes.create(id);
			RECIPE_TYPE_NAMES.put(recipeType, englishName);
			return recipeType;
		}
	}
	
	public static void recipeTypes()
	{
		RecipeTypes.BENDING_MACHINE = RecipeTypes.create("Bending Machine", "bending_machine").withItemInputs().withItemOutputs();
		RecipeTypes.ALLOY_SMELTER = RecipeTypes.create("Alloy Smelter", "alloy_smelter").withItemInputs().withItemOutputs();
		RecipeTypes.CANNING_MACHINE = RecipeTypes.create("Canning Machine", "canning_machine").withItemInputs().withFluidInputs().withItemOutputs().withFluidOutputs();
		RecipeTypes.COMPOSTER = RecipeTypes.create("Composter", "composter").withItemInputs().withFluidInputs().withItemOutputs().withFluidOutputs();
	}
	
	public static void singleBlockCrafting()
	{
		// @formatter:off
		
		SingleBlockCraftingMachines.registerMachineTiers(
				"Bending Machine", "bending_machine", RecipeTypes.BENDING_MACHINE,
				1, 1, 0, 0,
				(params) -> {},
				new ProgressBar.Parameters(77, 34, "compress"),
				new RecipeEfficiencyBar.Parameters(38, 62),
				new EnergyBar.Parameters(18, 30),
				(items) -> items.addSlot(56, 35).addSlot(102, 35),
				(fluids) -> {},
				true, false, false,
				TIER_BRONZE | TIER_STEEL | TIER_ELECTRIC,
				16
		);
		
		SingleBlockCraftingMachines.registerMachineTiers(
				"Alloy Smelter", "alloy_smelter", RecipeTypes.ALLOY_SMELTER,
				2, 1, 0, 0,
				(params) -> {},
				new ProgressBar.Parameters(88, 33, "arrow"),
				new RecipeEfficiencyBar.Parameters(38, 62),
				new EnergyBar.Parameters(14, 34),
				(items) -> items.addSlots(40, 35, 2, 1).addSlot(120, 35),
				(fluids) -> {},
				true, false, false,
				TIER_STEEL | TIER_ELECTRIC,
				16
		);
		
		SingleBlockCraftingMachines.registerMachineTiers(
				"Canning Machine", "canning_machine", RecipeTypes.CANNING_MACHINE,
				2, 2, 1, 1,
				(params) -> {},
				new ProgressBar.Parameters(79, 34, "canning"),
				new RecipeEfficiencyBar.Parameters(38, 66),
				new EnergyBar.Parameters(14, 35),
				(items) -> items.addSlots(58, 27, 1, 2).addSlots(102, 27, 1, 2),
				(fluids) -> fluids.addSlot(38, 27).addSlot(122, 27),
				true, false, true,
				TIER_STEEL | TIER_ELECTRIC,
				16
		);
		
		SingleBlockCraftingMachines.registerMachineTiers(
				"Composter", "composter", RecipeTypes.COMPOSTER,
				2, 2, 1, 1,
				(params) -> {},
				new ProgressBar.Parameters(78, 34, "centrifuge"),
				new RecipeEfficiencyBar.Parameters(38, 66),
				new EnergyBar.Parameters(14, 35),
				(items) -> items.addSlots(58, 27, 1, 2).addSlots(102, 27, 1, 2),
				(fluids) -> fluids.addSlot(38, 27).addSlot(122, 27),
				true, true, false,
				TIER_BRONZE | TIER_STEEL | TIER_ELECTRIC,
				16
		);
		
		// @formatter:on
	}
	
	public static void singleBlockSpecial()
	{
		MIMachineHookHelper.registerSingleBlockSpecialMachine(
				"Bronze Solar Boiler", "bronze_solar_boiler", "solar_boiler",
				MachineCasings.BRICKED_BRONZE, true, true, false,
				(bep) -> new SolarBoilerMachineBlockEntity(bep, true),
				MachineBlockEntity::registerFluidApi
		);
		MIMachineHookHelper.registerSingleBlockSpecialMachine(
				"Steel Solar Boiler", "steel_solar_boiler", "solar_boiler",
				MachineCasings.BRICKED_STEEL, true, true, false,
				(bep) -> new SolarBoilerMachineBlockEntity(bep, false),
				MachineBlockEntity::registerFluidApi
		);
		
		MIMachineHookHelper.registerSingleBlockSpecialMachine(
				"Steel Honey Extractor", "steel_honey_extractor", "honey_extractor",
				MachineCasings.STEEL, true, false, true,
				(bep) -> new SteamFluidHarvestingMachineBlockEntity(
						bep, "steel_honey_extractor",
						2, HoneyExtractorBehavior.STEEL,
						16 * FluidType.BUCKET_VOLUME, EIFluids.HONEY
				),
				MachineBlockEntity::registerFluidApi
		);
		MIMachineHookHelper.registerSingleBlockSpecialMachine(
				"Electric Honey Extractor", "electric_honey_extractor", "honey_extractor",
				CableTier.LV.casing, true, false, true,
				(bep) -> new ElectricFluidHarvestingMachineBlockEntity(
						bep, "electric_honey_extractor",
						4, HoneyExtractorBehavior.ELECTRIC,
						32 * FluidType.BUCKET_VOLUME, EIFluids.HONEY
				),
				MachineBlockEntity::registerFluidApi,
				ElectricFluidHarvestingMachineBlockEntity::registerEnergyApi
		);
		
		MIMachineHookHelper.registerSingleBlockSpecialMachine(
				"Steel Brewery", "steel_brewery", "brewery",
				MachineCasings.STEEL, true, false, true,
				(bep) -> new SteamBreweryMachineBlockEntity(bep, false),
				MachineBlockEntity::registerItemApi,
				MachineBlockEntity::registerFluidApi
		);
		MIMachineHookHelper.registerSingleBlockSpecialMachine(
				"Electric Brewery", "electric_brewery", "brewery",
				CableTier.LV.casing, true, false, true,
				ElectricBreweryMachineBlockEntity::new,
				MachineBlockEntity::registerItemApi,
				MachineBlockEntity::registerFluidApi,
				ElectricBreweryMachineBlockEntity::registerEnergyApi
		);
		
		MIMachineHookHelper.registerSingleBlockSpecialMachine(
				"Bronze Waste Collector", "bronze_waste_collector", "waste_collector",
				MachineCasings.BRONZE, false, true, false,
				(bep) -> new SteamFluidHarvestingMachineBlockEntity(
						bep, "bronze_waste_collector",
						1, WasteCollectorBehavior.BRONZE,
						8 * FluidType.BUCKET_VOLUME, EIFluids.MANURE
				),
				MachineBlockEntity::registerFluidApi
		);
		MIMachineHookHelper.registerSingleBlockSpecialMachine(
				"Steel Waste Collector", "steel_waste_collector", "waste_collector",
				MachineCasings.STEEL, false, true, false,
				(bep) -> new SteamFluidHarvestingMachineBlockEntity(
						bep, "steel_waste_collector",
						2, WasteCollectorBehavior.STEEL,
						16 * FluidType.BUCKET_VOLUME, EIFluids.MANURE
				),
				MachineBlockEntity::registerFluidApi
		);
		MIMachineHookHelper.registerSingleBlockSpecialMachine(
				"Electric Waste Collector", "electric_waste_collector", "waste_collector",
				CableTier.LV.casing, false, true, false,
				(bep) -> new ElectricFluidHarvestingMachineBlockEntity(
						bep, "electric_waste_collector",
						4, WasteCollectorBehavior.ELECTRIC,
						32 * FluidType.BUCKET_VOLUME, EIFluids.MANURE
				),
				MachineBlockEntity::registerFluidApi,
				ElectricFluidHarvestingMachineBlockEntity::registerEnergyApi
		);
		
		MIMachineHookHelper.registerSingleBlockSpecialMachine(
				"Machine Chainer", "machine_chainer",
				MachineChainerMachineBlockEntity::new,
				MachineChainerMachineBlockEntity::registerCapabilities
		);
		
		MIMachineHookHelper.registerSingleBlockSpecialMachine(
				"Universal Transformer", "universal_transformer", "universal_transformer",
				CableTier.LV.casing, true, true, true, false,
				UniversalTransformerMachineBlockEntity::new,
				UniversalTransformerMachineBlockEntity::registerEnergyApi
		);
		
		for(CableTier tier : new CableTier[]{CableTier.LV, CableTier.MV, CableTier.HV})
		{
			String name = "%s Solar Panel".formatted(tier.shortEnglishName);
			String id = "%s_solar_panel".formatted(tier.name);
			String overlayFolder = "solar_panel/%s".formatted(tier.name);
			MIMachineHookHelper.registerSingleBlockSpecialMachine(
					name, id, overlayFolder,
					tier.casing, false, true, true, false,
					(bep) -> new SolarPanelMachineBlockEntity(bep, id, tier),
					MachineBlockEntity::registerItemApi,
					MachineBlockEntity::registerFluidApi,
					SolarPanelMachineBlockEntity::registerEnergyApi
			);
		}
	}
}
