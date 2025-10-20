package net.swedz.extended_industrialization;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.compat.rei.machines.SteamMode;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.OverclockComponent;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.materials.MIMaterials;
import aztech.modern_industrialization.materials.part.MIParts;
import aztech.modern_industrialization.util.MobSpawning;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.machines.blockentity.LargeConfigurableChestMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.SolarBoilerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.SolarPanelMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.UniversalTransformerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.brewery.ElectricBreweryMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.brewery.SteamBreweryMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.fluidharvesting.ElectricFluidHarvestingMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.fluidharvesting.SteamFluidHarvestingMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.LargeElectricFurnaceBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.ProcessingArrayBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.farmer.ElectricFarmerBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.farmer.SteamFarmerBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower.TeslaTowerBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.tesla.LethalTeslaCoilMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.tesla.TeslaCoilMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.tesla.TeslaParticleGeneratorMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.tesla.TeslaReceiverHatchBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.tesla.TeslaReceiverMachineBlockEntity;
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
		hook.builder("steam_farmer", "Steam Farmer", SteamFarmerBlockEntity::new)
				.builtinModel(BRONZE_PLATED_BRICKS, "farmer", (model) -> model.front(true).top(true).active(true))
				.registrator((__) -> SteamFarmerBlockEntity.registerReiShapes())
				.registerMachine();
		
		hook.builder("electric_farmer", "Electric Farmer", ElectricFarmerBlockEntity::new)
				.builtinModel(STEEL, "farmer", (model) -> model.front(true).top(true).active(true))
				.registrator((__) -> ElectricFarmerBlockEntity.registerReiShapes())
				.registerMachine();
		
		hook.builder("processing_array", "Processing Array", ProcessingArrayBlockEntity::new)
				.builtinModel(CLEAN_STAINLESS_STEEL, "processing_array", (model) -> model.front(true).active(true))
				.registrator((__) -> ProcessingArrayBlockEntity.registerReiShapes())
				.registerMachine();
		
		{
			SimpleMember fireClayBricks = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("fire_clay_bricks")));
			SimpleMember bronzePlatedBricks = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("bronze_plated_bricks")));
			HatchFlags hatches = new HatchFlags.Builder().with(HatchTypes.ITEM_INPUT, HatchTypes.ITEM_OUTPUT, HatchTypes.FLUID_INPUT).build();
			ShapeTemplate shape = new ShapeTemplate.Builder(FIREBRICKS)
					.add3by3(-1, fireClayBricks, false, hatches)
					.add3by3(0, bronzePlatedBricks, true, HatchFlags.NO_HATCH)
					.add3by3(1, bronzePlatedBricks, false, HatchFlags.NO_HATCH)
					.build();
			hook.builder("large_steam_furnace", "Large Steam Furnace", (bep) -> new SteamMultipliedCraftingMultiblockBlockEntity(
							bep, EI.id("large_steam_furnace"), new ShapeTemplate[]{shape},
							OverclockComponent.getDefaultCatalysts(),
							MIMachineRecipeTypes.FURNACE,
							EI.config().batchingMachines().largeSteamFurnaceSize(),
							EuCostTransformers.percentage(() -> (float) EI.config().batchingMachines().largeSteamFurnaceEU())
					))
					.builtinModel(BRONZE_PLATED_BRICKS, "large_furnace", (model) -> model.front(true).active(true))
					.registerMachine()
					.registerMultiblockShape(shape)
					.registerAsWorkstationFor(MI.id("bronze_furnace"))
					.registerAsWorkstationFor(MI.id("steel_furnace"));
		}
		
		hook.builder("large_electric_furnace", "Large Electric Furnace", LargeElectricFurnaceBlockEntity::new)
				.builtinModel(HEATPROOF, "large_furnace", (model) -> model.front(true).active(true))
				.registerMachine()
				.registerAsWorkstationFor(MI.id("bronze_furnace"))
				.registerAsWorkstationFor(MI.id("steel_furnace"))
				.registerAsWorkstationFor(MI.id("electric_furnace"));
		
		{
			SimpleMember bronzePlatedBricks = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("bronze_plated_bricks")));
			HatchFlags hatches = new HatchFlags.Builder().with(HatchTypes.ITEM_INPUT, HatchTypes.ITEM_OUTPUT, HatchTypes.FLUID_INPUT).build();
			ShapeTemplate shape = new ShapeTemplate.Builder(BRONZE_PLATED_BRICKS).add3by3LevelsRoofed(-1, 1, bronzePlatedBricks, hatches).build();
			hook.builder("large_steam_macerator", "Large Steam Macerator", (bep) -> new SteamMultipliedCraftingMultiblockBlockEntity(
							bep, EI.id("large_steam_macerator"), new ShapeTemplate[]{shape},
							OverclockComponent.getDefaultCatalysts(),
							MIMachineRecipeTypes.MACERATOR,
							EI.config().batchingMachines().largeSteamMaceratorSize(),
							EuCostTransformers.percentage(() -> (float) EI.config().batchingMachines().largeSteamMaceratorEU())
					))
					.builtinModel(BRONZE_PLATED_BRICKS, "large_macerator", (model) -> model.front(true).active(true))
					.registerMachine()
					.registerMultiblockShape(shape)
					.registerAsWorkstationFor(MI.id("bronze_macerator"))
					.registerAsWorkstationFor(MI.id("steel_macerator"));
		}
		
		{
			SimpleMember steelPlatedBricks = SimpleMember.forBlock(EIBlocks.STEEL_PLATED_BRICKS);
			HatchFlags hatches = new HatchFlags.Builder().with(HatchTypes.ITEM_INPUT, HatchTypes.ITEM_OUTPUT, HatchTypes.ENERGY_INPUT).build();
			ShapeTemplate shape = new ShapeTemplate.Builder(Casings.STEEL_PLATED_BRICKS).add3by3LevelsRoofed(-1, 1, steelPlatedBricks, hatches).build();
			hook.builder("large_electric_macerator", "Large Electric Macerator", (bep) -> new ElectricMultipliedCraftingMultiblockBlockEntity(
							bep, EI.id("large_electric_macerator"), new ShapeTemplate[]{shape},
							MachineTier.LV,
							MIMachineRecipeTypes.MACERATOR,
							EI.config().batchingMachines().largeElectricMaceratorSize(),
							EuCostTransformers.percentage(() -> (float) EI.config().batchingMachines().largeElectricMaceratorEU())
					))
					.builtinModel(Casings.STEEL_PLATED_BRICKS, "large_macerator", (model) -> model.front(true).active(true))
					.registerMachine()
					.registerMultiblockShape(shape)
					.registerAsWorkstationFor(MI.id("bronze_macerator"))
					.registerAsWorkstationFor(MI.id("steel_macerator"))
					.registerAsWorkstationFor(MI.id("electric_macerator"));
		}
		
		hook.builder("tesla_tower", "Tesla Tower", TeslaTowerBlockEntity::new)
				.builtinModel(CLEAN_STAINLESS_STEEL, "tesla_tower", (model) -> model.front(true).active(true))
				.registerMachine();
	}
	
	public static void singleBlockCrafting(SingleBlockCraftingMachinesMIHookContext hook)
	{
		hook.builder("bending_machine", "Bending Machine", RecipeTypes.BENDING_MACHINE)
				.bronze().steel().electric()
				.gui(SteamMode.BOTH, (gui) -> gui
						.slots((s) -> s
								.itemInput(56, 35)
								.itemOutput(102, 35))
						.progressBar(77, 34, "compress")
						.efficiencyBar(38, 62)
						.energyBar(14, 34))
				.builtinModel("bending_machine", (model) -> model.front(true))
				.registerMachine();
		
		hook.builder("alloy_smelter", "Alloy Smelter", RecipeTypes.ALLOY_SMELTER)
				.steel().electric()
				.gui(SteamMode.BOTH, (gui) -> gui
						.slots((s) -> s
								.itemInputs(40, 35, 2, 1)
								.itemOutput(120, 35))
						.progressBar(88, 33, "arrow")
						.efficiencyBar(38, 62)
						.energyBar(14, 34))
				.builtinModel("alloy_smelter", (model) -> model.front(true))
				.registerMachine();
		
		hook.builder("canning_machine", "Canning Machine", RecipeTypes.CANNING_MACHINE)
				.steel().electric()
				.gui(SteamMode.BOTH, (gui) -> gui
						.slots((s) -> s
								.itemInputs(58, 27, 1, 2)
								.itemOutputs(102, 27, 1, 2)
								.fluidInput(38, 27, 16)
								.fluidOutput(122, 27, 16))
						.progressBar(79, 34, "canning")
						.efficiencyBar(38, 66)
						.energyBar(14, 35))
				.builtinModel("canning_machine", (model) -> model.front(true).side(true))
				.registerMachine();
		
		hook.builder("composter", "Composter", RecipeTypes.COMPOSTER)
				.bronze().steel().electric()
				.gui(SteamMode.BOTH, (gui) -> gui
						.slots((s) -> s
								.itemInputs(58, 27, 1, 2)
								.itemOutputs(102, 27, 1, 2)
								.fluidInput(38, 27, 16)
								.fluidOutput(122, 27, 16))
						.progressBar(78, 34, "centrifuge")
						.efficiencyBar(38, 66)
						.energyBar(14, 35))
				.builtinModel("composter", (model) -> model.front(true).top(true))
				.registerMachine();
	}
	
	public static void singleBlockSpecial(SingleBlockSpecialMachinesMIHookContext hook)
	{
		hook.builder("bronze_solar_boiler", "Bronze Solar Boiler", (bep) -> new SolarBoilerMachineBlockEntity(bep, true))
				.builtinModel(MachineCasings.BRICKED_BRONZE, "solar_boiler", (model) -> model.front(true).top(true).active(true))
				.registrator(MachineBlockEntity::registerFluidApi)
				.registerMachine();
		hook.builder("steel_solar_boiler", "Steel Solar Boiler", (bep) -> new SolarBoilerMachineBlockEntity(bep, false))
				.builtinModel(MachineCasings.BRICKED_STEEL, "solar_boiler", (model) -> model.front(true).top(true).active(true))
				.registrator(MachineBlockEntity::registerFluidApi)
				.registerMachine();
		
		hook.builder("steel_honey_extractor", "Steel Honey Extractor", (bep) -> new SteamFluidHarvestingMachineBlockEntity(
						bep, EI.id("steel_honey_extractor"),
						2, HoneyExtractorBehavior.STEEL,
						16 * FluidType.BUCKET_VOLUME, EIFluids.HONEY
				))
				.builtinModel(MachineCasings.STEEL, "honey_extractor", (model) -> model.front(true).side(true).active(true))
				.registrator(MachineBlockEntity::registerFluidApi)
				.registerMachine();
		hook.builder("electric_honey_extractor", "Electric Honey Extractor", (bep) -> new ElectricFluidHarvestingMachineBlockEntity(
						bep, EI.id("electric_honey_extractor"),
						4, HoneyExtractorBehavior.ELECTRIC,
						32 * FluidType.BUCKET_VOLUME, EIFluids.HONEY
				))
				.builtinModel(CableTier.LV.casing, "honey_extractor", (model) -> model.front(true).side(true).active(true))
				.registrator(MachineBlockEntity::registerFluidApi)
				.registrator(ElectricFluidHarvestingMachineBlockEntity::registerEnergyApi)
				.registerMachine();
		
		hook.builder("steel_brewery", "Steel Brewery", (bep, gui) -> new SteamBreweryMachineBlockEntity(bep, gui, false))
				.builtinModel(MachineCasings.STEEL, "brewery", (model) -> model.front(true).side(true).active(true))
				.gui((gui) -> gui
						.guiHeight(186)
						.inventoryOnlySlots((s) -> s
								.fluidInput(5, 45, MIFluids.STEAM::asFluid, 16)
								.fluidInput(24, 45, EIFluids.BLAZING_ESSENCE::asFluid, 16))
						.slots((s) -> s
								.itemInputs(43, 27, 3, 3)
								.itemOutputs(119, 27, 3, 3))
						.progressBar(97, 43, "triple_arrow"))
				.registrator(MachineBlockEntity::registerItemApi)
				.registrator(MachineBlockEntity::registerFluidApi)
				.registerMachine();
		hook.builder("electric_brewery", "Electric Brewery", ElectricBreweryMachineBlockEntity::new)
				.builtinModel(CableTier.LV.casing, "brewery", (model) -> model.front(true).side(true).active(true))
				.gui((gui) -> gui
						.guiHeight(186)
						.inventoryOnlySlots((s) -> s
								.fluidInput(24, 45, EIFluids.BLAZING_ESSENCE::asFluid, 32))
						.slots((s) -> s
								.itemInputs(43, 27, 3, 3)
								.itemOutputs(119, 27, 3, 3))
						.progressBar(97, 43, "triple_arrow")
						.energyBar(7, 44)
						.efficiencyBar(57, 86))
				.registrator(MachineBlockEntity::registerItemApi)
				.registrator(MachineBlockEntity::registerFluidApi)
				.registrator(ElectricBreweryMachineBlockEntity::registerEnergyApi)
				.registerMachine();
		
		
		hook.builder("bronze_waste_collector", "Bronze Waste Collector", (bep) -> new SteamFluidHarvestingMachineBlockEntity(
						bep, EI.id("bronze_waste_collector"),
						1, WasteCollectorBehavior.BRONZE,
						8 * FluidType.BUCKET_VOLUME, EIFluids.MANURE
				))
				.builtinModel(MachineCasings.BRONZE, "waste_collector", (model) -> model.top(true).front(false).active(true))
				.registrator(MachineBlockEntity::registerFluidApi)
				.registerMachine();
		hook.builder("steel_waste_collector", "Steel Waste Collector", (bep) -> new SteamFluidHarvestingMachineBlockEntity(
						bep, EI.id("steel_waste_collector"),
						2, WasteCollectorBehavior.STEEL,
						16 * FluidType.BUCKET_VOLUME, EIFluids.MANURE
				))
				.builtinModel(MachineCasings.STEEL, "waste_collector", (model) -> model.top(true).front(false).active(true))
				.registrator(MachineBlockEntity::registerFluidApi)
				.registerMachine();
		hook.builder("electric_waste_collector", "Electric Waste Collector", (bep) -> new ElectricFluidHarvestingMachineBlockEntity(
						bep, EI.id("electric_waste_collector"),
						4, WasteCollectorBehavior.ELECTRIC,
						32 * FluidType.BUCKET_VOLUME, EIFluids.MANURE
				))
				.builtinModel(CableTier.LV.casing, "waste_collector", (model) -> model.top(true).front(false).active(true))
				.registrator(MachineBlockEntity::registerFluidApi)
				.registrator(ElectricFluidHarvestingMachineBlockEntity::registerEnergyApi)
				.registerMachine();
		
		hook.builder("machine_chainer", "Machine Chainer", MachineChainerMachineBlockEntity::new)
				.registrator(MachineChainerMachineBlockEntity::registerCapabilities)
				.registerMachine();
		
		hook.builder("universal_transformer", "Universal Transformer", UniversalTransformerMachineBlockEntity::new)
				.builtinModel(CableTier.LV.casing, "universal_transformer", (model) -> model.top(true).side(true).front(false).active(false))
				.registrator(UniversalTransformerMachineBlockEntity::registerEnergyApi)
				.registerMachine();
		
		for(CableTier tier : new CableTier[]{CableTier.LV, CableTier.MV, CableTier.HV})
		{
			String name = "%s_solar_panel".formatted(tier.name);
			String englishName = "%s Solar Panel".formatted(tier.shortEnglishName);
			String overlayFolder = "solar_panel/%s".formatted(tier.name);
			hook.builder(name, englishName, (bep) -> new SolarPanelMachineBlockEntity(bep, EI.id(name), tier))
					.builtinModel(tier.casing, overlayFolder, (model) -> model.top(true).side(true).front(false).active(false))
					.registrator(MachineBlockEntity::registerItemApi)
					.registrator(MachineBlockEntity::registerFluidApi)
					.registrator(SolarPanelMachineBlockEntity::registerEnergyApi)
					.registerMachine();
		}
		
		hook.builder("large_configurable_chest", "Large Configurable Chest", LargeConfigurableChestMachineBlockEntity::new)
				.builtinModel(Casings.LARGE_STEEL_CRATE, "large_configurable_chest", (model) -> model.front(false).active(false))
				.registrator(MachineBlockEntity::registerItemApi)
				.registerMachine();
		
		hook.builder("tesla_coil", "Tesla Coil", TeslaCoilMachineBlockEntity::new)
				.builtinModel(CableTier.LV.casing, "tesla_coil", (model) -> model.front(true).top(true).side(true).active(true))
				.registrator(TeslaCoilMachineBlockEntity::registerEnergyApi)
				.registerMachine();
		
		hook.builder("tesla_receiver", "Tesla Receiver", TeslaReceiverMachineBlockEntity::new)
				.builtinModel(CableTier.LV.casing, "tesla_receiver", (model) -> model.front(true).top(true).side(true).active(true))
				.registrator(TeslaReceiverMachineBlockEntity::registerEnergyApi)
				.registerMachine();
		
		for(CableTier tier : CableTier.allTiers())
		{
			var name = "%s_tesla_receiver_hatch".formatted(tier.name);
			hook.builder(name, "%s Tesla Receiver Hatch".formatted(tier.shortEnglishName), (bep) -> new TeslaReceiverHatchBlockEntity(bep, EI.id(name), tier))
					.builtinModel(tier.casing, "tesla_receiver_hatch", (model) -> model.front(true).side(true).active(false))
					.registerMachine();
		}
		
		hook.builder("lethal_tesla_coil", "Lethal Tesla Coil", LethalTeslaCoilMachineBlockEntity::new)
				.modify((block) -> block.tag(BlockTags.WITHER_IMMUNE, BlockTags.DRAGON_IMMUNE))
				.properties((properties) -> properties
						.mapColor(MapColor.METAL)
						.destroyTime(4)
						.requiresCorrectToolForDrops()
						.isValidSpawn(MobSpawning.NO_SPAWN)
						.explosionResistance(3600000))
				.excludeDefaultBlockProperties()
				.builtinModel(CableTier.LV.casing, "lethal_tesla_coil", (model) -> model.front(true).top(true).side(true).active(true))
				.registrator(LethalTeslaCoilMachineBlockEntity::registerEnergyApi)
				.registerMachine();
		
		hook.builder("tesla_particle_generator", "Tesla Particle Generator", TeslaParticleGeneratorMachineBlockEntity::new)
				.modify((block) -> block.item().withoutModel())
				.properties((properties) -> properties
						.mapColor(MapColor.METAL)
						.isValidSpawn(MobSpawning.NO_SPAWN)
						.noCollission()
						.noOcclusion()
						.instabreak())
				.excludeDefaultBlockProperties()
				.excludeDefaultMineableTags()
				.registerMachine();
	}
}
