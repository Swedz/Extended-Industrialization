package net.swedz.extended_industrialization.material;

import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EISortOrder;
import net.swedz.extended_industrialization.EITags;
import net.swedz.tesseract.neoforge.compat.mi.material.MIMaterials;
import net.swedz.tesseract.neoforge.compat.mi.material.part.MIMaterialParts;
import net.swedz.tesseract.neoforge.compat.mi.material.recipe.MIMachineMaterialRecipeContext;
import net.swedz.tesseract.neoforge.material.Material;
import net.swedz.tesseract.neoforge.material.builtin.recipe.VanillaMaterialRecipeContext;
import net.swedz.tesseract.neoforge.material.part.MaterialPart;
import net.swedz.tesseract.neoforge.material.recipe.MaterialRecipeGroup;
import net.swedz.tesseract.neoforge.registry.common.CommonLootTableBuilders;
import net.swedz.tesseract.neoforge.registry.common.CommonModelBuilders;

import static aztech.modern_industrialization.machines.init.MIMachineRecipeTypes.*;
import static net.swedz.extended_industrialization.EIMachines.RecipeTypes.*;
import static net.swedz.tesseract.neoforge.compat.mi.material.part.MIMaterialParts.*;
import static net.swedz.tesseract.neoforge.compat.mi.material.property.MIMaterialProperties.*;

public interface EIMaterials
{
	interface Parts
	{
		MaterialPart POLISHED_MACHINE_CASING = create("polished_machine_casing", "Polished Machine Casing")
				.formattingMaterialOnly("polished_%s_machine_casing"::formatted, "Polished %s Machine Casing"::formatted)
				.blockModel(CommonModelBuilders::blockCubeAll)
				.blockLoot(CommonLootTableBuilders::self)
				.itemOn(EI.ID, (c, h) -> h.sorted(EISortOrder.CASINGS));
		
		MaterialPart CURVED_PLATE = MIMaterialParts.CURVED_PLATE
				.itemOn(EI.ID, (c, h) -> h.sorted(EISortOrder.PARTS));
		
		MaterialPart TESLA_TOP_LOAD = create("tesla_top_load", "Tesla Top Load")
				.itemModelBuilder(CommonModelBuilders::generated)
				.itemOn(EI.ID, (c, h) -> h.sorted(EISortOrder.PARTS));
		
		MaterialPart TESLA_WINDING = create("tesla_winding", "Tesla Winding")
				.blockModel(CommonModelBuilders::blockTopEnd)
				.blockLoot(CommonLootTableBuilders::self)
				.itemOn(EI.ID, (c, h) -> h.sorted(EISortOrder.TESLA_WINDINGS));
		
		static MaterialPart create(String id, String englishName)
		{
			return new MaterialPart(EI.id(id), englishName);
		}
	}
	
	interface Recipes
	{
		MaterialRecipeGroup<VanillaMaterialRecipeContext> STANDARD = MaterialRecipeGroup.create(VanillaMaterialRecipeContext::new)
				.add("polished_machine_casing", (c) -> c.shaped(Parts.POLISHED_MACHINE_CASING, 1, (r) -> r.add('c', CURVED_PLATE).add('p', PLATE), "cpc", "cpc", "cpc"));
		
		MaterialRecipeGroup<MIMachineMaterialRecipeContext> STANDARD_MACHINES = MaterialRecipeGroup.create(MIMachineMaterialRecipeContext::new)
				.add("plate_to_curved_plate_bending", (c) -> c.machine("curved_plate", BENDING_MACHINE, 2, (int) (200 * c.get(TIME_FACTOR)) / 2, PLATE, 1, CURVED_PLATE, 1))
				.add("rod_to_ring_bending", (c) -> c.machine("ring", BENDING_MACHINE, 2, (int) (200 * c.get(TIME_FACTOR)) / 2, ROD, 1, RING, 1))
				.add("polished_machine_casing", (c) -> c.machine(ASSEMBLER, 8, 10 * 20, Parts.POLISHED_MACHINE_CASING, 1, (b) -> b.addPartInput(PLATE, 3).addPartInput(CURVED_PLATE, 6)))
				.add("tesla_top_load", (c) -> c.machine("tesla_top_load", PACKER, 8, 5 * 20, CURVED_PLATE, 8, Parts.TESLA_TOP_LOAD, 1))
				.add("tesla_winding", (c) -> c.machine(ASSEMBLER, 8, 5 * 20, Parts.TESLA_WINDING, 1, (b) -> b.addPartInput(CABLE, 8).addItemInput(EITags.itemCommon("plates/stainless_steel"), 4)));
	}
	
	Material SILVER = MIMaterials.SILVER.as(EIMaterialRegistry.get())
			.add(Parts.CURVED_PLATE, Parts.TESLA_TOP_LOAD)
			.add(Parts.POLISHED_MACHINE_CASING)
			.recipes(Recipes.STANDARD, Recipes.STANDARD_MACHINES);
	
	Material COPPER = MIMaterials.COPPER.as(EIMaterialRegistry.get())
			.add(Parts.TESLA_WINDING)
			.recipes(Recipes.STANDARD, Recipes.STANDARD_MACHINES);
	
	Material ELECTRUM = MIMaterials.ELECTRUM.as(EIMaterialRegistry.get())
			.add(Parts.TESLA_WINDING)
			.recipes(Recipes.STANDARD, Recipes.STANDARD_MACHINES);
	
	Material ALUMINUM = MIMaterials.ALUMINUM.as(EIMaterialRegistry.get())
			.add(Parts.TESLA_WINDING)
			.recipes(Recipes.STANDARD, Recipes.STANDARD_MACHINES);
	
	Material ANNEALED_COPPER = MIMaterials.ANNEALED_COPPER.as(EIMaterialRegistry.get())
			.add(Parts.TESLA_WINDING)
			.recipes(Recipes.STANDARD, Recipes.STANDARD_MACHINES);
	
	Material SUPERCONDUCTOR = MIMaterials.SUPERCONDUCTOR.as(EIMaterialRegistry.get())
			.add(Parts.TESLA_WINDING)
			.recipes(Recipes.STANDARD, Recipes.STANDARD_MACHINES);
	
	static Material[] values()
	{
		return new Material[]{SILVER, COPPER, ELECTRUM, ALUMINUM, ANNEALED_COPPER, SUPERCONDUCTOR};
	}
	
	static void init()
	{
	}
}
