package net.swedz.extended_industrialization.machines.components.farmer;

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
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.swedz.extended_industrialization.machines.components.farmer.block.FarmerBlockMap;
import net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.HarvestingHandler;
import net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.registry.FarmerHarvestingHandlers;
import net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.registry.FarmerHarvestingHandlersHolder;
import net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.registry.FarmerListener;
import net.swedz.extended_industrialization.machines.components.farmer.task.FarmerProcessRates;
import net.swedz.extended_industrialization.machines.components.farmer.task.FarmerTask;
import net.swedz.extended_industrialization.machines.components.farmer.task.FarmerTaskType;
import net.swedz.tesseract.neoforge.compat.mi.helper.MachineInventoryHelper;
import net.swedz.tesseract.neoforge.event.FarmlandLoseMoistureEvent;
import net.swedz.tesseract.neoforge.isolatedlistener.IsolatedListeners;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.stream.Stream;

public final class FarmerComponent implements IComponent
{
	private final MultiblockInventoryComponent   inventory;
	private final IsActiveComponent              isActive;
	private final FarmerComponentPlantableStacks plantableStacks;
	private final PlantingMode                   defaultPlantingMode;
	private final FarmerProcessRates             processRates;
	
	private final FarmerBlockMap                 blockMap;
	private final FarmerHarvestingHandlersHolder harvestingHandlers;
	private final List<FarmerTask>               tasks;
	
	private final List<FarmerListener<? extends Event>> listeners = Lists.newArrayList();
	
	public PlantingMode plantingMode;
	public boolean      tilling;
	
	private Level        level;
	private ShapeMatcher shapeMatcher;
	
	private int processTick;
	
	public FarmerComponent(MultiblockInventoryComponent inventory, IsActiveComponent isActive, PlantingMode defaultPlantingMode, FarmerProcessRates processRates)
	{
		this.inventory = inventory;
		this.isActive = isActive;
		this.plantableStacks = new FarmerComponentPlantableStacks(this);
		this.defaultPlantingMode = defaultPlantingMode;
		this.plantingMode = defaultPlantingMode;
		this.processRates = processRates;
		
		blockMap = new FarmerBlockMap();
		harvestingHandlers = FarmerHarvestingHandlers.create();
		tasks = Stream.of(FarmerTaskType.values())
				.filter(processRates::contains)
				.map((task) -> task.create(this))
				.toList();
		
		listeners.add(new FarmerListener<>(BlockEvent.FarmlandTrampleEvent.class, (event) ->
		{
			if(tilling && blockMap.containsDirtAt(event.getPos()))
			{
				event.setCanceled(true);
			}
		}));
		listeners.add(new FarmerListener<>(FarmlandLoseMoistureEvent.class, (event) ->
		{
			if(tilling && blockMap.containsDirtAt(event.getPos()) && consumeWater(inventory, Simulation.SIMULATE))
			{
				consumeWater(inventory, Simulation.ACT);
				event.setCanceled(true);
			}
		}));
		listeners.addAll(harvestingHandlers.getListeners(blockMap));
	}
	
	public MultiblockInventoryComponent getInventory()
	{
		return inventory;
	}
	
	public FarmerBlockMap getBlockMap()
	{
		return blockMap;
	}
	
	public FarmerHarvestingHandlersHolder getHarvestingHandlersHolder()
	{
		return harvestingHandlers;
	}
	
	public FarmerComponentPlantableStacks getPlantableStacks()
	{
		return plantableStacks;
	}
	
	public FarmerProcessRates getProcessRates()
	{
		return processRates;
	}
	
	public static boolean consumeWater(MultiblockInventoryComponent inventory, Simulation simulation)
	{
		return MachineInventoryHelper.consumeFluid(inventory.getFluidInputs(), Fluids.WATER, 50, simulation) == 50;
	}
	
	public void fromOffsets(BlockPos controllerPos, Direction controllerDirection, List<BlockPos> offsets)
	{
		blockMap.fromOffsets(level, controllerPos, controllerDirection, offsets);
	}
	
	public void updateStackListeners()
	{
		plantableStacks.update(inventory.getItemInputs());
	}
	
	public void tick()
	{
		if(level == null)
		{
			return;
		}
		
		processTick++;
		
		blockMap.markDirty();
		
		boolean hasWater = consumeWater(inventory, Simulation.SIMULATE);
		
		for(FarmerTask task : tasks)
		{
			task.run(level, plantingMode, tilling, processTick, hasWater);
		}
		
		if(processTick >= 60 * 20)
		{
			processTick = 0;
		}
	}
	
	public void registerListeners(Level level, ShapeMatcher shapeMatcher)
	{
		if(this.shapeMatcher != null)
		{
			throw new IllegalStateException("There are already listeners registered on this FarmerComponent");
		}
		this.level = level;
		this.shapeMatcher = shapeMatcher;
		for(FarmerListener listener : listeners)
		{
			IsolatedListeners.register(level, shapeMatcher.getSpannedChunks(), listener.eventClass(), listener.listener());
		}
	}
	
	public void unregisterListeners(Level level, ShapeMatcher shapeMatcher)
	{
		for(FarmerListener listener : listeners)
		{
			IsolatedListeners.unregister(level, shapeMatcher.getSpannedChunks(), listener.eventClass(), listener.listener());
		}
		this.shapeMatcher = null;
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
		tag.putBoolean("tilling", tilling);
		tag.putString("planting_mode", plantingMode.name());
		
		CompoundTag harvestingHandlersCache = new CompoundTag();
		for(HarvestingHandler harvestingHandler : harvestingHandlers.getHandlers())
		{
			harvestingHandler.writeNbt(harvestingHandlersCache);
		}
		tag.put("harvesting_handlers", harvestingHandlersCache);
		
		CompoundTag tasksTag = new CompoundTag();
		for(FarmerTask task : tasks)
		{
			CompoundTag taskTag = new CompoundTag();
			task.writeNbt(taskTag);
			if(!taskTag.isEmpty())
			{
				tasksTag.put(task.type().name(), taskTag);
			}
		}
		if(!tasksTag.isEmpty())
		{
			tag.put("tasks", tasksTag);
		}
	}
	
	@Override
	public void readNbt(CompoundTag tag, boolean isUpgradingMachine)
	{
		tilling = tag.getBoolean("tilling");
		plantingMode = PlantingMode.fromName(tag.getString("planting_mode"));
		if(plantingMode == null)
		{
			plantingMode = defaultPlantingMode;
		}
		
		CompoundTag harvestingHandlersCache = tag.getCompound("harvesting_handlers");
		for(HarvestingHandler harvestingHandler : harvestingHandlers.getHandlers())
		{
			harvestingHandler.readNbt(harvestingHandlersCache);
		}
		
		CompoundTag tasksTag = tag.getCompound("tasks");
		if(!tasksTag.isEmpty())
		{
			for(FarmerTask task : tasks)
			{
				CompoundTag taskTag = tasksTag.getCompound(task.type().name());
				task.readNbt(taskTag, isUpgradingMachine);
			}
		}
	}
}
