package net.swedz.extended_industrialization.machines.component.solar.electric;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.transaction.Transaction;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.items.PhotovoltaicCellItem;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class SolarGeneratorComponent implements IComponent.ServerOnly
{
	private final MIInventory     inventory;
	private final EnergyComponent energy;
	
	private final Supplier<Float>                 energyEfficiency;
	private final Predicate<PhotovoltaicCellItem> photovoltaicCellTest;
	
	private int tick;
	
	private PhotovoltaicCellItem photovoltaicCell;
	
	public SolarGeneratorComponent(MIInventory inventory, EnergyComponent energy, Supplier<Float> energyEfficiency, Predicate<PhotovoltaicCellItem> photovoltaicCellTest)
	{
		this.inventory = inventory;
		this.energy = energy;
		this.energyEfficiency = energyEfficiency;
		this.photovoltaicCellTest = photovoltaicCellTest;
	}
	
	private ConfigurableFluidStack getSlotWater()
	{
		return inventory.getFluidStacks().get(0);
	}
	
	private ConfigurableItemStack getSlotPhotovoltaicCell()
	{
		return inventory.getItemStacks().get(0);
	}
	
	public long getEnergyPerTick()
	{
		return photovoltaicCell != null ? (long) (photovoltaicCell.getEuPerTick() * energyEfficiency.get()) : 0;
	}
	
	private boolean tryUseDistilledWater()
	{
		try (Transaction transaction = Transaction.openOuter())
		{
			boolean usedDistilledWater = this.getSlotWater().extractDirect(FluidVariant.of(EIFluids.DISTILLED_WATER.asFluid()), 1, transaction) > 0;
			transaction.commit();
			return usedDistilledWater;
		}
	}
	
	private void deterioratePhotovoltaicCell()
	{
		ConfigurableItemStack slotCell = this.getSlotPhotovoltaicCell();
		ItemStack cellStack = slotCell.toStack();
		photovoltaicCell.incrementTick(cellStack);
		if(photovoltaicCell.getSolarTicksRemaining(cellStack) > 0)
		{
			slotCell.setKey(ItemVariant.of(cellStack));
		}
		else
		{
			photovoltaicCell = null;
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
		
		if(this.getSlotPhotovoltaicCell().getResource().getItem() instanceof PhotovoltaicCellItem cellItem && photovoltaicCellTest.test(cellItem))
		{
			photovoltaicCell = cellItem;
			
			boolean usedDistilledWater = this.tryUseDistilledWater();
			boolean deterioratePhotovoltaicCell = !usedDistilledWater || tick % 2 == 0;
			
			if(deterioratePhotovoltaicCell)
			{
				this.deterioratePhotovoltaicCell();
			}
			
			energy.insertEu(this.getEnergyPerTick(), Simulation.ACT);
		}
		else
		{
			photovoltaicCell = null;
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
