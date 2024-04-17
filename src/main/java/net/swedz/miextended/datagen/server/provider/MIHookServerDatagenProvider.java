package net.swedz.miextended.datagen.server.provider;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.materials.Material;
import aztech.modern_industrialization.materials.MaterialRegistry;
import aztech.modern_industrialization.materials.part.PartTemplate;
import net.minecraft.data.DataGenerator;
import net.swedz.miextended.datagen.api.DatagenProvider;
import net.swedz.miextended.datagen.api.object.DatagenRecipeWrapper;
import net.swedz.miextended.mi.machines.MIMachineHook;

import java.util.Map;
import java.util.function.Consumer;

import static aztech.modern_industrialization.materials.part.MIParts.*;
import static aztech.modern_industrialization.materials.property.MaterialProperty.*;

public final class MIHookServerDatagenProvider extends DatagenProvider
{
	public MIHookServerDatagenProvider(DataGenerator generator)
	{
		super(generator, "MI Extended Datagen/Server/MI", MI.ID);
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
	
	private void addMaterialRecipe(Material material, String name, MachineRecipeType recipeType, int eu, int duration, Consumer<MachineRecipeBuilder> recipeBuilder)
	{
		DatagenRecipeWrapper wrapper = new DatagenRecipeWrapper(this, "materials/%s/%s".formatted(material.name, recipeType.getPath()), name);
		MachineRecipeBuilder recipe = new MachineRecipeBuilder(recipeType, eu, duration);
		recipeBuilder.accept(recipe);
		wrapper.modernIndustrializationMachineRecipe(recipe);
		wrapper.write();
	}
	
	private void addMaterialRecipe(Material material, String name, MachineRecipeType recipeType, int eu, Consumer<MachineRecipeBuilder> recipeBuilder)
	{
		this.addMaterialRecipe(material, name, recipeType, eu, (int) (200 * material.get(HARDNESS).timeFactor), recipeBuilder);
	}
	
	@Override
	public void run()
	{
		for(Map.Entry<String, Material> entry : MaterialRegistry.getMaterials().entrySet())
		{
			Material material = entry.getValue();
			if(this.hasPart(material, CURVED_PLATE) && this.hasPart(material, PLATE))
			{
				this.removeRecipe("materials/%s/compressor".formatted(material.name), "plate");
				
				this.addMaterialRecipe(material, "plate", MIMachineHook.BENDING_MACHINE, 2, (r) -> r
						.addItemInput(material.getPart(PLATE), 1)
						.addItemOutput(material.getPart(CURVED_PLATE), 1));
			}
			if(this.hasPart(material, RING) && this.hasPart(material, ROD))
			{
				this.removeRecipe("materials/%s/compressor".formatted(material.name), "ring");
				
				this.addMaterialRecipe(material, "ring", MIMachineHook.BENDING_MACHINE, 2, (r) -> r
						.addItemInput(material.getPart(ROD), 1)
						.addItemOutput(material.getPart(RING), 1));
			}
		}
	}
}
