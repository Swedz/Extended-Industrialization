package net.swedz.extended_industrialization.machines.blockentity.multiblock.farmer;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.components.UpgradeComponent;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import aztech.modern_industrialization.util.Simulation;
import com.google.common.collect.Lists;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIMachines;
import net.swedz.extended_industrialization.machines.component.farmer.PlantingMode;
import net.swedz.extended_industrialization.machines.component.farmer.task.FarmerProcessRates;
import net.swedz.extended_industrialization.machines.component.farmer.task.FarmerTaskType;

import java.util.List;

public final class ElectricFarmerBlockEntity extends FarmerBlockEntity implements EnergyListComponentHolder
{
	private static final ShapeWrapper SHAPES = new ShapeWrapper(4)
			.withCasing(
					SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("steel_machine_casing"))),
					SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("steel_machine_casing_pipe"))),
					EIMachines.Casings.STEEL_PIPE
			)
			.withElectric()
			.complete();
	
	private static final FarmerProcessRates PROCESS_RATES = new FarmerProcessRates()
			.with(FarmerTaskType.TILLING, 1, 1)
			.with(FarmerTaskType.HYDRATING, 1, 1)
			.with(FarmerTaskType.FERTLIZING, 1, 1)
			.with(FarmerTaskType.HARVESTING, Integer.MAX_VALUE, 5)
			.with(FarmerTaskType.PLANTING, 1, 5);
	
	private final RedstoneControlComponent redstoneControl;
	private final List<EnergyComponent>    energyInputs = Lists.newArrayList();
	
	public ElectricFarmerBlockEntity(BEP bep)
	{
		super(bep, EI.id("electric_farmer"), 16, PlantingMode.ALTERNATING_LINES, true, PROCESS_RATES, SHAPES);
		
		this.redstoneControl = new RedstoneControlComponent();
		this.registerComponents(redstoneControl);
		
		this.registerGuiComponent(new SlotPanel.Server(this)
				.withRedstoneControl(redstoneControl));
	}
	
	public static void registerReiShapes()
	{
		int index = 0;
		for(ShapeTemplate shapeTemplate : SHAPES.shapeTemplates())
		{
			ReiMachineRecipes.registerMultiblockShape(EI.id("electric_farmer"), shapeTemplate, "" + index);
			index++;
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
	protected void onRematch(ShapeMatcher shapeMatcher)
	{
		super.onRematch(shapeMatcher);
		
		if(shapeMatcher.isMatchSuccessful())
		{
			energyInputs.clear();
			for(HatchBlockEntity hatch : shapeMatcher.getMatchedHatches())
			{
				hatch.appendEnergyInputs(energyInputs);
			}
		}
	}
	
	@Override
	protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face)
	{
		ItemInteractionResult result = super.useItemOn(player, hand, face);
		if(!result.consumesAction())
		{
			result = components.mapOrDefault(UpgradeComponent.class, (upgrade) -> upgrade.onUse(this, player, hand), result);
		}
		if(!result.consumesAction())
		{
			result = redstoneControl.onUse(this, player, hand);
		}
		return result;
	}
	
	@Override
	public boolean isEnabled()
	{
		return redstoneControl.doAllowNormalOperation(this);
	}
}
