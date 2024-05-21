package net.swedz.extended_industrialization.datagen.server.provider.recipes;

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
import net.swedz.extended_industrialization.registry.blocks.EIBlocks;
import net.swedz.extended_industrialization.registry.items.EIItems;
import net.swedz.extended_industrialization.registry.tags.EITags;

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
				"packer/photovoltaic_cell", "lv", MIMachineRecipeTypes.PACKER,
				4, 40 * 20,
				(r) -> r
						.addItemInput(EITags.itemForge("glass_panes"), 1)
						.addItemInput(EITags.itemForge("plates/silver"), 1)
						.addItemInput("modern_industrialization:rubber_sheet", 1)
						.addItemOutput(EIItems.LV_PHOTOVOLTAIC_CELL, 1),
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
	}
}
