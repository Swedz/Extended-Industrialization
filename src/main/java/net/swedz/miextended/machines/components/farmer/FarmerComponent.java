package net.swedz.miextended.machines.components.farmer;

import aztech.modern_industrialization.inventory.MIItemStorage;
import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.transaction.Transaction;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.IPlantable;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.api.MachineInventoryHelper;
import net.swedz.miextended.api.event.FarmlandLoseMoistureEvent;
import net.swedz.miextended.api.isolatedlistener.IsolatedListener;
import net.swedz.miextended.api.isolatedlistener.IsolatedListeners;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public final class FarmerComponent implements IComponent
{
	private final MultiblockInventoryComponent inventory;
	private final IsActiveComponent            isActive;
	private final PlantingMode                 plantingMode;
	
	private final IsolatedListener<FarmlandLoseMoistureEvent>      listenerFarmlandLoseMoisture;
	
	private final List<BlockPos> treePositions = Lists.newArrayList();
	
	public boolean tilling;
	
	private Level        level;
	private ShapeMatcher shapeMatcher;
	
	private List<BlockPos> dirtPositions = List.of();
	
	private final FarmerComponentPlantableStacks plantableStacks;
	
	private int          processTick;
	private FarmerBlocks dirtBlocks;
	private FarmerBlocks cropBlocks;
	private boolean      hasWater;
	
	public FarmerComponent(MultiblockInventoryComponent inventory, IsActiveComponent isActive, PlantingMode plantingMode)
	{
		this.inventory = inventory;
		this.isActive = isActive;
		this.plantableStacks = new FarmerComponentPlantableStacks(this);
		this.plantingMode = plantingMode;
		
		this.listenerFarmlandLoseMoisture = (event) ->
		{
			if(isActive.isActive && this.consumeWater(Simulation.SIMULATE))
			{
				this.consumeWater(Simulation.ACT);
				event.setCanceled(true);
			}
		};
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
	private boolean till()
	{
		if(!tilling || !hasWater)
		{
			return false;
		}
		
		for(FarmerBlock dirtBlockEntry : dirtBlocks)
		{
			BlockPos pos = dirtBlockEntry.pos();
			BlockState state = dirtBlockEntry.state();
			if(state.is(BlockTags.DIRT))
			{
				BlockState newState = Blocks.FARMLAND.defaultBlockState();
				if(Blocks.FARMLAND.canSurvive(newState, level, pos))
				{
					level.setBlock(pos, newState, 1 | 2 | 8);
					level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
					dirtBlockEntry.updateState(newState);
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean wetten()
	{
		if(!hasWater)
		{
			return false;
		}
		
		for(FarmerBlock dirtBlockEntry : dirtBlocks)
		{
			BlockPos pos = dirtBlockEntry.pos();
			BlockState state = dirtBlockEntry.state();
			if(state.getBlock() instanceof FarmBlock)
			{
				int moisture = state.getValue(FarmBlock.MOISTURE);
				if(moisture < 7 && this.consumeWater(Simulation.ACT))
				{
					BlockState newState = state.setValue(FarmBlock.MOISTURE, 7);
					level.setBlock(pos, newState, 2);
					dirtBlockEntry.updateState(newState);
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean fertilize()
	{
		// TODO
		return false;
	}
	
	private boolean harvest()
	{
		if(processTick != 20)
		{
			return false;
		}
		
		for(FarmerBlock cropBlockEntry : cropBlocks)
		{
			BlockPos pos = cropBlockEntry.pos();
			BlockState state = cropBlockEntry.state();
			
			if(state.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(state))
			{
				ResourceLocation lootTableId = state.getBlock().getLootTable();
				LootTable lootTable = level.getServer().getLootData().getLootTable(lootTableId);
				LootParams lootParams = new LootParams.Builder((ServerLevel) level)
						.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
						.withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
						.withParameter(LootContextParams.BLOCK_STATE, state)
						.create(LootContextParamSets.BLOCK);
				List<ItemStack> items = lootTable.getRandomItems(lootParams);
				
				if(items.size() == 0)
				{
					continue;
				}
				
				try (Transaction transaction = Transaction.openOuter())
				{
					MIItemStorage itemOutput = new MIItemStorage(inventory.getItemOutputs());
					
					boolean success = true;
					for(ItemStack item : items)
					{
						long inserted = itemOutput.insertAllSlot(ItemVariant.of(item), item.getCount(), transaction);
						if(inserted != item.getCount())
						{
							success = false;
							break;
						}
					}
					if(!success)
					{
						continue;
					}
					
					BlockState newState = Blocks.AIR.defaultBlockState();
					level.setBlock(pos, newState, 1 | 2);
					level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));
					cropBlockEntry.updateState(newState);
					
					transaction.commit();
				}
				
				return true;
			}
			
			else if(treePositions.contains(pos))
			{
				MIExtended.LOGGER.info("we can harvest a tree");
			}
		}
		
		return false;
	}
	
	private boolean plant()
	{
		if(processTick != 20)
		{
			return false;
		}
		
		List<PlantableConfigurableItemStack> plantables = plantableStacks.getItems();
		plantables.removeIf((plantable) -> !plantable.isPlantable() || (!plantingMode.includeEmptyStacks() && plantable.getStack().isEmpty()));
		
		if(plantables.size() == 0)
		{
			return false;
		}
		
		int blockIndex = 0;
		for(FarmerBlock dirtBlockEntry : dirtBlocks)
		{
			int index = plantingMode.index(dirtBlockEntry, plantables);
			PlantableConfigurableItemStack plantable = plantables.get(index);
			if(plantable.canBePlantedOn(dirtBlockEntry) && !plantable.getStack().isEmpty())
			{
				FarmerBlock cropBlockEntry = cropBlocks.get(blockIndex);
				BlockPos pos = cropBlockEntry.pos();
				BlockState state = cropBlockEntry.state();
				if(state.isAir())
				{
					BlockState plantState = plantable.getPlant(pos);
					
					plantable.getStack().decrement(1);
					
					level.setBlock(pos, plantState, 1 | 2);
					level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(plantState));
					cropBlockEntry.updateState(plantState);
					
					return true;
				}
			}
			blockIndex++;
		}
		
		return false;
	}
	
	public void tick()
	{
		if(level == null)
		{
			return;
		}
		
		dirtBlocks = new FarmerBlocks();
		cropBlocks = new FarmerBlocks();
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
		
		hasWater = this.consumeWater(Simulation.SIMULATE);
		
		this.till();
		this.wetten();
		this.fertilize();
		this.harvest();
		this.plant();
		
		processTick++;
		if(processTick > 20)
		{
			processTick = 0;
		}
	}
	
	public void registerListeners(Level level, ShapeMatcher shapeMatcher)
	{
		this.level = level;
		this.shapeMatcher = shapeMatcher;
		IsolatedListeners.register(level, shapeMatcher.getSpannedChunks(), FarmlandLoseMoistureEvent.class, listenerFarmlandLoseMoisture);
	}
	
	public void unregisterListeners(Level level, ShapeMatcher shapeMatcher)
	{
		IsolatedListeners.unregister(level, shapeMatcher.getSpannedChunks(), FarmlandLoseMoistureEvent.class, listenerFarmlandLoseMoisture);
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
	
	private record FarmerTick(FarmerBlocks dirtBlocks, FarmerBlocks cropBlocks, boolean hasWater)
	{
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
		private final int      line;
		private final BlockPos pos;
		
		private BlockState state;
		
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
		
		public void updateState(BlockState state)
		{
			this.state = state;
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
