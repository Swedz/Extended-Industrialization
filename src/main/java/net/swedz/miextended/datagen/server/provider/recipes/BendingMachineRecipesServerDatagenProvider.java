package net.swedz.miextended.datagen.server.provider.recipes;

import aztech.modern_industrialization.materials.Material;
import aztech.modern_industrialization.materials.MaterialRegistry;
import aztech.modern_industrialization.materials.part.PartTemplate;
import net.minecraft.data.DataGenerator;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.mi.hook.MIMachineHook;

import java.util.Map;

import static aztech.modern_industrialization.materials.part.MIParts.*;

public final class BendingMachineRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public BendingMachineRecipesServerDatagenProvider(DataGenerator generator)
	{
		super(generator, "MI Extended Datagen/Server/Recipes/Bending Machine", MIExtended.ID);
	}
	
	private void addBendingMachineRecipes(String name, Material material, PartTemplate from, PartTemplate to)
	{
		if(this.hasPart(material, from) && this.hasPart(material, to))
		{
			this.addMaterialMachineRecipe(material, name, MIMachineHook.BENDING_MACHINE, 2, (int) (2.5 * 20), (r) -> r
					.addItemInput(material.getPart(from).getTaggedIngredient(), 1, 1)
					.addItemOutput(material.getPart(to), 1));
		}
	}
	
	@Override
	public void run()
	{
		for(Map.Entry<String, Material> entry : MaterialRegistry.getMaterials().entrySet())
		{
			Material material = entry.getValue();
			this.addBendingMachineRecipes("plate", material, PLATE, CURVED_PLATE);
			this.addBendingMachineRecipes("ring", material, ROD, RING);
		}
	}
}
