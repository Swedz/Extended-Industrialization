package net.swedz.miextended.machines.blockentities.honeyextractor;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.helper.SteamHelper;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Simulation;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.miextended.fluids.MIEFluids;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SteamHoneyExtractorMachineBlockEntity extends HoneyExtractorMachineBlockEntity
{
	private final MIInventory inventory;
	
	public final boolean bronze;
	
	public SteamHoneyExtractorMachineBlockEntity(BEP bep, boolean bronze)
	{
		super(bep, bronze ? "bronze_honey_extractor" : "steel_honey_extractor", bronze ? 1 : 2);
		
		long capacity = FluidType.BUCKET_VOLUME * (bronze ? 8 : 16);
		
		List<ConfigurableFluidStack> fluidStacks = Arrays.asList(
				ConfigurableFluidStack.lockedInputSlot(capacity, MIFluids.STEAM.asFluid()),
				ConfigurableFluidStack.lockedOutputSlot(capacity, MIEFluids.HONEY.asFluid())
		);
		SlotPositions fluidPositions = new SlotPositions.Builder().addSlot(21, 30).addSlot(OUTPUT_SLOT_X, OUTPUT_SLOT_Y).build();
		this.inventory = new MIInventory(Collections.emptyList(), fluidStacks, SlotPositions.empty(), fluidPositions);
		
		this.bronze = bronze;
		
		this.registerComponents(inventory);
	}
	
	@Override
	protected long consumeEu(long max)
	{
		return SteamHelper.consumeSteamEu(inventory.getFluidStacks(), max, Simulation.ACT);
	}
	
	@Override
	protected int getHoneyMultiplier()
	{
		return 1;
	}
	
	@Override
	public MIInventory getInventory()
	{
		return inventory;
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
