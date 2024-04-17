package net.swedz.intothetwilight.datagen.server.provider;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import aztech.modern_industrialization.materials.Material;
import aztech.modern_industrialization.materials.MaterialRegistry;
import aztech.modern_industrialization.materials.part.PartTemplate;
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
	
	private boolean hasPart(Material material, PartTemplate part)
	{
		return material.getParts().containsKey(part.key());
	}
	
	private void removeRecipe(String path, String name)
	{
		DatagenRecipeWrapper remove = new DatagenRecipeWrapper(this, path, name);
		remove.remove();
		remove.write();
	}
	
	@Override
	public void run()
	{
		for(Map.Entry<String, Material> entry : MaterialRegistry.getMaterials().entrySet())
		{
			Material material = entry.getValue();
			if(this.hasPart(material, CURVED_PLATE))
			{
				if(this.hasPart(material, DOUBLE_INGOT))
				{
					this.removeRecipe("materials/%s/forge_hammer".formatted(material.name), "double_ingot_to_curved_plate");
					this.removeRecipe("materials/%s/forge_hammer".formatted(material.name), "double_ingot_to_curved_plate_with_tool");
				}
				if(this.hasPart(material, INGOT))
				{
					this.removeRecipe("materials/%s/forge_hammer".formatted(material.name), "ingot_to_curved_plate");
					this.removeRecipe("materials/%s/forge_hammer".formatted(material.name), "ingot_to_curved_plate_with_tool");
				}
				if(this.hasPart(material, PLATE))
				{
					this.removeRecipe("materials/%s/forge_hammer".formatted(material.name), "plate_to_curved_plate_with_tool");
					this.removeRecipe("materials/%s/compressor".formatted(material.name), "plate");
					
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
}
