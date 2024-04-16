package net.swedz.intothetwilight.datagen.server.provider;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import aztech.modern_industrialization.materials.Material;
import aztech.modern_industrialization.materials.MaterialRegistry;
import net.minecraft.data.DataGenerator;
import net.swedz.intothetwilight.datagen.api.DatagenProvider;
import net.swedz.intothetwilight.datagen.api.object.DatagenRecipeWrapper;
import net.swedz.intothetwilight.mi.machines.MIMachineHook;

import java.util.Map;

import static aztech.modern_industrialization.materials.part.MIParts.*;
import static aztech.modern_industrialization.materials.property.MaterialProperty.*;

public final class ITTMIServerDatagenProvider extends DatagenProvider
{
	public ITTMIServerDatagenProvider(DataGenerator generator)
	{
		super(generator, "Into the Twilight Datagen/Server/MI", MI.ID);
	}
	
	@Override
	public void run()
	{
		for(Map.Entry<String, Material> entry : MaterialRegistry.getMaterials().entrySet())
		{
			Material material = entry.getValue();
			if(material.getParts().containsKey(PLATE.key()) && material.getParts().containsKey(CURVED_PLATE.key()))
			{
				DatagenRecipeWrapper wrapper = new DatagenRecipeWrapper(
						this,
						"materials/%s/%s".formatted(material.name, MIMachineHook.BENDING_MACHINE.getPath()),
						"plate"
				);
				wrapper.modernIndustrializationMachineRecipe(
						new MachineRecipeBuilder(MIMachineHook.BENDING_MACHINE, 2, (int) (200 * material.get(HARDNESS).timeFactor))
								.addItemInput(material.getPart(PLATE), 1)
								.addItemOutput(material.getPart(CURVED_PLATE), 1)
				);
				wrapper.write();
			}
		}
	}
}
