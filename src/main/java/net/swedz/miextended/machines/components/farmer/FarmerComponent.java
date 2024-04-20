package net.swedz.miextended.machines.components.farmer;

import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.IPlantable;
import net.swedz.miextended.api.MachineInventoryHelper;
import net.swedz.miextended.api.event.FarmlandLoseMoistureEvent;
import net.swedz.miextended.api.isolatedlistener.IsolatedListener;
import net.swedz.miextended.api.isolatedlistener.IsolatedListeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public final class FarmerComponent implements IComponent, IsolatedListener<FarmlandLoseMoistureEvent>
{
	private final MultiblockInventoryComponent inventory;
	private final IsActiveComponent            isActive;
	private final PlantingMode                 plantingMode;
	
	public boolean tilling;
	
	private Level        level;
	private ShapeMatcher shapeMatcher;
	
	private List<BlockPos> dirtPositions = List.of();
	
	private final FarmerComponentPlantableStacks plantableStacks;
	
	private int processTick;
	
	public FarmerComponent(MultiblockInventoryComponent inventory, IsActiveComponent isActive, PlantingMode plantingMode)
	{
		this.inventory = inventory;
		this.isActive = isActive;
		this.plantableStacks = new FarmerComponentPlantableStacks(this);
		this.plantingMode = plantingMode;
	}
	
	public void fromOffsets(BlockPos controllerPos, Direction controllerDirection, List<BlockPos> offsets)
	{
		List<BlockPos> dirtPositions = new ArrayList<>(offsets.size());
		for(BlockPos offset : offsets)
		{
			BlockPos worldPos = ShapeMatcher.toWorldPos(controllerPos, controllerDirection, offset);
			dirtPositions.add(worldPos);
		}
		this.dirtPositions = Collections.unmodifiableList(dirtPositions);
	}
	
	public void updateStackListeners()
	{
		plantableStacks.update(inventory.getItemInputs());
	}
	
	private boolean consumeWater(Simulation simulation)
	{
		return MachineInventoryHelper.consumeFluid(inventory.getFluidInputs(), Fluids.WATER, 50, simulation) == 50;
	}
	
	@SuppressWarnings("deprecation")
	private boolean till(FarmerBlocks dirtBlocks, FarmerBlocks cropBlocks)
	{
		if(!tilling)
		{
			return false;
		}
		
		for(FarmerBlock blockEntry : dirtBlocks)
		{
			BlockPos pos = blockEntry.pos();
			BlockState state = blockEntry.state();
			if(state.is(BlockTags.DIRT))
			{
				BlockState newState = Blocks.FARMLAND.defaultBlockState();
				if(Blocks.FARMLAND.canSurvive(newState, level, pos))
				{
					level.setBlock(pos, newState, 1 | 2 | 8);
					level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean wetten(FarmerBlocks dirtBlocks, FarmerBlocks cropBlocks)
	{
		if(this.consumeWater(Simulation.SIMULATE))
		{
			for(FarmerBlock blockEntry : dirtBlocks)
			{
				BlockPos pos = blockEntry.pos();
				BlockState state = blockEntry.state();
				if(state.getBlock() instanceof FarmBlock)
				{
					int moisture = state.getValue(FarmBlock.MOISTURE);
					if(moisture < 7 && this.consumeWater(Simulation.ACT))
					{
						level.setBlock(pos, state.setValue(FarmBlock.MOISTURE, 7), 2);
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private boolean fertilize(FarmerBlocks dirtBlocks, FarmerBlocks cropBlocks)
	{
		// TODO
		return false;
	}
	
	private boolean harvest(FarmerBlocks dirtBlocks, FarmerBlocks cropBlocks)
	{
		// TODO
		return false;
	}
	
	private boolean plant(FarmerBlocks dirtBlocks, FarmerBlocks cropBlocks)
	{
		List<PlantableConfigurableItemStack> plantables = plantableStacks.getItems();
		plantables.removeIf((plantable) -> !plantable.isPlantable() || (!plantingMode.includeEmptyStacks() && plantable.getStack().isEmpty()));
		
		if(plantables.size() == 0)
		{
			return false;
		}
		
		for(FarmerBlock blockEntry : dirtBlocks)
		{
			int index = plantingMode.index(blockEntry, plantables);
			PlantableConfigurableItemStack plantable = plantables.get(index);
			if(plantable.canBePlantedOn(blockEntry) && !plantable.getStack().isEmpty())
			{
				BlockPos pos = blockEntry.pos().above();
				BlockState state = level.getBlockState(pos);
				if(state.isAir())
				{
					BlockState plantState = plantable.getPlant(pos);
					
					plantable.getStack().decrement(1);
					
					level.setBlock(pos, plantState, 1 | 2);
					level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(plantState));
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void run()
	{
		if(level == null)
		{
			return;
		}
		
		FarmerBlocks dirtBlocks = new FarmerBlocks();
		FarmerBlocks cropBlocks = new FarmerBlocks();
		int line = 0;
		Integer lastX = null;
		for(BlockPos pos : dirtPositions)
		{
			if(lastX != null && lastX != pos.getX())
			{
				line++;
			}
			dirtBlocks.put(line, pos);
			cropBlocks.put(line, pos.above());
			lastX = pos.getX();
		}
		
		this.till(dirtBlocks, cropBlocks);
		this.wetten(dirtBlocks, cropBlocks);
		this.fertilize(dirtBlocks, cropBlocks);
		this.harvest(dirtBlocks, cropBlocks);
		this.plant(dirtBlocks, cropBlocks);
		
		processTick++;
		if(processTick > 20)
		{
			processTick = 0;
		}
	}
	
	@Override
	public void on(FarmlandLoseMoistureEvent event)
	{
		if(isActive.isActive && this.consumeWater(Simulation.SIMULATE))
		{
			this.consumeWater(Simulation.ACT);
			event.setCanceled(true);
		}
	}
	
	public void registerListeners(Level level, ShapeMatcher shapeMatcher)
	{
		this.level = level;
		this.shapeMatcher = shapeMatcher;
		IsolatedListeners.register(level, shapeMatcher.getSpannedChunks(), FarmlandLoseMoistureEvent.class, this);
	}
	
	public void unregisterListeners(Level level, ShapeMatcher shapeMatcher)
	{
		IsolatedListeners.unregister(level, shapeMatcher.getSpannedChunks(), FarmlandLoseMoistureEvent.class, this);
		this.shapeMatcher = null;
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
		tag.putBoolean("tilling", tilling);
	}
	
	@Override
	public void readNbt(CompoundTag tag, boolean isUpgradingMachine)
	{
		tilling = tag.getBoolean("tilling");
	}
	
	public MultiblockInventoryComponent getInventory()
	{
		return inventory;
	}
	
	public boolean isActive()
	{
		return isActive.isActive;
	}
	
	public Level getLevel()
	{
		return level;
	}
	
	private final class FarmerBlocks extends ArrayList<FarmerBlock>
	{
		public void put(int line, BlockPos pos)
		{
			this.add(new FarmerBlock(line, pos, level.getBlockState(pos)));
		}
	}
	
	public final class FarmerBlock implements Comparable<FarmerBlock>
	{
		private final int        line;
		private final BlockPos   pos;
		private final BlockState state;
		
		private FarmerBlock(int line, BlockPos pos, BlockState state)
		{
			this.line = line;
			this.pos = pos;
			this.state = state;
		}
		
		public int line()
		{
			return line;
		}
		
		public BlockPos pos()
		{
			return pos;
		}
		
		public BlockState state()
		{
			return state;
		}
		
		public boolean canBePlantedOnBy(IPlantable plantable)
		{
			return state.canSustainPlant(level, pos, Direction.UP, plantable);
		}
		
		@Override
		public int compareTo(FarmerBlock other)
		{
			return pos.compareTo(other.pos());
		}
	}
	
	public enum PlantingMode
	{
		ALTERNATING_LINES(true, (block, plantables) -> block.line() % plantables.size()),
		AS_NEEDED(false, (block, plantables) -> 0);
		
		private final boolean includeEmptyStacks;
		
		private final BiFunction<FarmerBlock, List<PlantableConfigurableItemStack>, Integer> index;
		
		PlantingMode(boolean includeEmptyStacks, BiFunction<FarmerBlock, List<PlantableConfigurableItemStack>, Integer> index)
		{
			this.includeEmptyStacks = includeEmptyStacks;
			this.index = index;
		}
		
		public boolean includeEmptyStacks()
		{
			return includeEmptyStacks;
		}
		
		public int index(FarmerBlock block, List<PlantableConfigurableItemStack> plantables)
		{
			return index.apply(block, plantables);
		}
	}
}
