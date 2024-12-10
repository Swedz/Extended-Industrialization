package net.swedz.extended_industrialization;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.OverclockComponent;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.guicomponents.ProgressBar;
import aztech.modern_industrialization.machines.guicomponents.RecipeEfficiencyBar;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.materials.MIMaterials;
import aztech.modern_industrialization.materials.part.MIParts;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.machines.blockentity.LargeConfigurableChestMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.SolarBoilerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.SolarPanelMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.UniversalTransformerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.WirelessChargerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.brewery.ElectricBreweryMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.brewery.SteamBreweryMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.fluidharvesting.ElectricFluidHarvestingMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.fluidharvesting.SteamFluidHarvestingMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.LargeElectricFurnaceBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.ProcessingArrayBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.farmer.ElectricFarmerBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.farmer.SteamFarmerBlockEntity;
import net.swedz.extended_industrialization.machines.component.fluidharvesting.honeyextractor.HoneyExtractorBehavior;
import net.swedz.extended_industrialization.machines.component.fluidharvesting.wastecollector.WasteCollectorBehavior;
import net.swedz.extended_industrialization.machines.recipe.BreweryMachineRecipeType;
import net.swedz.extended_industrialization.machines.recipe.CanningMachineRecipeType;
import net.swedz.extended_industrialization.machines.recipe.ComposterMachineRecipeType;
import net.swedz.tesseract.neoforge.compat.mi.component.craft.multiplied.EuCostTransformers;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.BlastFurnaceTiersMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.MachineCasingsMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.MachineRecipeTypesMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.MultiblockMachinesMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.SingleBlockCraftingMachinesMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.SingleBlockSpecialMachinesMIHookContext;
import net.swedz.tesseract.neoforge.compat.mi.machine.blockentity.multiblock.multiplied.ElectricMultipliedCraftingMultiblockBlockEntity;
import net.swedz.tesseract.neoforge.compat.mi.machine.blockentity.multiblock.multiplied.SteamMultipliedCraftingMultiblockBlockEntity;

import java.util.Map;
import java.util.function.Function;

import static aztech.modern_industrialization.machines.init.SingleBlockCraftingMachines.*;
import static aztech.modern_industrialization.machines.models.MachineCasings.*;

public final class EIMachines
{
	public static void blastFurnaceTiers(BlastFurnaceTiersMIHookContext hook)
	{
	}
	
	public static final class Casings
	{
		public static MachineCasing
				BRONZE_PIPE,
				STEEL_PIPE,
				STEEL_PLATED_BRICKS,
				LARGE_STEEL_CRATE;
	}
	
	public static void casings(MachineCasingsMIHookContext hook)
	{
		Casings.BRONZE_PIPE = hook.registerImitateBlock("bronze_pipe", () -> MIMaterials.BRONZE.getPart(MIParts.MACHINE_CASING_PIPE).asBlock());
		Casings.STEEL_PIPE = hook.registerImitateBlock("steel_pipe", () -> MIMaterials.STEEL.getPart(MIParts.MACHINE_CASING_PIPE).asBlock());
		Casings.STEEL_PLATED_BRICKS = hook.registerImitateBlock("steel_plated_bricks", EIBlocks.STEEL_PLATED_BRICKS);
		Casings.LARGE_STEEL_CRATE = hook.registerCubeAll("large_steel_crate", "Large Steel Crate", EI.id("block/casings/large_steel_crate"));
	}
	
	public static final class RecipeTypes
	{
		public static MachineRecipeType
				BENDING_MACHINE,
				ALLOY_SMELTER,
				CANNING_MACHINE,
				COMPOSTER,
				BREWERY;
		
		private static final Map<MachineRecipeType, String> RECIPE_TYPE_NAMES = Maps.newHashMap();
		
		public static Map<MachineRecipeType, String> getNames()
		{
			return RECIPE_TYPE_NAMES;
		}
		
		private static MachineRecipeType create(MachineRecipeTypesMIHookContext hook, String englishName, String id, Function<ResourceLocation, MachineRecipeType> creator)
		{
			MachineRecipeType recipeType = hook.create(id, creator);
			RECIPE_TYPE_NAMES.put(recipeType, englishName);
			return recipeType;
		}
		
		private static MachineRecipeType create(MachineRecipeTypesMIHookContext hook, String englishName, String id)
		{
			return create(hook, englishName, id, MachineRecipeType::new);
		}
	}
	
