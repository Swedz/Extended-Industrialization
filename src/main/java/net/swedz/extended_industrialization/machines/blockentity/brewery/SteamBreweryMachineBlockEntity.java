package net.swedz.extended_industrialization.machines.blockentity.brewery;

import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OverclockComponent;
import aztech.modern_industrialization.machines.guicomponents.GunpowderOverclockGui;
import aztech.modern_industrialization.machines.helper.SteamHelper;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.util.Simulation;
import com.google.common.collect.Lists;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.swedz.extended_industrialization.EI;
import net.swedz.tesseract.neoforge.compat.mi.machine.builder.MachineGuiConfiguration;

import java.util.List;

public final class SteamBreweryMachineBlockEntity extends BreweryMachineBlockEntity
{
	private final OverclockComponent overclockComponent;
	
	public SteamBreweryMachineBlockEntity(BEP bep, MachineGuiConfiguration gui, boolean bronze)
	{
		super(bep, bronze ? MachineTier.BRONZE : MachineTier.STEEL, gui.createGuiParams(EI.id((bronze ? "bronze" : "steel") + "_brewery")), gui.buildInventory());
		
		gui.registerProgressBar(this, crafter::getProgress);
		
		this.overclockComponent = new OverclockComponent(OverclockComponent.getDefaultCatalysts()); // TODO allow kjs to hook into this
		GunpowderOverclockGui.Parameters gunpowderOverclockGuiParams = new GunpowderOverclockGui.Parameters(gui.getProgressBar().renderX, gui.getProgressBar().renderY + 20);
		this.registerGuiComponent(new GunpowderOverclockGui.Server(gunpowderOverclockGuiParams, overclockComponent::getTicks));
		this.registerComponents(overclockComponent);
	}
	
	@Override
	public long consumeEu(long max, Simulation simulation)
	{
		return SteamHelper.consumeSteamEu(inventory.getFluidInputs(), max, simulation);
	}
	
	@Override
	public long getMaxRecipeEu()
	{
		return overclockComponent.getRecipeEu(tier.getMaxEu());
	}
	
	@Override
	public long getBaseRecipeEu()
	{
		return overclockComponent.getRecipeEu(tier.getBaseEu());
	}
	
	@Override
	public List<Component> getTooltips()
	{
		List<Component> tooltips = Lists.newArrayList();
		tooltips.addAll(overclockComponent.getTooltips());
		tooltips.addAll(super.getTooltips());
		return tooltips;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		overclockComponent.tick(this);
	}
	
	@Override
	protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face)
	{
		ItemInteractionResult result = super.useItemOn(player, hand, face);
		if(!result.consumesAction())
		{
			return overclockComponent.onUse(this, player, hand);
		}
		return result;
	}
}
