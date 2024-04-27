package net.swedz.extended_industrialization.machines.blockentities.fluidharvesting;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyComponentHolder;
import aztech.modern_industrialization.definition.FluidLike;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Collections;
import java.util.List;

public abstract class ElectricFluidHarvestingMachineBlockEntity extends FluidHarvestingMachineBlockEntity implements EnergyComponentHolder
{
	protected final MIInventory inventory;
	
	protected final EnergyComponent          energy;
	protected final MIEnergyStorage          insertable;
	protected final RedstoneControlComponent redstoneControl;
	
	public ElectricFluidHarvestingMachineBlockEntity(BEP bep, String blockName, long euCost, long capacity, FluidLike fluid)
	{
		super(bep, blockName, euCost);
		
		List<ConfigurableFluidStack> fluidStacks = Collections.singletonList(
				ConfigurableFluidStack.lockedOutputSlot(capacity, fluid.asFluid())
		);
		SlotPositions fluidPositions = new SlotPositions.Builder().addSlot(OUTPUT_SLOT_X, OUTPUT_SLOT_Y).build();
		this.inventory = new MIInventory(Collections.emptyList(), fluidStacks, SlotPositions.empty(), fluidPositions);
		
		this.energy = new EnergyComponent(this, 3200);
		this.insertable = energy.buildInsertable((tier) -> tier == CableTier.LV);
		this.redstoneControl = new RedstoneControlComponent();
		
		this.registerComponents(energy, inventory, redstoneControl);
		
		this.registerGuiComponent(new EnergyBar.Server(
				new EnergyBar.Parameters(18, 29), energy::getEu, energy::getCapacity
		));
		this.registerGuiComponent(new SlotPanel.Server(this).withRedstoneControl(redstoneControl));
	}
	
	@Override
	public MIInventory getInventory()
	{
		return inventory;
	}
	
	@Override
	public EnergyAccess getEnergyComponent()
	{
		return energy;
	}
	
	public static void registerEnergyApi(BlockEntityType<?> bet)
	{
		MICapabilities.onEvent((event) ->
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) -> ((ElectricFluidHarvestingMachineBlockEntity) be).insertable));
	}
}
