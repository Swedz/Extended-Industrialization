package net.swedz.miextended.machines.components.farmer.task;

import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import net.minecraft.world.level.Level;
import net.swedz.miextended.machines.components.farmer.FarmerComponentPlantableStacks;
import net.swedz.miextended.machines.components.farmer.PlantingMode;
import net.swedz.miextended.machines.components.farmer.block.FarmerBlockMap;

public abstract class FarmerTask
{
	protected final MultiblockInventoryComponent   inventory;
	protected final FarmerBlockMap                 blockMap;
	protected final FarmerComponentPlantableStacks plantableStacks;
	protected final FarmerTaskOperations           operations;
	
	protected Level        level;
	protected PlantingMode plantingMode;
	protected boolean      tilling;
	
	protected int     processTick;
	protected boolean hasWater;
	
	public FarmerTask(MultiblockInventoryComponent inventory, FarmerBlockMap blockMap, FarmerComponentPlantableStacks plantableStacks, int maxOperations)
	{
		this.inventory = inventory;
		this.blockMap = blockMap;
		this.plantableStacks = plantableStacks;
		this.operations = new FarmerTaskOperations(maxOperations);
	}
	
	public boolean run(Level level, PlantingMode plantingMode, boolean tilling, int processTick, boolean hasWater)
	{
		operations.reset();
		this.level = level;
		this.plantingMode = plantingMode;
		this.tilling = tilling;
		this.processTick = processTick;
		this.hasWater = hasWater;
		return this.run();
	}
	
	protected abstract boolean run();
}
