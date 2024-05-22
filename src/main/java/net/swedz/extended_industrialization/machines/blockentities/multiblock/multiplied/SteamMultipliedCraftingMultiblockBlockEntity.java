package net.swedz.extended_industrialization.machines.blockentities.multiblock.multiplied;

import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OverclockComponent;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.swedz.extended_industrialization.machines.components.craft.multiplied.EuCostTransformer;

import java.util.List;

public class SteamMultipliedCraftingMultiblockBlockEntity extends AbstractSteamMultipliedCraftingMultiblockBlockEntity
{
	private final MachineRecipeType recipeType;
	private final int               maxMultiplier;
	private final EuCostTransformer euCostTransformer;
	
	public SteamMultipliedCraftingMultiblockBlockEntity(BEP bep, String name, ShapeTemplate[] shapeTemplates, List<OverclockComponent.Catalyst> overclockCatalysts, MachineRecipeType recipeType, int maxMultiplier, EuCostTransformer euCostTransformer)
	{
		super(bep, name, shapeTemplates, overclockCatalysts);
		
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
