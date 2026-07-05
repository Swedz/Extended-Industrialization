package net.swedz.extended_industrialization.machines.component.solar.electric;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.MachineComponent;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.transaction.Transaction;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.component.PhotovoltaicCell;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class SolarGeneratorComponent implements MachineComponent.ServerOnly
{
	private final MIInventory     inventory;
	private final EnergyComponent energy;
	
	private final Supplier<Float>             energyEfficiency;
	private final Predicate<PhotovoltaicCell> photovoltaicCellTest;
	
	private int tick;
	
	private PhotovoltaicCell photovoltaicCell;
	
	private boolean usedDistilledWater;
	
	public SolarGeneratorComponent(MIInventory inventory, EnergyComponent energy, Supplier<Float> energyEfficiency, Predicate<PhotovoltaicCell> photovoltaicCellTest)
	{
		this.inventory = inventory;
		this.energy = energy;
		this.energyEfficiency = energyEfficiency;
		this.photovoltaicCellTest = photovoltaicCellTest;
	}
	
	private ConfigurableFluidStack getSlotWater()
	{
		return inventory.getFluidStacks().getFirst();
	}
	
	private ConfigurableItemStack getSlotPhotovoltaicCell()
	{
		return inventory.getItemStacks().getFirst();
	}
	
	public long getEnergyPerTick()
	{
		return photovoltaicCell != null ? (long) (photovoltaicCell.euPerTick() * energyEfficiency.get() * (usedDistilledWater ? 1.5 : 1)) : 0;
	}
	
	private boolean tryUseDistilledWater()
	{
		try(var transaction = Transaction.openRoot())
		{
			boolean usedDistilledWater = this.getSlotWater().extractDirect(FluidVariant.of(EIFluids.DISTILLED_WATER.asFluid()), 1, transaction) > 0;
			transaction.commit();
			return usedDistilledWater;
		}
	}
	
	private void incrementSolarTicks(ItemStack stack)
	{
		int solarTicks = stack.getOrDefault(EIComponents.SOLAR_TICKS, 0) + 1;
		if(solarTicks > photovoltaicCell.durationTicks())
		{
			return;
		}
		stack.set(EIComponents.SOLAR_TICKS, solarTicks);
	}
	
	private int getSolarTicksRemaining(ItemStack stack)
	{
		int solarTicks = stack.getOrDefault(EIComponents.SOLAR_TICKS, 0);
		return photovoltaicCell.durationTicks() - solarTicks;
	}
	
	private void deterioratePhotovoltaicCell()
	{
		var slotCell = this.getSlotPhotovoltaicCell();
		var cellStack = slotCell.toStack();
		this.incrementSolarTicks(cellStack);
		if(this.getSolarTicksRemaining(cellStack) > 0)
		{
			slotCell.setKey(ItemVariant.of(cellStack));
		}
		else
		{
			photovoltaicCell = null;
			usedDistilledWater = false;
			slotCell.setAmount(0);
			slotCell.setKey(ItemVariant.blank());
		}
	}
	
	public void tick()
	{
		tick++;
		if(tick > 20)
		{
			tick = 1;
		}
		
		var resource = this.getSlotPhotovoltaicCell().getResource();
		var components = PatchedDataComponentMap.fromPatch(resource.getItem().components(), resource.getComponentsPatch());
		var photovoltaicCellComponent = components.get(EIComponents.PHOTOVOLTAIC_CELL.get());
		if(photovoltaicCellComponent != null && photovoltaicCellTest.test(photovoltaicCellComponent))
		{
			photovoltaicCell = photovoltaicCellComponent;
			
			usedDistilledWater = this.tryUseDistilledWater();
			boolean deterioratePhotovoltaicCell = (!usedDistilledWater || tick % 2 == 0) && !photovoltaicCell.lastsForever();
			
			if(deterioratePhotovoltaicCell)
			{
				this.deterioratePhotovoltaicCell();
			}
			
			energy.insertEu(this.getEnergyPerTick(), Simulation.ACT);
		}
		else
		{
			photovoltaicCell = null;
			
			usedDistilledWater = false;
		}
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
	}
}
