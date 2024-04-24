package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.materials.Material;
import aztech.modern_industrialization.materials.MaterialRegistry;
import aztech.modern_industrialization.materials.part.PartTemplate;
import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.hook.mi.MIMachineHook;

import java.util.Map;

import static aztech.modern_industrialization.materials.part.MIParts.*;
import static aztech.modern_industrialization.materials.property.MaterialProperty.*;

public final class BendingMachineRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public BendingMachineRecipesServerDatagenProvider(GatherDataEvent event)
	{
		super(event);
	}
	
	private static void addBendingMachineRecipes(String name, Material material, PartTemplate from, PartTemplate to, RecipeOutput output)
	{
		if(hasPart(material, from) && hasPart(material, to))
		{
			addMaterialMachineRecipe(
					material, name, MIMachineHook.RecipeTypes.BENDING_MACHINE,
					2, (int) ((200 * material.get(HARDNESS).timeFactor) / 2),
					(r) -> r
							.addItemInput(material.getPart(from).getTaggedIngredient(), 1, 1)
							.addItemOutput(material.getPart(to), 1),
					output
			);
		}
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		for(Map.Entry<String, Material> entry : MaterialRegistry.getMaterials().entrySet())
		{
			Material material = entry.getValue();
			addBendingMachineRecipes("plate", material, PLATE, CURVED_PLATE, output);
			addBendingMachineRecipes("ring", material, ROD, RING, output);
		}
	}
}
