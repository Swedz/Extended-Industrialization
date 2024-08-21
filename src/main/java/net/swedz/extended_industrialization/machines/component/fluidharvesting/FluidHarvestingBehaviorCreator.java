package net.swedz.extended_industrialization.machines.component.fluidharvesting;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.swedz.tesseract.neoforge.compat.mi.helper.EuConsumerBehavior;

public interface FluidHarvestingBehaviorCreator
{
	FluidHarvestingBehavior create(MachineBlockEntity machine, EuConsumerBehavior euConsumerBehavior);
}
