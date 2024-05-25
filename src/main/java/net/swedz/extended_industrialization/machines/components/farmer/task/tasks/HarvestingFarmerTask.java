package net.swedz.extended_industrialization.machines.components.farmer.task.tasks;

import aztech.modern_industrialization.inventory.MIItemStorage;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.swedz.extended_industrialization.machines.components.farmer.FarmerComponent;
import net.swedz.extended_industrialization.machines.components.farmer.block.FarmerBlock;
import net.swedz.extended_industrialization.machines.components.farmer.block.FarmerTile;
import net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.HarvestingContext;
import net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.HarvestingHandler;
import net.swedz.extended_industrialization.machines.components.farmer.harvestinghandler.registry.FarmerHarvestingHandlersHolder;
import net.swedz.extended_industrialization.machines.components.farmer.task.FarmerTask;
import net.swedz.extended_industrialization.machines.components.farmer.task.FarmerTaskType;

import java.util.List;
import java.util.Optional;

public final class HarvestingFarmerTask extends FarmerTask
{
	private final FarmerHarvestingHandlersHolder harvestingHandlersHolder;
	
	public HarvestingFarmerTask(FarmerComponent component)
	{
		super(FarmerTaskType.HARVESTING, component);
		harvestingHandlersHolder = component.getHarvestingHandlersHolder();
	}
	
	private boolean harvestBlocks(FarmerBlock cropBlockEntry, HarvestingContext context, HarvestingHandler handler)
	{
		List<BlockPos> blockPositions = handler.getBlocks(context);
		
		if(blockPositions.size() == 0)
		{
			return false;
		}
		
		try (Transaction transaction = Transaction.openOuter())
		{
			List<ItemStack> items = handler.getDrops(context);
			
			if(items.size() == 0)
			{
				return false;
			}
			
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
				return false;
			}
			
			BlockState newState = Blocks.AIR.defaultBlockState();
			int i = 0;
			for(BlockPos blockPosition : blockPositions)
			{
				level.setBlock(blockPosition, newState, 1 | 2);
				level.gameEvent(GameEvent.BLOCK_DESTROY, blockPosition, GameEvent.Context.of(level.getBlockState(blockPosition)));
				i++;
			}
			cropBlockEntry.updateState(newState);
			
			transaction.commit();
			
			handler.harvested(context);
		}
		
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
			Optional<HarvestingHandler> handlerOptional = harvestingHandlersHolder.getHandler(context);
			
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
