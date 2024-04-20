package net.swedz.miextended.machines.components.farmer.task.tasks;

import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import net.swedz.miextended.machines.components.farmer.FarmerComponentPlantableStacks;
import net.swedz.miextended.machines.components.farmer.block.FarmerBlockMap;
import net.swedz.miextended.machines.components.farmer.task.FarmerTask;

public final class FertilizingFarmerTask extends FarmerTask
{
	public FertilizingFarmerTask(MultiblockInventoryComponent inventory, FarmerBlockMap blockMap, FarmerComponentPlantableStacks plantableStacks, int maxOperations)
	{
		super(inventory, blockMap, plantableStacks, maxOperations);
	}
	
	@Override
	protected boolean run()
	{
		return false;
	}
}