	public static void recipeTypes(MachineRecipeTypesMIHookContext hook)
	{
		RecipeTypes.BENDING_MACHINE = RecipeTypes.create(hook, "Bending Machine", "bending_machine").withItemInputs().withItemOutputs();
		RecipeTypes.ALLOY_SMELTER = RecipeTypes.create(hook, "Alloy Smelter", "alloy_smelter").withItemInputs().withItemOutputs();
		RecipeTypes.CANNING_MACHINE = RecipeTypes.create(hook, "Canning Machine", "canning_machine", CanningMachineRecipeType::new).withItemInputs().withFluidInputs().withItemOutputs().withFluidOutputs();
		RecipeTypes.COMPOSTER = RecipeTypes.create(hook, "Composter", "composter", ComposterMachineRecipeType::new).withItemInputs().withFluidInputs().withItemOutputs().withFluidOutputs();
		RecipeTypes.BREWERY = RecipeTypes.create(hook, "Brewery", "brewery", BreweryMachineRecipeType::new).withItemInputs().withFluidInputs().withItemOutputs();
	}
	
	public static void multiblocks(MultiblockMachinesMIHookContext hook)
	{
		hook.register(
				"Steam Farmer", "steam_farmer", "farmer",
				BRONZE_PLATED_BRICKS, true, true, false,
				SteamFarmerBlockEntity::new,
				(__) -> SteamFarmerBlockEntity.registerReiShapes()
		);
		
		hook.register(
				"Electric Farmer", "electric_farmer", "farmer",
				STEEL, true, true, false,
				ElectricFarmerBlockEntity::new,
				(__) -> ElectricFarmerBlockEntity.registerReiShapes()
		);
		
		hook.register(
				"Processing Array", "processing_array", "processing_array",
				CLEAN_STAINLESS_STEEL, true, false, false,
				ProcessingArrayBlockEntity::new,
				(__) -> ProcessingArrayBlockEntity.registerReiShapes()
		);
		
		{
			SimpleMember fireClayBricks = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("fire_clay_bricks")));
			SimpleMember bronzePlatedBricks = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("bronze_plated_bricks")));
			HatchFlags hatches = new HatchFlags.Builder().with(HatchType.ITEM_INPUT, HatchType.ITEM_OUTPUT, HatchType.FLUID_INPUT).build();
			ShapeTemplate shape = new ShapeTemplate.Builder(FIREBRICKS)
					.add3by3(-1, fireClayBricks, false, hatches)
					.add3by3(0, bronzePlatedBricks, true, HatchFlags.NO_HATCH)
					.add3by3(1, bronzePlatedBricks, false, HatchFlags.NO_HATCH)
					.build();
			hook.register(
					"Large Steam Furnace", "large_steam_furnace", "large_furnace",
					BRONZE_PLATED_BRICKS, true, false, false,
					(bep) -> new SteamMultipliedCraftingMultiblockBlockEntity(
							bep, EI.id("large_steam_furnace"), new ShapeTemplate[]{shape},
							OverclockComponent.getDefaultCatalysts(),
							MIMachineRecipeTypes.FURNACE,
							EIConfig.largeSteamFurnaceBatchSize,
							EuCostTransformers.percentage(() -> (float) EIConfig.largeSteamFurnaceEuCostMultiplier)
					)
			);
			ReiMachineRecipes.registerMultiblockShape(EI.id("large_steam_furnace"), shape);
			ReiMachineRecipes.registerWorkstation(MI.id("bronze_furnace"), EI.id("large_steam_furnace"));
			ReiMachineRecipes.registerWorkstation(MI.id("steel_furnace"), EI.id("large_steam_furnace"));
		}
		
		hook.register(
				"Large Electric Furnace", "large_electric_furnace", "large_furnace",
				HEATPROOF, true, false, false,
				LargeElectricFurnaceBlockEntity::new
		);
		ReiMachineRecipes.registerWorkstation(MI.id("bronze_furnace"), EI.id("large_electric_furnace"));
		ReiMachineRecipes.registerWorkstation(MI.id("steel_furnace"), EI.id("large_electric_furnace"));
		ReiMachineRecipes.registerWorkstation(MI.id("electric_furnace"), EI.id("large_electric_furnace"));
		
		{
			SimpleMember bronzePlatedBricks = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("bronze_plated_bricks")));
			HatchFlags hatches = new HatchFlags.Builder().with(HatchType.ITEM_INPUT, HatchType.ITEM_OUTPUT, HatchType.FLUID_INPUT).build();
			ShapeTemplate shape = new ShapeTemplate.Builder(BRONZE_PLATED_BRICKS).add3by3LevelsRoofed(-1, 1, bronzePlatedBricks, hatches).build();
			hook.register(
					"Large Steam Macerator", "large_steam_macerator", "large_macerator",
					BRONZE_PLATED_BRICKS, true, false, false,
					(bep) -> new SteamMultipliedCraftingMultiblockBlockEntity(
							bep, EI.id("large_steam_macerator"), new ShapeTemplate[]{shape},
							OverclockComponent.getDefaultCatalysts(),
							MIMachineRecipeTypes.MACERATOR,
							EIConfig.largeSteamMaceratorBatchSize,
							EuCostTransformers.percentage(() -> (float) EIConfig.largeSteamMaceratorEuCostMultiplier)
					)
			);
			ReiMachineRecipes.registerMultiblockShape(EI.id("large_steam_macerator"), shape);
			ReiMachineRecipes.registerWorkstation(MI.id("bronze_macerator"), EI.id("large_steam_macerator"));
			ReiMachineRecipes.registerWorkstation(MI.id("steel_macerator"), EI.id("large_steam_macerator"));
		}
		
		{
			SimpleMember steelPlatedBricks = SimpleMember.forBlock(EIBlocks.STEEL_PLATED_BRICKS);
			HatchFlags hatches = new HatchFlags.Builder().with(HatchType.ITEM_INPUT, HatchType.ITEM_OUTPUT, HatchType.ENERGY_INPUT).build();
			ShapeTemplate shape = new ShapeTemplate.Builder(Casings.STEEL_PLATED_BRICKS).add3by3LevelsRoofed(-1, 1, steelPlatedBricks, hatches).build();
			hook.register(
					"Large Electric Macerator", "large_electric_macerator", "large_macerator",
					Casings.STEEL_PLATED_BRICKS, true, false, false,
					(bep) -> new ElectricMultipliedCraftingMultiblockBlockEntity(
							bep, EI.id("large_electric_macerator"), new ShapeTemplate[]{shape},
							MachineTier.LV,
							MIMachineRecipeTypes.MACERATOR,
							EIConfig.largeElectricMaceratorBatchSize,
							EuCostTransformers.percentage(() -> (float) EIConfig.largeElectricMaceratorEuCostMultiplier)
					)
			);
			ReiMachineRecipes.registerMultiblockShape(EI.id("large_electric_macerator"), shape);
			ReiMachineRecipes.registerWorkstation(MI.id("bronze_macerator"), EI.id("large_electric_macerator"));
			ReiMachineRecipes.registerWorkstation(MI.id("steel_macerator"), EI.id("large_electric_macerator"));
			ReiMachineRecipes.registerWorkstation(MI.id("electric_macerator"), EI.id("large_electric_macerator"));
		}
	}
	
	public static void singleBlockCrafting(SingleBlockCraftingMachinesMIHookContext hook)
	{
		// @formatter:off
		
		hook.register(
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
		
		hook.register(
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
		
		hook.register(
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
		
		hook.register(
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
	
	public static void singleBlockSpecial(SingleBlockSpecialMachinesMIHookContext hook)
	{
		hook.register(
				"Bronze Solar Boiler", "bronze_solar_boiler", "solar_boiler",
				MachineCasings.BRICKED_BRONZE, true, true, false,
				(bep) -> new SolarBoilerMachineBlockEntity(bep, true),
				MachineBlockEntity::registerFluidApi
		);
		hook.register(
				"Steel Solar Boiler", "steel_solar_boiler", "solar_boiler",
				MachineCasings.BRICKED_STEEL, true, true, false,
				(bep) -> new SolarBoilerMachineBlockEntity(bep, false),
				MachineBlockEntity::registerFluidApi
		);
		
		hook.register(
				"Steel Honey Extractor", "steel_honey_extractor", "honey_extractor",
				MachineCasings.STEEL, true, false, true,
				(bep) -> new SteamFluidHarvestingMachineBlockEntity(
						bep, "steel_honey_extractor",
						2, HoneyExtractorBehavior.STEEL,
						16 * FluidType.BUCKET_VOLUME, EIFluids.HONEY
				),
				MachineBlockEntity::registerFluidApi
		);
		hook.register(
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
		
		hook.register(
				"Steel Brewery", "steel_brewery", "brewery",
				MachineCasings.STEEL, true, false, true,
				(bep) -> new SteamBreweryMachineBlockEntity(bep, false),
				MachineBlockEntity::registerItemApi,
				MachineBlockEntity::registerFluidApi
		);
		hook.register(
				"Electric Brewery", "electric_brewery", "brewery",
				CableTier.LV.casing, true, false, true,
				ElectricBreweryMachineBlockEntity::new,
				MachineBlockEntity::registerItemApi,
				MachineBlockEntity::registerFluidApi,
				ElectricBreweryMachineBlockEntity::registerEnergyApi
		);
		
		hook.register(
				"Bronze Waste Collector", "bronze_waste_collector", "waste_collector",
				MachineCasings.BRONZE, false, true, false,
				(bep) -> new SteamFluidHarvestingMachineBlockEntity(
						bep, "bronze_waste_collector",
						1, WasteCollectorBehavior.BRONZE,
						8 * FluidType.BUCKET_VOLUME, EIFluids.MANURE
				),
				MachineBlockEntity::registerFluidApi
		);
		hook.register(
				"Steel Waste Collector", "steel_waste_collector", "waste_collector",
				MachineCasings.STEEL, false, true, false,
				(bep) -> new SteamFluidHarvestingMachineBlockEntity(
						bep, "steel_waste_collector",
						2, WasteCollectorBehavior.STEEL,
						16 * FluidType.BUCKET_VOLUME, EIFluids.MANURE
				),
				MachineBlockEntity::registerFluidApi
		);
		hook.register(
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
		
		hook.register(
				"Machine Chainer", "machine_chainer",
				MachineChainerMachineBlockEntity::new,
				MachineChainerMachineBlockEntity::registerCapabilities
		);
		
		hook.register(
				"Universal Transformer", "universal_transformer", "universal_transformer",
				CableTier.LV.casing, false, true, true, false,
				UniversalTransformerMachineBlockEntity::new,
				UniversalTransformerMachineBlockEntity::registerEnergyApi
		);
		
		for(CableTier tier : new CableTier[]{CableTier.LV, CableTier.MV, CableTier.HV})
		{
			String name = "%s Solar Panel".formatted(tier.shortEnglishName);
			String id = "%s_solar_panel".formatted(tier.name);
			String overlayFolder = "solar_panel/%s".formatted(tier.name);
			hook.register(
					name, id, overlayFolder,
					tier.casing, false, true, true, false,
					(bep) -> new SolarPanelMachineBlockEntity(bep, EI.id(id), tier),
					MachineBlockEntity::registerItemApi,
					MachineBlockEntity::registerFluidApi,
					SolarPanelMachineBlockEntity::registerEnergyApi
			);
		}
		
		hook.register(
				"Local Wireless Charging Station", "local_wireless_charging_station", "wireless_charging_station/local",
				CableTier.MV.casing, false, true, true, false,
				(bep) -> new WirelessChargerMachineBlockEntity(bep, EI.id("local_wireless_charging_station"), CableTier.MV, (m, p) -> m.getBlockPos().closerThan(p.blockPosition(), EIConfig.localWirelessChargingStationRange)),
				WirelessChargerMachineBlockEntity::registerEnergyApi
		);
		hook.register(
				"Global Wireless Charging Station", "global_wireless_charging_station", "wireless_charging_station/global",
				CableTier.HV.casing, false, true, true, false,
				(bep) -> new WirelessChargerMachineBlockEntity(bep, EI.id("global_wireless_charging_station"), CableTier.HV, (m, p) -> m.getLevel() == p.level()),
				WirelessChargerMachineBlockEntity::registerEnergyApi
		);
		hook.register(
				"Interdimensional Wireless Charging Station", "interdimensional_wireless_charging_station", "wireless_charging_station/interdimensional",
				CableTier.EV.casing, false, true, true, false,
				(bep) -> new WirelessChargerMachineBlockEntity(bep, EI.id("interdimensional_wireless_charging_station"), CableTier.EV, (m, p) -> true),
				WirelessChargerMachineBlockEntity::registerEnergyApi
		);
		
		hook.register(
				"Large Configurable Chest", "large_configurable_chest", "large_configurable_chest",
				Casings.LARGE_STEEL_CRATE, false, false, false, false,
				LargeConfigurableChestMachineBlockEntity::new,
				MachineBlockEntity::registerItemApi
		);
	}
}
