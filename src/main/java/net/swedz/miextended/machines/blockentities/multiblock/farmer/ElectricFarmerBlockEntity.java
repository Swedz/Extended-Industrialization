package net.swedz.miextended.machines.blockentities.multiblock.farmer;

import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.MIIdentifier;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.components.UpgradeComponent;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.swedz.miextended.machines.components.farmer.PlantingMode;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public final class ElectricFarmerBlockEntity extends FarmerBlockEntity implements EnergyListComponentHolder
{
	private static final ShapeWrapper SHAPES = new ShapeWrapper(4)
			.withCasing(
					SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(new MIIdentifier("clean_stainless_steel_machine_casing"))),
					SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(new MIIdentifier("stainless_steel_machine_casing_pipe"))),
					MachineCasings.STAINLESS_STEEL_PIPE
			)
			.withElectric()
			.complete();
	
	private final RedstoneControlComponent redstoneControl;
	private final List<EnergyComponent>    energyInputs = Lists.newArrayList();
	
	public ElectricFarmerBlockEntity(BEP bep)
	{
		super(bep, "electric_farmer", 16, PlantingMode.ALTERNATING_LINES, true, 4, SHAPES);
		
		this.redstoneControl = new RedstoneControlComponent();
		this.registerComponents(redstoneControl);
	}
	
	public static void registerReiShapes()
	{
		for(ShapeTemplate shapeTemplate : SHAPES.shapeTemplates())
		{
			ReiMachineRecipes.registerMultiblockShape("electric_farmer", shapeTemplate);
		}
	}
	
	@Override
	public long consumeEu(long max)
	{
		long total = 0;
		for(EnergyComponent energyComponent : energyInputs)
		{
			total += energyComponent.consumeEu(max - total, Simulation.ACT);
		}
		return total;
	}
	
	@Override
	public List<? extends EnergyAccess> getEnergyComponents()
	{
		return energyInputs;
	}
	
	@Override
	public void onSuccessfulMatch(ShapeMatcher shapeMatcher)
	{
		super.onSuccessfulMatch(shapeMatcher);
		
		energyInputs.clear();
		for(HatchBlockEntity hatch : shapeMatcher.getMatchedHatches())
		{
			hatch.appendEnergyInputs(energyInputs);
		}
	}
	
	@Override
	protected InteractionResult onUse(Player player, InteractionHand hand, Direction face)
	{
		InteractionResult result = super.onUse(player, hand, face);
		if(!result.consumesAction())
		{
			result = this.mapComponentOrDefault(UpgradeComponent.class, (upgrade) -> upgrade.onUse(this, player, hand), result);
		}
		if(!result.consumesAction())
		{
			result = redstoneControl.onUse(this, player, hand);
		}
		return result;
	}
}
