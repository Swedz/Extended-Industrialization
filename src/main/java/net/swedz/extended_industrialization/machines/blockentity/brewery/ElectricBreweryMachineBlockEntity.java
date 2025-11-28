package net.swedz.extended_industrialization.machines.blockentity.brewery;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyComponentHolder;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.LubricantHelper;
import aztech.modern_industrialization.machines.components.OverdriveComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.components.UpgradeComponent;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.swedz.extended_industrialization.EI;
import net.swedz.tesseract.neoforge.compat.mi.machine.builder.MachineGuiConfiguration;

public final class ElectricBreweryMachineBlockEntity extends BreweryMachineBlockEntity implements EnergyComponentHolder
{
	private final RedstoneControlComponent redstoneControl;
	private final CasingComponent          casing;
	private final UpgradeComponent         upgrades;
	private final OverdriveComponent       overdrive;
	
	private final EnergyComponent energy;
	private final MIEnergyStorage insertable;
	
	public ElectricBreweryMachineBlockEntity(BEP bep, MachineGuiConfiguration gui)
	{
		super(bep, MachineTier.LV, gui.createGuiParams(EI.id("electric_brewery")), gui.buildInventory());
		
		redstoneControl = new RedstoneControlComponent();
		casing = new CasingComponent();
		upgrades = new UpgradeComponent();
		overdrive = new OverdriveComponent();
		
		energy = new EnergyComponent(this, casing::getEuCapacity);
		insertable = energy.buildInsertable(casing::canInsertEu);
		
		gui.registerProgressBar(this, crafter::getProgress);
		gui.registerEnergyBar(this, energy::getEu, energy::getCapacity);
		gui.registerEfficiencyBar(this, crafter);
		
		this.registerGuiComponent(new SlotPanel(this)
				.withRedstoneControl(redstoneControl)
				.withUpgrades(upgrades)
				.withCasing(casing)
				.withOverdrive(overdrive));
		
		this.registerComponents(energy, redstoneControl, casing, upgrades, overdrive);
	}
	
	@Override
	public boolean isEnabled()
	{
		return redstoneControl.doAllowNormalOperation(this);
	}
	
	@Override
	public long consumeEu(long max, Simulation simulation)
	{
		return energy.consumeEu(max, simulation);
	}
	
	@Override
	public long getMaxRecipeEu()
	{
		return tier.getMaxEu() + upgrades.getAddMaxEUPerTick();
	}
	
	@Override
	public EnergyAccess getEnergyComponent()
	{
		return energy;
	}
	
	@Override
	public boolean isOverdriving()
	{
		return overdrive.shouldOverdrive();
	}
	
	@Override
	protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face)
	{
		ItemInteractionResult result = super.useItemOn(player, hand, face);
		if(!result.consumesAction())
		{
			result = redstoneControl.onUse(this, player, hand);
		}
		if(!result.consumesAction())
		{
			result = casing.onUse(this, player, hand);
		}
		if(!result.consumesAction())
		{
			result = upgrades.onUse(this, player, hand);
		}
		if(!result.consumesAction())
		{
			result = LubricantHelper.onUse(crafter, player, hand);
		}
		return result;
	}
	
	@Override
	public MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData(casing.getCasing());
		orientation.writeModelData(data);
		data.isActive = isActiveComponent.isActive;
		return data;
	}
	
	public static void registerEnergyApi(BlockEntityType<?> bet)
	{
		MICapabilities.onEvent((event) ->
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) -> ((ElectricBreweryMachineBlockEntity) be).insertable));
	}
}
