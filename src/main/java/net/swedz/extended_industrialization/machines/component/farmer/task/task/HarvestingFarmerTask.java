package net.swedz.extended_industrialization.machines.component.farmer.task.task;

import aztech.modern_industrialization.inventory.MIItemStorage;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.transaction.Transaction;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.entity.ai.village.poi.PoiSection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.common.extensions.IBlockStateExtension;
import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.datamap.EnchantmentModule;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.farmer.ElectricFarmerBlockEntity;
import net.swedz.extended_industrialization.machines.component.farmer.FarmerComponent;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerBlock;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerTile;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.HarvestableBehavior;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.HarvestableBehaviorHolder;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.HarvestingContext;
import net.swedz.extended_industrialization.machines.component.farmer.task.FarmerTask;
import net.swedz.extended_industrialization.machines.component.farmer.task.FarmerTaskType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class HarvestingFarmerTask extends FarmerTask
{
	private final HarvestableBehaviorHolder harvestingHandlers;
	
	private final Map<BlockPos, List<ItemStack>> cachedDrops = Maps.newHashMap();
	
	public HarvestingFarmerTask(FarmerComponent component)
	{
		super(FarmerTaskType.HARVESTING, component);
		harvestingHandlers = component.getHarvestableBehaviorHolder();
	}
	
	private boolean insertDrops(List<ItemStack> drops, boolean simulate)
	{
		try (Transaction transaction = Transaction.openOuter())
		{
			MIItemStorage itemOutput = new MIItemStorage(inventory.getItemOutputs());
			
			boolean success = true;
			for(ItemStack item : drops)
			{
				long inserted = itemOutput.insertAllSlot(ItemVariant.of(item), item.getCount(), transaction);
				if(inserted != item.getCount() &&
				   !item.is(EITags.Items.FARMER_VOIDABLE))
				{
					success = false;
					break;
				}
			}
			
			if(!simulate)
			{
				transaction.commit();
			}
			
			return success;
		}
	}
	
	private List<ItemStack> sortDrops(List<ItemStack> drops)
	{
		drops.sort(Comparator.comparing((item) -> item.is(EITags.Items.FARMER_VOIDABLE)));
		return drops;
	}
	
	private List<ItemStack> getDrops(HarvestingContext context, HarvestableBehavior handler)
	{
		BlockPos origin = context.pos();
		List<ItemStack> drops;
		if(cachedDrops.containsKey(origin))
		{
			drops = List.copyOf(cachedDrops.get(origin));
			if(!this.insertDrops(drops, true))
			{
				return List.of();
			}
			cachedDrops.remove(origin);
			return this.sortDrops(handler.getDrops(context));
		}
		else
		{
			drops = this.sortDrops(handler.getDrops(context));
			if(drops.isEmpty())
			{
				return drops;
			}
			if(!this.insertDrops(drops, true))
			{
				cachedDrops.put(origin, drops);
				return List.of();
			}
		}
		return drops;
	}
	
	private boolean harvestBlocks(FarmerBlock cropBlockEntry, HarvestingContext context, HarvestableBehavior handler)
	{
		BlockPos origin = context.pos();
		List<BlockPos> blockPositions = handler.getBlocks(context);
		
		if(blockPositions.isEmpty())
		{
			return false;
		}
		
		List<ItemStack> drops = this.getDrops(context, handler);
		if(drops.isEmpty())
		{
			return false;
		}
		
		this.insertDrops(drops, false);
		
		BlockState[] oldStates = new BlockState[blockPositions.size()];
		BlockState[] newStates = new BlockState[blockPositions.size()];
		BlockState newOriginState = Blocks.AIR.defaultBlockState();
		for(int index = 0; index < blockPositions.size(); index++)
		{
			var pos = blockPositions.get(index);
			oldStates[index] = level.getBlockState(pos);
			BlockState newState = level.getFluidState(pos).createLegacyBlock();
			newStates[index] = newState;
			if(pos.equals(origin))
			{
				newOriginState = newState;
			}
			level.setBlock(pos, newState, Block.UPDATE_NONE, 0);
		}
		for(int index = 0; index < blockPositions.size(); index++)
		{
			var pos = blockPositions.get(index);
			var oldState = oldStates[index];
			var newState = newStates[index];
			this.markAndNotifyBlockWithoutOnBlockStateChange(pos, level.getChunkAt(pos), oldState, newState, Block.UPDATE_ALL, Block.UPDATE_LIMIT);
		}
		cropBlockEntry.updateState(newOriginState);
		
		handler.harvested(context);
		
		return true;
	}
	
	/**
	 * This is a bit of a weird one so I think it warrants some explanation.
	 * <br><br>
	 * In the method linked below under "See Also", {@link Level#onBlockStateChange(BlockPos, BlockState, BlockState)}
	 * and {@link IBlockStateExtension#onBlockStateChange(LevelReader, BlockPos, BlockState)} are always called. On the
	 * server level (which this is always called on the server) that method causes POIs to get removed if the block it
	 * is set to is no longer the same type of POI. This is fine in most cases, but in our case we need to remove
	 * blocks and then go through and notify adjacent blocks about this change <i>after</i> all of the blocks have been
	 * removed. This is to avoid certain types of blocks (like vines or moss carpets) that may be included in the batch
	 * remove getting popped off instead of being harvested. That all being said, on the second iteration, we cannot
	 * just use the method linked below because it causes the POIs to get removed a second time resulting in log spam
	 * (for example: <code>POI data mismatch: never registered at ...</code>, see {@link PoiSection#remove(BlockPos)}
	 * for the source of this message).
	 * <br><br>
	 * That all being said, this is the same code as the method linked below without the aforementioned
	 * <code>onBlockStateChange</code> calls.
	 *
	 * @see Level#markAndNotifyBlock(BlockPos, LevelChunk, BlockState, BlockState, int, int)
	 */
	private void markAndNotifyBlockWithoutOnBlockStateChange(BlockPos pos, LevelChunk chunk, BlockState oldState, BlockState newState, int updateFlag, int updateLimit)
	{
		Block block = newState.getBlock();
		BlockState currentState = level.getBlockState(pos);
		if(currentState == newState)
		{
			if(oldState != currentState)
			{
				level.setBlocksDirty(pos, oldState, currentState);
			}
			
			if((updateFlag & 2) != 0
			   && (!level.isClientSide() || (updateFlag & 4) == 0)
			   && (level.isClientSide() || chunk.getFullStatus() != null && chunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING)))
			{
				level.sendBlockUpdated(pos, oldState, newState, updateFlag);
			}
			
			if((updateFlag & 1) != 0)
			{
				level.blockUpdated(pos, oldState.getBlock());
				if(!level.isClientSide() && newState.hasAnalogOutputSignal())
				{
					level.updateNeighbourForOutputSignal(pos, block);
				}
			}
			
			if((updateFlag & 16) == 0 && updateLimit > 0)
			{
				int flags = updateFlag & -34;
				oldState.updateIndirectNeighbourShapes(level, pos, flags, updateLimit - 1);
				newState.updateNeighbourShapes(level, pos, flags, updateLimit - 1);
				newState.updateIndirectNeighbourShapes(level, pos, flags, updateLimit - 1);
			}
		}
	}
	
	private Optional<EnchantmentModule> getActiveEnchantment()
	{
		return farmer.getMachine() instanceof ElectricFarmerBlockEntity electric ?
				electric.getEnchantmentModuleComponent().getActiveEnchantment() :
				Optional.empty();
	}
	
	@Override
	protected boolean run()
	{
		for(FarmerTile tile : blockMap)
		{
			FarmerBlock crop = tile.crop();
			BlockPos pos = crop.pos();
			BlockState state = crop.state(level);
			
			HarvestingContext context = new HarvestingContext(level, pos, state, this.getActiveEnchantment(), farmer.getMachine().getHighestCableTier());
			Optional<HarvestableBehavior> handlerOptional = harvestingHandlers.behavior(context);
			
			if(handlerOptional.isPresent())
			{
				HarvestableBehavior handler = handlerOptional.get();
				if(handler.isFullyGrown(context) && this.harvestBlocks(crop, context, handler) && operations.operate())
				{
					return true;
				}
			}
		}
		
		return operations.didOperate();
	}
}
