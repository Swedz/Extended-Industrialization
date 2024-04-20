package net.swedz.miextended.machines.components.farmer.task;

import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import net.swedz.miextended.machines.components.farmer.FarmerComponentPlantableStacks;
import net.swedz.miextended.machines.components.farmer.block.FarmerBlockMap;

public interface FarmerTaskFactory
{
	FarmerTask create(MultiblockInventoryComponent inventory, FarmerBlockMap blockMap, FarmerComponentPlantableStacks plantableStacks, int maxOperations);
}
