package net.swedz.extended_industrialization.machines.component.farmer.task.tasks;

import aztech.modern_industrialization.inventory.MIItemStorage;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.transaction.Transaction;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.swedz.extended_industrialization.machines.component.farmer.FarmerComponent;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerBlock;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerTile;
import net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler.HarvestingContext;
import net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler.HarvestingHandler;
import net.swedz.extended_industrialization.machines.component.farmer.harvestinghandler.registry.FarmerHarvestingHandlersHolder;
import net.swedz.extended_industrialization.machines.component.farmer.task.FarmerTask;
import net.swedz.extended_industrialization.machines.component.farmer.task.FarmerTaskType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class HarvestingFarmerTask extends FarmerTask
{
	private final FarmerHarvestingHandlersHolder harvestingHandlers;
	
	private final Map<BlockPos, List<ItemStack>> cachedDrops = Maps.newHashMap();
	
	public HarvestingFarmerTask(FarmerComponent component)
	{
		super(FarmerTaskType.HARVESTING, component);
		harvestingHandlers = component.getHarvestingHandlersHolder();
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
				if(inserted != item.getCount())
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
	
	private List<ItemStack> getDrops(HarvestingContext context, HarvestingHandler handler)
	{
		BlockPos origin = context.pos();
		List<ItemStack> drops;
		if(cachedDrops.containsKey(origin))
		{
			drops = cachedDrops.get(origin);
			if(!this.insertDrops(drops, true))
			{
				return List.of();
			}
			cachedDrops.remove(origin);
			return handler.getDrops(context);
		}
		else
		{
			drops = handler.getDrops(context);
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
	
	private boolean harvestBlocks(FarmerBlock cropBlockEntry, HarvestingContext context, HarvestingHandler handler)
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
		
		BlockState newState = Blocks.AIR.defaultBlockState();
		int i = 0;
		for(BlockPos blockPosition : blockPositions)
		{
			level.setBlock(blockPosition, newState, 1 | 2);
			level.gameEvent(GameEvent.BLOCK_DESTROY, blockPosition, GameEvent.Context.of(level.getBlockState(blockPosition)));
			i++;
		}
		cropBlockEntry.updateState(newState);
		
		handler.harvested(context);
		
		return true;
	}
	
	@Override
	protected boolean run()
	{
		for(FarmerTile tile : blockMap)
		{
			FarmerBlock crop = tile.crop();
			BlockPos pos = crop.pos();
			BlockState state = crop.state(level);
			
			HarvestingContext context = new HarvestingContext(level, pos, state);
			Optional<HarvestingHandler> handlerOptional = harvestingHandlers.getHandler(context);
			
			if(handlerOptional.isPresent())
			{
				HarvestingHandler handler = handlerOptional.get();
				if(handler.isFullyGrown(context) && this.harvestBlocks(crop, context, handler) && operations.operate())
				{
					return true;
				}
			}
		}
		
		return operations.didOperate();
	}
}
