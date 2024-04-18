package net.swedz.miextended.mi.machines.blockentities.honeyextractor;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.api.machine.holder.EnergyComponentHolder;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.miextended.fluids.MIEFluids;

import java.util.Collections;
import java.util.List;

public final class ElectricHoneyExtractorMachineBlockEntity extends HoneyExtractorMachineBlockEntity implements EnergyComponentHolder
{
	private final MIInventory inventory;
	
	private final EnergyComponent          energy;
	private final MIEnergyStorage          insertable;
	private final RedstoneControlComponent redstoneControl;
	
	public ElectricHoneyExtractorMachineBlockEntity(BEP bep)
	{
		super(bep, "electric_honey_extractor");
		
		long capacity = FluidType.BUCKET_VOLUME * 32;
		
		List<ConfigurableFluidStack> fluidStacks = Collections.singletonList(
				ConfigurableFluidStack.lockedOutputSlot(capacity, MIEFluids.HONEY.asFluid())
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
	protected long consumeEu(long max)
	{
		return redstoneControl.doAllowNormalOperation(this) ? energy.consumeEu(max, Simulation.ACT) : 0;
	}
	
	@Override
	protected int getHoneyMultiplier()
	{
		return 2;
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
				event.registerBlockEntity(EnergyApi.SIDED, bet, (be, direction) -> ((ElectricHoneyExtractorMachineBlockEntity) be).insertable));
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData();
		data.isActive = isActiveComponent.isActive;
		orientation.writeModelData(data);
		return data;
	}
}
