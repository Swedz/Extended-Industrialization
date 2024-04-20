package net.swedz.miextended.machines.components.farmer;

import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.swedz.miextended.api.MachineInventoryHelper;
import net.swedz.miextended.api.event.FarmlandLoseMoistureEvent;
import net.swedz.miextended.api.event.TreeGrowthEvent;
import net.swedz.miextended.api.isolatedlistener.IsolatedListener;
import net.swedz.miextended.api.isolatedlistener.IsolatedListeners;
import net.swedz.miextended.machines.components.farmer.block.FarmerBlockMap;
import net.swedz.miextended.machines.components.farmer.task.FarmerTask;
import net.swedz.miextended.machines.components.farmer.task.FarmerTaskFactory;
import net.swedz.miextended.machines.components.farmer.task.tasks.FertilizingFarmerTask;
import net.swedz.miextended.machines.components.farmer.task.tasks.HarvestingFarmerTask;
import net.swedz.miextended.machines.components.farmer.task.tasks.HydratingFarmerTask;
import net.swedz.miextended.machines.components.farmer.task.tasks.PlantingFarmerTask;
import net.swedz.miextended.machines.components.farmer.task.tasks.TillingFarmerTask;

import java.util.List;

public final class FarmerComponent implements IComponent
{
	private final MultiblockInventoryComponent   inventory;
	private final IsActiveComponent              isActive;
	private final FarmerComponentPlantableStacks plantableStacks;
	private final PlantingMode                   defaultPlantingMode;
	private final int                            maxOperationsPerTask;
	
	private final FarmerBlockMap   blockMap;
	private final List<FarmerTask> tasks;
	
	private final IsolatedListener<FarmlandLoseMoistureEvent> listenerFarmlandLoseMoisture;
	private final IsolatedListener<TreeGrowthEvent>           listenerTreeGrowth;
	
	public PlantingMode plantingMode;
	public boolean      tilling;
	
	private Level        level;
	private ShapeMatcher shapeMatcher;
	
	private int processTick;
	
	public FarmerComponent(MultiblockInventoryComponent inventory, IsActiveComponent isActive, PlantingMode defaultPlantingMode, int maxOperationsPerTask)
	{
		this.inventory = inventory;
		this.isActive = isActive;
		this.plantableStacks = new FarmerComponentPlantableStacks(this);
		this.defaultPlantingMode = defaultPlantingMode;
		this.plantingMode = defaultPlantingMode;
		this.maxOperationsPerTask = maxOperationsPerTask;
		
		this.blockMap = new FarmerBlockMap();
		List<FarmerTaskFactory> taskFactories = List.of(
				TillingFarmerTask::new,
				HydratingFarmerTask::new,
				FertilizingFarmerTask::new,
				HarvestingFarmerTask::new,
				PlantingFarmerTask::new
		);
		this.tasks = taskFactories.stream().map((f) -> f.create(inventory, blockMap, plantableStacks, maxOperationsPerTask)).toList();
		
		this.listenerFarmlandLoseMoisture = (event) ->
		{
			if(isActive.isActive && this.consumeWater(Simulation.SIMULATE))
			{
				this.consumeWater(Simulation.ACT);
				event.setCanceled(true);
			}
		};
		this.listenerTreeGrowth = (event) ->
		{
			if(blockMap.containsDirtAt(event.getPos().below()))
			{
				// TODO server reboot will make trees get forgotten...
				blockMap.addTree(event.getPos(), event.getPositions());
			}
		};
	}
	
	public void fromOffsets(BlockPos controllerPos, Direction controllerDirection, List<BlockPos> offsets)
	{
		blockMap.fromOffsets(level, controllerPos, controllerDirection, offsets);
	}
	
	public void updateStackListeners()
	{
		plantableStacks.update(inventory.getItemInputs());
	}
	
	private boolean consumeWater(Simulation simulation)
	{
		return MachineInventoryHelper.consumeFluid(inventory.getFluidInputs(), Fluids.WATER, 50, simulation) == 50;
	}
	
	public void tick()
	{
		if(level == null)
		{
			return;
		}
		
		processTick++;
		
		blockMap.markDirty();
		
		boolean hasWater = this.consumeWater(Simulation.SIMULATE);
		
		for(FarmerTask task : tasks)
		{
			task.run(level, plantingMode, tilling, processTick, hasWater);
		}
		
		if(processTick >= 20)
		{
			processTick = 0;
		}
	}
	
	public void registerListeners(Level level, ShapeMatcher shapeMatcher)
	{
		this.level = level;
		this.shapeMatcher = shapeMatcher;
		IsolatedListeners.register(level, shapeMatcher.getSpannedChunks(), FarmlandLoseMoistureEvent.class, listenerFarmlandLoseMoisture);
		IsolatedListeners.register(level, shapeMatcher.getSpannedChunks(), TreeGrowthEvent.class, listenerTreeGrowth);
	}
	
	public void unregisterListeners(Level level, ShapeMatcher shapeMatcher)
	{
		IsolatedListeners.unregister(level, shapeMatcher.getSpannedChunks(), FarmlandLoseMoistureEvent.class, listenerFarmlandLoseMoisture);
		IsolatedListeners.unregister(level, shapeMatcher.getSpannedChunks(), TreeGrowthEvent.class, listenerTreeGrowth);
		this.shapeMatcher = null;
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
		tag.putBoolean("tilling", tilling);
		tag.putInt("planting_mode", plantingMode.ordinal());
	}
	
	@Override
	public void readNbt(CompoundTag tag, boolean isUpgradingMachine)
	{
		tilling = tag.getBoolean("tilling");
		plantingMode = PlantingMode.values()[tag.contains("planting_mode") ? tag.getInt("planting_mode") : defaultPlantingMode.ordinal()];
	}
}
