package net.swedz.extended_industrialization.machines.components.farmer.task.tasks;

import aztech.modern_industrialization.inventory.MIItemStorage;
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.machines.components.farmer.FarmerComponentPlantableStacks;
import net.swedz.extended_industrialization.machines.components.farmer.block.FarmerBlock;
import net.swedz.extended_industrialization.machines.components.farmer.block.FarmerBlockMap;
import net.swedz.extended_industrialization.machines.components.farmer.block.FarmerTile;
import net.swedz.extended_industrialization.machines.components.farmer.block.FarmerTree;
import net.swedz.extended_industrialization.machines.components.farmer.task.FarmerTask;
import net.swedz.extended_industrialization.machines.components.farmer.task.FarmerTaskType;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public final class HarvestingFarmerTask extends FarmerTask
{
	public HarvestingFarmerTask(MultiblockInventoryComponent inventory, FarmerBlockMap blockMap, FarmerComponentPlantableStacks plantableStacks, int maxOperations, int processInterval)
	{
		super(FarmerTaskType.HARVESTING, inventory, blockMap, plantableStacks, maxOperations, processInterval);
	}
	
	private List<ItemStack> getHarvestItems(BlockPos pos, BlockState state)
	{
		ResourceLocation lootTableId = state.getBlock().getLootTable();
		LootTable lootTable = level.getServer().getLootData().getLootTable(lootTableId);
		LootParams lootParams = new LootParams.Builder((ServerLevel) level)
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
				.withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
				.withParameter(LootContextParams.BLOCK_STATE, state)
				.create(LootContextParamSets.BLOCK);
		return lootTable.getRandomItems(lootParams);
	}
	
	private List<ItemStack> getHarvestItems(List<BlockPos> blockPositions, List<BlockState> blockStates)
	{
		List<ItemStack> items = Lists.newArrayList();
		for(int i = 0; i < blockPositions.size(); i++)
		{
			items.addAll(this.getHarvestItems(blockPositions.get(i), blockStates.get(i)));
		}
		return items;
	}
	
	private boolean harvestBlocks(FarmerBlock cropBlockEntry, List<BlockPos> blockPositions, List<BlockState> blockStates)
	{
		try (Transaction transaction = Transaction.openOuter())
		{
			List<ItemStack> items = this.getHarvestItems(blockPositions, blockStates);
			
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
				level.gameEvent(GameEvent.BLOCK_DESTROY, blockPosition, GameEvent.Context.of(blockStates.get(i)));
				i++;
			}
			cropBlockEntry.updateState(newState);
			
			transaction.commit();
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
			
			if(state.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(state))
			{
				if(this.harvestBlocks(crop, List.of(pos), List.of(state)))
				{
					if(operations.operate())
					{
						return true;
					}
				}
			}
			
			else if(blockMap.containsTree(crop))
			{
				FarmerTree tree = blockMap.popTree(crop);
				if(this.harvestBlocks(crop, tree.blocks(), tree.blockStates(level)))
				{
					if(operations.operate())
					{
						return true;
					}
				}
			}
		}
		
		return operations.didOperate();
	}
}
