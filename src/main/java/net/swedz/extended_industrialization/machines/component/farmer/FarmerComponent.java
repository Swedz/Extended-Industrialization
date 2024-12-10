package net.swedz.extended_industrialization.machines.component.farmer;

import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.util.Simulation;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.swedz.extended_industrialization.EILocalizedListeners;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerBlockMap;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.FarmerListener;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.HarvestableBehavior;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.HarvestableBehaviorHolder;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.HarvestingContext;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.harvestable.CropBlockHarvestable;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.harvestable.NetherWartHarvestable;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.harvestable.SimpleTallCropHarvestable;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.harvestable.TreeHarvestable;
import net.swedz.extended_industrialization.machines.component.farmer.planting.FarmerPlantable;
import net.swedz.extended_industrialization.machines.component.farmer.planting.PlantableBehaviorHolder;
import net.swedz.extended_industrialization.machines.component.farmer.planting.PlantingContext;
import net.swedz.extended_industrialization.machines.component.farmer.planting.plantable.SpecialFarmerPlantable;
import net.swedz.extended_industrialization.machines.component.farmer.planting.plantable.StandardFarmerPlantable;
import net.swedz.extended_industrialization.machines.component.farmer.task.FarmerProcessRates;
import net.swedz.extended_industrialization.machines.component.farmer.task.FarmerTask;
import net.swedz.extended_industrialization.machines.component.farmer.task.FarmerTaskType;
import net.swedz.tesseract.neoforge.behavior.BehaviorRegistry;
import net.swedz.tesseract.neoforge.compat.mi.helper.MachineInventoryHelper;
import net.swedz.tesseract.neoforge.event.FarmlandLoseMoistureEvent;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class FarmerComponent implements IComponent
{
	private static final BehaviorRegistry<PlantableBehaviorHolder, FarmerPlantable, PlantingContext>         PLANTABLE_REGISTRY   = BehaviorRegistry.create(PlantableBehaviorHolder::new);
	private static final BehaviorRegistry<HarvestableBehaviorHolder, HarvestableBehavior, HarvestingContext> HARVESTABLE_REGISTRY = BehaviorRegistry.create(HarvestableBehaviorHolder::new);
	
	public static void registerPlantable(Supplier<FarmerPlantable> creator)
	{
		PLANTABLE_REGISTRY.register(creator);
	}
	
	public static void registerHarvestable(Supplier<HarvestableBehavior> creator)
	{
		HARVESTABLE_REGISTRY.register(creator);
	}
	
	static
	{
		registerPlantable(StandardFarmerPlantable::new);
		registerPlantable(SpecialFarmerPlantable::new);
		
		registerHarvestable(CropBlockHarvestable::new);
		registerHarvestable(NetherWartHarvestable::new);
		registerHarvestable(SimpleTallCropHarvestable::new);
		registerHarvestable(TreeHarvestable::new);
	}
	
	private final MultiblockInventoryComponent   inventory;
	private final IsActiveComponent              isActive;
	private final FarmerComponentPlantableStacks plantableStacks;
	private final PlantingMode                   defaultPlantingMode;
	private final FarmerProcessRates             processRates;
	
	private final FarmerBlockMap            blockMap;
	private final PlantableBehaviorHolder   plantableBehaviorHolder;
	private final HarvestableBehaviorHolder harvestableBehaviorHolder;
	private final List<FarmerTask>          tasks;
	
	private final List<FarmerListener<? extends Event>> listeners = Lists.newArrayList();
	
	private Level   level;
	private boolean listenersRegistered = false;
	
	public PlantingMode plantingMode;
	public boolean      tilling;
	
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
		plantableBehaviorHolder = PLANTABLE_REGISTRY.createHolder();
		harvestableBehaviorHolder = HARVESTABLE_REGISTRY.createHolder();
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
		listeners.addAll(harvestableBehaviorHolder.listeners(blockMap));
	}
	
	public MultiblockInventoryComponent getInventory()
	{
		return inventory;
	}
	
	public FarmerBlockMap getBlockMap()
	{
		return blockMap;
	}
	
	public PlantableBehaviorHolder getPlantableBehaviorHolder()
	{
		return plantableBehaviorHolder;
	}
	
	public HarvestableBehaviorHolder getHarvestableBehaviorHolder()
	{
		return harvestableBehaviorHolder;
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
	
	public List<Component> getTaskTooltipLines()
	{
		return tasks.stream()
				.map((task) -> task.type().tooltip())
				.filter(Objects::nonNull)
				.toList();
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
		if(listenersRegistered)
		{
			throw new IllegalStateException("There are already listeners registered on this FarmerComponent");
		}
		listenersRegistered = true;
		this.level = level;
		for(FarmerListener listener : listeners)
		{
			EILocalizedListeners.INSTANCE.register(level, shapeMatcher.getSpannedChunks(), listener.eventClass(), listener.listener());
		}
	}
	
	public void unregisterListeners(Level level, ShapeMatcher shapeMatcher)
	{
		for(FarmerListener listener : listeners)
		{
			EILocalizedListeners.INSTANCE.unregister(level, shapeMatcher.getSpannedChunks(), listener.eventClass(), listener.listener());
		}
		this.listenersRegistered = false;
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		tag.putBoolean("tilling", tilling);
		tag.putString("planting_mode", plantingMode.name());
		
		CompoundTag harvestingHandlersCache = new CompoundTag();
		for(HarvestableBehavior harvestingHandler : harvestableBehaviorHolder.behaviors())
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
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
		tilling = tag.getBoolean("tilling");
		plantingMode = PlantingMode.fromName(tag.getString("planting_mode"));
		if(plantingMode == null)
		{
			plantingMode = defaultPlantingMode;
		}
		
		CompoundTag harvestingHandlersCache = tag.getCompound("harvesting_handlers");
		for(HarvestableBehavior harvestingHandler : harvestableBehaviorHolder.behaviors())
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
