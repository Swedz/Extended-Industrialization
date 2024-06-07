package net.swedz.extended_industrialization.machines.blockentities.multiblock.multiplied;

import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OverclockComponent;
import aztech.modern_industrialization.machines.helper.SteamHelper;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.swedz.extended_industrialization.EIText;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

import static aztech.modern_industrialization.MITooltips.*;
import static net.swedz.extended_industrialization.EITooltips.*;

public abstract class AbstractSteamMultipliedCraftingMultiblockBlockEntity extends AbstractMultipliedCraftingMultiblockBlockEntity
{
	private final OverclockComponent overclock;
	
	private boolean steel;
	
	public AbstractSteamMultipliedCraftingMultiblockBlockEntity(BEP bep, String name, ShapeTemplate[] shapeTemplates,
																List<OverclockComponent.Catalyst> overclockCatalysts)
	{
		super(bep, name, shapeTemplates);
		
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
	public long getBaseMaxRecipeEu()
	{
		return this.getBaseRecipeEu();
	}
	
	@Override
	public List<Component> getTooltips()
	{
		List<Component> tooltips = Lists.newArrayList();
		tooltips.addAll(overclock.getTooltips());
		tooltips.add(DEFAULT_PARSER.parse(EIText.MACHINE_BATCHER_RECIPE.text(MACHINE_RECIPE_TYPE_PARSER.parse(false, this.getRecipeType()))));
		tooltips.add(DEFAULT_PARSER.parse(EIText.MACHINE_BATCHER_SIZE_AND_COST.text(DEFAULT_PARSER.parse(this.getMaxMultiplier()), EU_COST_TRANSFORMER_PARSER.parse(this.getEuCostTransformer()))));
		return tooltips;
	}
}
