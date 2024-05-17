package net.swedz.extended_industrialization.machines.blockentities.multiblock.multiplied;

import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OverclockComponent;
import aztech.modern_industrialization.machines.helper.SteamHelper;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.swedz.extended_industrialization.machines.components.craft.multiplied.MultipliedCrafterComponent;

import java.util.List;
import java.util.function.Supplier;

public class SteamMultipliedCraftingMultiblockBlockEntity extends AbstractMultipliedCraftingMultiblockBlockEntity
{
	private final OverclockComponent overclock;
	
	private boolean steel;
	
	public SteamMultipliedCraftingMultiblockBlockEntity(BEP bep, String name, ShapeTemplate[] shapeTemplates,
														Supplier<MachineRecipeType> recipeTypeGetter, Supplier<Integer> maxMultiplierGetter, MultipliedCrafterComponent.EuCostTransformer euCostTransformer,
														List<OverclockComponent.Catalyst> overclockCatalysts)
	{
		super(bep, name, shapeTemplates, recipeTypeGetter, maxMultiplierGetter, euCostTransformer);
		
		overclock = new OverclockComponent(overclockCatalysts);
		this.registerComponents(overclock);
	}
	
	@Override
	public void onSuccessfulMatch(ShapeMatcher shapeMatcher)
	{
		steel = false;
		for(HatchBlockEntity hatch : shapeMatcher.getMatchedHatches())
		{
			if(hatch.upgradesToSteel())
			{
				steel = true;
				break;
			}
		}
	}
	
	@Override
	public List<Component> getTooltips()
	{
		return overclock.getTooltips();
	}
	
	@Override
	protected InteractionResult onUse(Player player, InteractionHand hand, Direction face)
	{
		InteractionResult result = super.onUse(player, hand, face);
		return !result.consumesAction() ? overclock.onUse(this, player, hand) : result;
	}
	
	@Override
	public long consumeEu(long max, Simulation simulation)
	{
		return SteamHelper.consumeSteamEu(inventory.getFluidInputs(), max, simulation);
	}
	
	@Override
	public long getBaseRecipeEu()
	{
		return overclock.getRecipeEu(steel ? 4 : 2);
	}
	
	@Override
	public long getMaxRecipeEu()
	{
		return this.getBaseRecipeEu();
	}
}
