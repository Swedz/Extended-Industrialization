package net.swedz.extended_industrialization.machines.blockentities.multiblock.multiplied;

import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.swedz.extended_industrialization.machines.components.craft.multiplied.EuCostTransformer;

public class ElectricMultipliedCraftingMultiblockBlockEntity extends AbstractElectricMultipliedCraftingMultiblockBlockEntity
{
	private final MachineRecipeType recipeType;
	private final int               maxMultiplier;
	private final EuCostTransformer euCostTransformer;
	
	public ElectricMultipliedCraftingMultiblockBlockEntity(BEP bep, String name, ShapeTemplate[] shapeTemplates,
														   MachineTier machineTier,
														   MachineRecipeType recipeType, int maxMultiplier, EuCostTransformer euCostTransformer)
	{
		super(bep, name, shapeTemplates, machineTier);
		
		this.recipeType = recipeType;
		this.maxMultiplier = maxMultiplier;
		this.euCostTransformer = euCostTransformer;
	}
	
	@Override
	public MachineRecipeType getRecipeType()
	{
		return recipeType;
	}
	
	@Override
	public int getMaxMultiplier()
	{
		return maxMultiplier;
	}
	
	@Override
	public EuCostTransformer getEuCostTransformer()
	{
		return euCostTransformer;
	}
}
