package net.swedz.extended_industrialization.machines.component.farmer.task.task;

import aztech.modern_industrialization.inventory.MIItemStorage;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.transaction.Transaction;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
			level.markAndNotifyBlock(pos, level.getChunkAt(pos), oldState, newState, Block.UPDATE_ALL, Block.UPDATE_LIMIT);
		}
		cropBlockEntry.updateState(newOriginState);
		
		handler.harvested(context);
		
		return true;
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
