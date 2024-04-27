package net.swedz.extended_industrialization.machines.blockentities.fluidharvesting.wastecollector;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.swedz.extended_industrialization.api.EuConsumerBehavior;
import net.swedz.extended_industrialization.machines.blockentities.fluidharvesting.FluidHarvestingBehavior;
import net.swedz.extended_industrialization.machines.blockentities.fluidharvesting.FluidHarvestingBehaviorCreator;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;

public final class WasteCollectorBehavior extends FluidHarvestingBehavior
{
	public static final FluidHarvestingBehaviorCreator BRONZE   = (m, e) -> new WasteCollectorBehavior(m, e, 15 * 20, 1, 0);
	public static final FluidHarvestingBehaviorCreator STEEL    = (m, e) -> new WasteCollectorBehavior(m, e, 15 * 20, 2, 0);
	public static final FluidHarvestingBehaviorCreator ELECTRIC = (m, e) -> new WasteCollectorBehavior(m, e, 15 * 20, 4, 0);
	
	private static final int MAX_ANIMALS       = 1;
	private static final int MANURE_PER_ANIMAL = 500;
	
	private final int range;
	
	private int animalsFound;
	
	private WasteCollectorBehavior(MachineBlockEntity machine, EuConsumerBehavior euBehavior, int totalPumpingTicks, float outputMultiplier, int range)
	{
		super(machine, euBehavior, totalPumpingTicks, outputMultiplier);
		this.range = range;
	}
	
	@Override
	public boolean canOperate()
	{
		animalsFound = Math.min(this.countAnimalsInArea(), MAX_ANIMALS);
		return animalsFound > 0;
	}
	
	@Override
	public void operate()
	{
		if(animalsFound == 0)
		{
			throw new IllegalStateException("Called operate with no animals found");
		}
		
		ConfigurableFluidStack fluidStack = this.getMachineBlockFluidStack();
		
		long manureToCollect = Math.min((long) this.getOutputMultiplier() * MANURE_PER_ANIMAL * animalsFound, fluidStack.getRemainingSpace());
		
		fluidStack.setKey(FluidVariant.of(EIFluids.MANURE.asFluid()));
		fluidStack.increment(manureToCollect);
	}
	
	private int countAnimalsInArea()
	{
		BoundingBox bounds = new BoundingBox(this.getMachineBlockEntity().getBlockPos());
		bounds = new BoundingBox(
				bounds.minX() - range,
				bounds.minY() + 1,
				bounds.minZ() - range,
				bounds.maxX() + range,
				bounds.maxY() + 3,
				bounds.maxZ() + range
		);
		return this.getMachineBlockEntity().getLevel().getEntitiesOfClass(Animal.class, AABB.of(bounds)).size();
	}
}
