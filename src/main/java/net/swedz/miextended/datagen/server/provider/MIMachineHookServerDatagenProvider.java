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

public final class MIMachineHookServerDatagenProvider extends DatagenProvider
{
	public MIMachineHookServerDatagenProvider(DataGenerator generator)
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
	
	private void addAlloySmelterRecipes(Material componentA, int amountA, Material componentB, int amountB, Material result, int amountResult)
	{
		this.addMaterialRecipe(result, "dust", MIMachineHook.ALLOY_SMELTER, 4, 10 * 20, (r) -> r
				.addItemInput(componentA.getPart(DUST).getTaggedIngredient(), amountA, 1)
				.addItemInput(componentB.getPart(DUST).getTaggedIngredient(), amountB, 1)
				.addItemOutput(result.getPart(INGOT), amountResult));
		this.addMaterialRecipe(result, "tiny_dust", MIMachineHook.ALLOY_SMELTER, 4, 10 * 20, (r) -> r
				.addItemInput(componentA.getPart(TINY_DUST).getTaggedIngredient(), amountA * 9, 1)
				.addItemInput(componentB.getPart(TINY_DUST).getTaggedIngredient(), amountB * 9, 1)
				.addItemOutput(result.getPart(INGOT), amountResult));
		this.addMaterialRecipe(result, "ingot", MIMachineHook.ALLOY_SMELTER, 4, 10 * 20, (r) -> r
				.addItemInput(componentA.getPart(INGOT).getTaggedIngredient(), amountA, 1)
				.addItemInput(componentB.getPart(INGOT).getTaggedIngredient(), amountB, 1)
				.addItemOutput(result.getPart(INGOT), amountResult));
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
						.addItemInput(material.getPart(PLATE).getTaggedIngredient(), 1, 1)
						.addItemOutput(material.getPart(CURVED_PLATE), 1));
			}
			if(this.hasPart(material, RING) && this.hasPart(material, ROD))
			{
				this.removeRecipe("materials/%s/compressor".formatted(material.name), "ring");
				
				this.addMaterialRecipe(material, "ring", MIMachineHook.BENDING_MACHINE, 2, (r) -> r
						.addItemInput(material.getPart(ROD).getTaggedIngredient(), 1, 1)
						.addItemOutput(material.getPart(RING), 1));
			}
		}
		
		this.addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("tin"), 1,
				MaterialRegistry.getMaterial("copper"), 3,
				MaterialRegistry.getMaterial("bronze"), 4
		);
		this.addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("lead"), 1,
				MaterialRegistry.getMaterial("antimony"), 1,
				MaterialRegistry.getMaterial("battery_alloy"), 2
		);
		this.addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("copper"), 1,
				MaterialRegistry.getMaterial("nickel"), 1,
				MaterialRegistry.getMaterial("cupronickel"), 2
		);
		this.addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("iron"), 2,
				MaterialRegistry.getMaterial("nickel"), 1,
				MaterialRegistry.getMaterial("invar"), 3
		);
		this.addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("gold"), 1,
				MaterialRegistry.getMaterial("silver"), 1,
				MaterialRegistry.getMaterial("electrum"), 2
		);
	}
}
