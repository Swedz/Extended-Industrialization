package net.swedz.extended_industrialization.machines.components.fluidharvesting;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.swedz.extended_industrialization.api.EuConsumerBehavior;

public interface FluidHarvestingBehaviorCreator
{
	FluidHarvestingBehavior create(MachineBlockEntity machine, EuConsumerBehavior euConsumerBehavior);
}