package net.swedz.extended_industrialization.machines.components.farmer.task;

import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.swedz.extended_industrialization.machines.components.farmer.FarmerComponent;
import net.swedz.extended_industrialization.machines.components.farmer.FarmerComponentPlantableStacks;
import net.swedz.extended_industrialization.machines.components.farmer.PlantingMode;
import net.swedz.extended_industrialization.machines.components.farmer.block.FarmerBlockMap;

public abstract class FarmerTask
{
	protected final FarmerTaskType                 type;
	protected final MultiblockInventoryComponent   inventory;
	protected final FarmerBlockMap                 blockMap;
	protected final FarmerComponentPlantableStacks plantableStacks;
	protected final FarmerTaskOperations           operations;
	protected final int                            processInterval;
	
	protected Level        level;
	protected PlantingMode plantingMode;
	protected boolean      tilling;
	
	protected int     processTick;
	protected boolean hasWater;
	
	public FarmerTask(FarmerTaskType type, FarmerComponent component)
	{
		this.type = type;
		this.inventory = component.getInventory();
		this.blockMap = component.getBlockMap();
		this.plantableStacks = component.getPlantableStacks();
		this.operations = new FarmerTaskOperations(component.getProcessRates().maxOperations(type));
		this.processInterval = component.getProcessRates().interval(type);
	}
	
	public FarmerTaskType type()
	{
		return type;
	}
	
	public boolean run(Level level, PlantingMode plantingMode, boolean tilling, int processTick, boolean hasWater)
	{
		if(operations.max() == 0 || processInterval == 0 || processTick % processInterval != 0)
		{
			return false;
		}
		operations.reset();
		this.level = level;
		this.plantingMode = plantingMode;
		this.tilling = tilling;
		this.processTick = processTick;
		this.hasWater = hasWater;
		return this.run();
	}
	
	protected abstract boolean run();
	
	public void writeNbt(CompoundTag tag)
	{
	}
	
	public void readNbt(CompoundTag tag, boolean isUpgradingMachine)
	{
	}
}
