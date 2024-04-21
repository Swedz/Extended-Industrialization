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
import net.neoforged.neoforge.event.level.BlockEvent;
import net.swedz.miextended.api.MachineInventoryHelper;
import net.swedz.miextended.api.event.FarmlandLoseMoistureEvent;
import net.swedz.miextended.api.event.TreeGrowthEvent;
import net.swedz.miextended.api.isolatedlistener.IsolatedListener;
import net.swedz.miextended.api.isolatedlistener.IsolatedListeners;
import net.swedz.miextended.machines.components.farmer.block.FarmerBlockMap;
import net.swedz.miextended.machines.components.farmer.block.FarmerTree;
import net.swedz.miextended.machines.components.farmer.task.FarmerProcessRates;
import net.swedz.miextended.machines.components.farmer.task.FarmerTask;
import net.swedz.miextended.machines.components.farmer.task.FarmerTaskType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class FarmerComponent implements IComponent
{
	private final MultiblockInventoryComponent   inventory;
	private final IsActiveComponent              isActive;
	private final FarmerComponentPlantableStacks plantableStacks;
	private final PlantingMode                   defaultPlantingMode;
	private final FarmerProcessRates             processRates;
	
	private final FarmerBlockMap   blockMap;
	private final List<FarmerTask> tasks;
	
	private final IsolatedListener<BlockEvent.FarmlandTrampleEvent> listenerFarmlandTrample;
	private final IsolatedListener<FarmlandLoseMoistureEvent>       listenerFarmlandLoseMoisture;
	private final IsolatedListener<TreeGrowthEvent>                 listenerTreeGrowth;
	
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
		
		this.blockMap = new FarmerBlockMap();
		this.tasks = Stream.of(FarmerTaskType.values())
				.filter(processRates::contains)
				.map((task) -> task.create(inventory, blockMap, plantableStacks, processRates.maxOperations(task), processRates.interval(task)))
				.toList();
		
		this.listenerFarmlandTrample = (event) ->
		{
			if(tilling && blockMap.containsDirtAt(event.getPos()))
			{
				event.setCanceled(true);
			}
		};
		this.listenerFarmlandLoseMoisture = (event) ->
		{
			if(tilling && blockMap.containsDirtAt(event.getPos()) && consumeWater(inventory, Simulation.SIMULATE))
			{
				consumeWater(inventory, Simulation.ACT);
				event.setCanceled(true);
			}
		};
		this.listenerTreeGrowth = (event) ->
		{
			if(blockMap.containsDirtAt(event.getPos().below()))
			{
				blockMap.addTree(event.getPos(), event.getPositions());
			}
		};
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
		this.level = level;
		this.shapeMatcher = shapeMatcher;
		IsolatedListeners.register(level, shapeMatcher.getSpannedChunks(), BlockEvent.FarmlandTrampleEvent.class, listenerFarmlandTrample);
		IsolatedListeners.register(level, shapeMatcher.getSpannedChunks(), FarmlandLoseMoistureEvent.class, listenerFarmlandLoseMoisture);
		IsolatedListeners.register(level, shapeMatcher.getSpannedChunks(), TreeGrowthEvent.class, listenerTreeGrowth);
	}
	
	public void unregisterListeners(Level level, ShapeMatcher shapeMatcher)
	{
		IsolatedListeners.unregister(level, shapeMatcher.getSpannedChunks(), BlockEvent.FarmlandTrampleEvent.class, listenerFarmlandTrample);
		IsolatedListeners.unregister(level, shapeMatcher.getSpannedChunks(), FarmlandLoseMoistureEvent.class, listenerFarmlandLoseMoisture);
		IsolatedListeners.unregister(level, shapeMatcher.getSpannedChunks(), TreeGrowthEvent.class, listenerTreeGrowth);
		this.shapeMatcher = null;
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
		tag.putBoolean("tilling", tilling);
		tag.putString("planting_mode", plantingMode.name());
		
		CompoundTag cache = new CompoundTag();
		CompoundTag trees = new CompoundTag();
		for(FarmerTree tree : blockMap.trees().values())
		{
			long[] list = tree.blocks().stream().mapToLong(BlockPos::asLong).toArray();
			trees.putLongArray(Long.toString(tree.base().asLong()), list);
		}
		cache.put("trees", trees);
		tag.put("cache", cache);
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
		
		CompoundTag cache = tag.getCompound("cache");
		CompoundTag trees = cache.getCompound("trees");
		for(String key : trees.getAllKeys())
		{
			BlockPos base = BlockPos.of(Long.parseLong(key));
			List<BlockPos> blocks = Arrays.stream(trees.getLongArray(key)).mapToObj(BlockPos::of).toList();
			blockMap.addTree(base, blocks);
		}
	}
}
