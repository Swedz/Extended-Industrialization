package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.MIItem;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.materials.MIMaterials;
import aztech.modern_industrialization.materials.part.MIParts;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.datagen.api.recipe.ShapedRecipeBuilder;
import net.swedz.extended_industrialization.EIBlocks;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EITags;

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
	
	private static void photovoltaicCells(RecipeOutput output)
	{
		addMachineRecipe(
				"photovoltaic_cell", "lv", MIMachineRecipeTypes.PACKER,
				4, 40 * 20,
				(r) -> r
						.addItemInput(EITags.itemForge("glass_panes"), 4)
						.addItemInput(EITags.itemForge("plates/silver"), 2)
						.addItemInput("modern_industrialization:rubber_sheet", 4)
						.addItemOutput(EIItems.LV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		addMachineRecipe(
				"photovoltaic_cell", "lv_synthetic_rubber", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.addItemInput(EITags.itemForge("glass_panes"), 4)
						.addItemInput(EITags.itemForge("plates/silver"), 2)
						.addFluidInput(MIFluids.SYNTHETIC_RUBBER, 20)
						.addItemOutput(EIItems.LV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		addMachineRecipe(
				"photovoltaic_cell", "lv_styrene_rubber", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.addItemInput(EITags.itemForge("glass_panes"), 4)
						.addItemInput(EITags.itemForge("plates/silver"), 2)
						.addFluidInput(MIFluids.STYRENE_BUTADIENE_RUBBER, 4)
						.addItemOutput(EIItems.LV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		
		addMachineRecipe(
				"photovoltaic_cell", "mv", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.addItemInput(EITags.itemForge("glass_panes"), 4)
						.addItemInput("modern_industrialization:silicon_n_doped_plate", 1)
						.addItemInput(EITags.itemForge("plates/silver"), 4)
						.addItemInput("modern_industrialization:silicon_p_doped_plate", 1)
						.addFluidInput(MIFluids.SYNTHETIC_RUBBER, 100)
						.addItemOutput(EIItems.MV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		addMachineRecipe(
				"photovoltaic_cell", "mv_styrene_rubber", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.addItemInput(EITags.itemForge("glass_panes"), 4)
						.addItemInput("modern_industrialization:silicon_n_doped_plate", 1)
						.addItemInput(EITags.itemForge("plates/silver"), 4)
						.addItemInput("modern_industrialization:silicon_p_doped_plate", 1)
						.addFluidInput(MIFluids.STYRENE_BUTADIENE_RUBBER, 20)
						.addItemOutput(EIItems.MV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		
		addMachineRecipe(
				"photovoltaic_cell", "hv", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.addItemInput(EITags.itemForge("glass_panes"), 4)
						.addItemInput("modern_industrialization:silicon_wafer", 4)
						.addItemInput(EITags.itemForge("plates/silver"), 8)
						.addFluidInput(MIFluids.SYNTHETIC_RUBBER, 200)
						.addFluidInput(MIFluids.POLYETHYLENE, 500)
						.addItemOutput(EIItems.HV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		addMachineRecipe(
				"photovoltaic_cell", "hv_styrene_rubber", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.addItemInput(EITags.itemForge("glass_panes"), 4)
						.addItemInput("modern_industrialization:silicon_wafer", 4)
						.addItemInput(EITags.itemForge("plates/silver"), 8)
						.addFluidInput(MIFluids.STYRENE_BUTADIENE_RUBBER, 40)
						.addFluidInput(MIFluids.POLYETHYLENE, 500)
						.addItemOutput(EIItems.HV_PHOTOVOLTAIC_CELL, 1),
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
						.addItemInput(EIItems.GRANITE_DUST, 4)
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
		
		photovoltaicCells(output);
		
		addBasicCraftingRecipes(
				"tool", "machine_config_card", false,
				EIItems.MACHINE_CONFIG_CARD, 1,
				(r) -> r
						.define('G', EITags.itemForge("glass_panes"))
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
	}
}
