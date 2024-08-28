package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.MIItem;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import aztech.modern_industrialization.materials.MIMaterials;
import aztech.modern_industrialization.materials.part.MIParts;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIBlocks;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.datagen.api.recipe.ShapedRecipeBuilder;

import java.util.function.Consumer;

public final class CommonRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public CommonRecipesServerDatagenProvider(GatherDataEvent event)
	{
		super(event);
	}
	
	private static void addBasicCraftingRecipes(String path, String name, boolean assembler, ItemLike result, int resultCount, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output)
	{
		ShapedRecipeBuilder shapedRecipeBuilder = new ShapedRecipeBuilder();
		crafting.accept(shapedRecipeBuilder);
		shapedRecipeBuilder.setOutput(result, resultCount);
		shapedRecipeBuilder.offerTo(output, EI.id(path + "/craft/" + name));
		
		if(assembler)
		{
			shapedRecipeBuilder.exportToAssembler().offerTo(output, EI.id(path + "/assembler/" + name));
		}
	}
	
	private static void components(RecipeOutput output)
	{
		addBasicCraftingRecipes(
				"component", "netherite_rotary_blade", true,
				EIItems.NETHERITE_ROTARY_BLADE, 1,
				(r) -> r
						.define('N', EITags.itemCommon("dusts/netherite"))
						.define('R', EITags.itemCommon("gears/stainless_steel"))
						.pattern(" N ")
						.pattern("NRN")
						.pattern(" N "),
				output
		);
		
		addBasicCraftingRecipes(
				"component", "steel_combine", false,
				EIItems.STEEL_COMBINE, 1,
				(r) -> r
						.define('C', "modern_industrialization:steel_curved_plate")
						.define('B', "modern_industrialization:steel_bolt")
						.define('R', EITags.itemCommon("rods/steel"))
						.pattern("CCC")
						.pattern("BRB")
						.pattern("CCC"),
				output
		);
		addMachineRecipe(
				"component/assembler", "steel_combine", MIMachineRecipeTypes.ASSEMBLER,
				8, 10 * 20,
				(b) -> b
						.addItemInput("modern_industrialization:steel_curved_plate", 6)
						.addItemInput(EITags.itemCommon("rods/steel"), 1)
						.addFluidInput(MIFluids.SOLDERING_ALLOY, 50)
						.addItemOutput(EIItems.STEEL_COMBINE, 1),
				output
		);
		
		addBasicCraftingRecipes(
				"component", "tin_can", true,
				EIItems.TIN_CAN, 2,
				(r) -> r
						.define('T', "modern_industrialization:tin_curved_plate")
						.pattern("T")
						.pattern("T"),
				output
		);
	}
	
	private static void photovoltaicCells(RecipeOutput output)
	{
		addMachineRecipe(
				"photovoltaic_cell", "lv", MIMachineRecipeTypes.PACKER,
				4, 40 * 20,
				(r) -> r
						.addItemInput(EITags.itemCommon("glass_panes"), 4)
						.addItemInput(EITags.itemCommon("plates/silver"), 2)
						.addItemInput("modern_industrialization:rubber_sheet", 4)
						.addItemOutput(EIItems.LV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		addMachineRecipe(
				"photovoltaic_cell", "lv_synthetic_rubber", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.addItemInput(EITags.itemCommon("glass_panes"), 4)
						.addItemInput(EITags.itemCommon("plates/silver"), 2)
						.addFluidInput(MIFluids.SYNTHETIC_RUBBER, 20)
						.addItemOutput(EIItems.LV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		addMachineRecipe(
				"photovoltaic_cell", "lv_styrene_rubber", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.addItemInput(EITags.itemCommon("glass_panes"), 4)
						.addItemInput(EITags.itemCommon("plates/silver"), 2)
						.addFluidInput(MIFluids.STYRENE_BUTADIENE_RUBBER, 4)
						.addItemOutput(EIItems.LV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		
		addMachineRecipe(
				"photovoltaic_cell", "mv", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.addItemInput(EITags.itemCommon("glass_panes"), 4)
						.addItemInput("modern_industrialization:silicon_n_doped_plate", 1)
						.addItemInput(EITags.itemCommon("plates/silver"), 4)
						.addItemInput("modern_industrialization:silicon_p_doped_plate", 1)
						.addFluidInput(MIFluids.SYNTHETIC_RUBBER, 100)
						.addItemOutput(EIItems.MV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		addMachineRecipe(
				"photovoltaic_cell", "mv_styrene_rubber", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.addItemInput(EITags.itemCommon("glass_panes"), 4)
						.addItemInput("modern_industrialization:silicon_n_doped_plate", 1)
						.addItemInput(EITags.itemCommon("plates/silver"), 4)
						.addItemInput("modern_industrialization:silicon_p_doped_plate", 1)
						.addFluidInput(MIFluids.STYRENE_BUTADIENE_RUBBER, 20)
						.addItemOutput(EIItems.MV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		
		addMachineRecipe(
				"photovoltaic_cell", "hv", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.addItemInput(EITags.itemCommon("glass_panes"), 4)
						.addItemInput("modern_industrialization:silicon_wafer", 4)
						.addItemInput(EITags.itemCommon("plates/silver"), 8)
						.addFluidInput(MIFluids.SYNTHETIC_RUBBER, 200)
						.addFluidInput(MIFluids.POLYETHYLENE, 500)
						.addItemOutput(EIItems.HV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		addMachineRecipe(
				"photovoltaic_cell", "hv_styrene_rubber", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.addItemInput(EITags.itemCommon("glass_panes"), 4)
						.addItemInput("modern_industrialization:silicon_wafer", 4)
						.addItemInput(EITags.itemCommon("plates/silver"), 8)
						.addFluidInput(MIFluids.STYRENE_BUTADIENE_RUBBER, 40)
						.addFluidInput(MIFluids.POLYETHYLENE, 500)
						.addItemOutput(EIItems.HV_PHOTOVOLTAIC_CELL, 1),
				output
		);
	}
	
	private static void nanoSuitPiece(String id, ItemLike baseArmor, int pieces, ItemLike result, Consumer<MachineRecipeBuilder> recipeBuilder, RecipeOutput output)
	{
		addMachineRecipe(
				"tool", id, MIMachineRecipeTypes.ASSEMBLER,
				8, 10 * 20,
				(builder) ->
				{
					builder
							.addItemInput(baseArmor, 1)
							.addItemInput(EITags.itemCommon("plates/carbon"), 4 * pieces)
							.addItemInput(MIItem.ELECTRONIC_CIRCUIT, 4)
							.addItemInput("modern_industrialization:silicon_battery", 2)
							.addItemInput(MIItem.LARGE_MOTOR, 4)
							.addFluidInput(MIFluids.POLYETHYLENE, 4000)
							.addFluidInput(MIFluids.NYLON, 2000)
							.addItemOutput(result, 1);
					if(recipeBuilder != null)
					{
						recipeBuilder.accept(builder);
					}
				},
				output
		);
	}
	
	private static void nanoSuit(RecipeOutput output)
	{
		nanoSuitPiece(
				"nano_suit_helmet",
				Items.NETHERITE_HELMET, 5, EIItems.NANO_HELMET,
				(r) -> r
						.addItemInput(EITags.itemCommon("glass_panes"), 4)
						.addItemInput(MIItem.REDSTONE_CONTROL_MODULE, 1),
				output
		);
		nanoSuitPiece(
				"nano_suit_chestplate",
				Items.NETHERITE_CHESTPLATE, 8, EIItems.NANO_CHESTPLATE,
				null,
				output
		);
		nanoSuitPiece(
				"nano_suit_leggings",
				Items.NETHERITE_LEGGINGS, 7, EIItems.NANO_LEGGINGS,
				null,
				output
		);
		nanoSuitPiece(
				"nano_suit_boots",
				Items.NETHERITE_BOOTS, 4, EIItems.NANO_BOOTS,
				null,
				output
		);
		
		addMachineRecipe(
				"tool", "nano_suit_gravichestplate_upgrade", MIMachineRecipeTypes.PACKER,
				32, 10 * 20,
				(b) -> b
						.addItemInput(EIItems.NANO_CHESTPLATE, 1)
						.addItemInput(MIItem.GRAVICHESTPLATE, 1)
						.addItemOutput(EIItems.NANO_GRAVICHESTPLATE, 1),
				output
		);
		addMachineRecipe(
				"tool", "nano_suit_gravichestplate_downgrade", MIMachineRecipeTypes.UNPACKER,
				32, 10 * 20,
				(b) -> b
						.addItemInput(EIItems.NANO_GRAVICHESTPLATE, 1)
						.addItemOutput(EIItems.NANO_CHESTPLATE, 1)
						.addItemOutput(MIItem.GRAVICHESTPLATE, 1),
				output
		);
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		addMachineRecipe(
				"mixer", "mulch", MIMachineRecipeTypes.MIXER,
				2, 5 * 20,
				(r) -> r
						.addItemInput(Items.DIRT, 1)
						.addItemInput(MIItem.WOOD_PULP, 6)
						.addItemOutput(EIItems.MULCH, 1),
				output
		);
		
		addMachineRecipe(
				"macerator", "granite_dust", MIMachineRecipeTypes.MACERATOR,
				2, 5 * 20,
				(r) -> r
						.addItemInput(Items.GRANITE, 1)
						.addItemOutput(EIItems.GRANITE_DUST, 4),
				output
		);
		addBasicCraftingRecipes(
				"compacting", "granite_from_dust", false,
				Items.GRANITE, 1,
				(r) -> r
						.define('G', EIItems.GRANITE_DUST)
						.pattern("GG")
						.pattern("GG"),
				output
		);
		addMachineRecipe(
				"packer", "granite_from_dust", MIMachineRecipeTypes.PACKER,
				2, 5 * 20,
				(r) -> r
						.addItemInput(EITags.itemCommon("dusts/granite"), 4)
						.addItemOutput(Items.GRANITE, 1),
				output
		);
		
		addBasicCraftingRecipes(
				"casing", "steel_plated_bricks", true,
				EIBlocks.STEEL_PLATED_BRICKS.get().asItem(), 1,
				(r) -> r
						.define('S', MIMaterials.STEEL.getPart(MIParts.PLATE))
						.define('B', "modern_industrialization:fire_clay_bricks")
						.pattern("SSS")
						.pattern("SBS")
						.pattern("SSS"),
				output
		);
		
		components(output);
		
		addMachineRecipe(
				"distillery", "distilled_water_from_water", MIMachineRecipeTypes.DISTILLERY,
				8, 10 * 20,
				(r) -> r
						.addFluidInput(Fluids.WATER, 1000)
						.addFluidOutput(EIFluids.DISTILLED_WATER, 500),
				output
		);
		
		photovoltaicCells(output);
		
		addBasicCraftingRecipes(
				"tool", "steam_chainsaw", false,
				EIItems.STEAM_CHAINSAW, 1,
				(r) -> r
						.define('F', Items.FURNACE)
						.define('D', Items.DIAMOND)
						.define('P', "modern_industrialization:iron_large_plate")
						.define('C', EITags.itemCommon("gears/copper"))
						.define('B', Items.BUCKET)
						.pattern("FDD")
						.pattern("PCD")
						.pattern("BPF"),
				output
		);
		
		addBasicCraftingRecipes(
				"tool", "electric_chainsaw", false,
				EIItems.ELECTRIC_CHAINSAW, 1,
				(r) -> r
						.define('U', MIItem.ADVANCED_UPGRADE)
						.define('R', MIItem.RUBBER_SHEET)
						.define('B', EIItems.NETHERITE_ROTARY_BLADE)
						.define('M', MIItem.ADVANCED_MOTOR)
						.define('C', "modern_industrialization:aluminum_cable")
						.pattern("URB")
						.pattern("MBR")
						.pattern("CMU"),
				output
		);
		
		addBasicCraftingRecipes(
				"tool", "electric_mining_drill", false,
				EIItems.ELECTRIC_MINING_DRILL, 1,
				(r) -> r
						.define('U', MIItem.ADVANCED_UPGRADE)
						.define('R', EIItems.NETHERITE_ROTARY_BLADE)
						.define('D', "modern_industrialization:stainless_steel_drill_head")
						.define('M', MIItem.ADVANCED_MOTOR)
						.define('L', MIItem.LARGE_MOTOR)
						.define('C', "modern_industrialization:aluminum_cable")
						.pattern("URD")
						.pattern("MLR")
						.pattern("CMU"),
				output
		);
		
		addBasicCraftingRecipes(
				"tool", "machine_config_card", false,
				EIItems.MACHINE_CONFIG_CARD, 1,
				(r) -> r
						.define('G', EITags.itemCommon("glass_panes"))
						.define('I', "modern_industrialization:inductor")
						.define('C', "modern_industrialization:capacitor")
						.define('M', "modern_industrialization:motor")
						.define('A', "modern_industrialization:analog_circuit_board")
						.pattern("GGG")
						.pattern("ICI")
						.pattern("MAM"),
				output
		);
		
		addBasicCraftingRecipes(
				"tool", "ultimate_drill", false,
				EIItems.ULTIMATE_LASER_DRILL, 1,
				(r) -> r
						.define('D', EIItems.ELECTRIC_MINING_DRILL)
						.define('B', EIItems.NETHERITE_ROTARY_BLADE)
						.define('C', EIItems.ELECTRIC_CHAINSAW)
						.define('c', "modern_industrialization:cooling_cell")
						.define('S', "modern_industrialization:superconductor_coil")
						.define('U', "modern_industrialization:highly_advanced_upgrade")
						.define('s', "modern_industrialization:superconductor_cable")
						.pattern("DBC")
						.pattern("cSc")
						.pattern("UsU"),
				output
		);
		
		nanoSuit(output);
	}
}
