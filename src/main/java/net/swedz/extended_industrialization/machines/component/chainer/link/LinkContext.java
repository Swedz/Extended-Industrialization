package net.swedz.extended_industrialization.machines.component.chainer.link;

import aztech.modern_industrialization.machines.MachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerLinks;

import java.util.Optional;

public final class LinkContext
{
	private final ChainerLinks          links;
	private final BlockPos              pos;
	private final Optional<BlockState>  blockState;
	private final Optional<BlockEntity> blockEntity;
	private final Optional<ItemStack>   itemStack;
	
	private LinkContext(
			ChainerLinks links,
			BlockPos pos,
			Optional<BlockState> blockState,
			Optional<BlockEntity> blockEntity,
			Optional<ItemStack> itemStack
	)
	{
		this.links = links;
		this.pos = pos;
		this.blockState = blockState;
		this.blockEntity = blockEntity;
		this.itemStack = itemStack;
	}
	
	public static LinkContext of(ChainerLinks links, BlockPos pos)
	{
		return new LinkContext(
				links,
				pos,
				Optional.of(links.level().getBlockState(pos)),
				Optional.ofNullable(links.level().getBlockEntity(pos)),
				Optional.empty()
		);
	}
	
	public static LinkContext of(ChainerLinks links, ItemStack itemStack)
	{
		LinkContext context;
		if(itemStack.getItem() instanceof BlockItem blockItem)
		{
			Block block = blockItem.getBlock();
			BlockEntity blockEntity = null;
			if(block instanceof MachineBlock machineBlock)
			{
				blockEntity = machineBlock.getBlockEntityInstance();
			}
			context = new LinkContext(
					links,
					BlockPos.ZERO,
					Optional.of(block.defaultBlockState()),
					Optional.ofNullable(blockEntity),
					Optional.of(itemStack)
			);
		}
		else
		{
			context = new LinkContext(
					links,
					BlockPos.ZERO,
					Optional.empty(),
					Optional.empty(),
					Optional.of(itemStack)
			);
		}
		return context;
	}
	
	public ChainerLinks links()
	{
		return links;
	}
	
	public Level level()
	{
		return links.level();
	}
	
	public BlockPos pos()
	{
		return pos;
	}
	
	public BlockState blockState()
	{
		return blockState.orElseThrow();
	}
	
	public boolean hasBlockState()
	{
		return blockState.isPresent();
	}
	
	public BlockEntity blockEntity()
	{
		return blockEntity.orElseThrow();
	}
	
	public boolean hasBlockEntity()
	{
		return blockEntity.isPresent();
	}
	
	public ItemStack itemStack()
	{
		return itemStack.orElseThrow();
	}
	
	public boolean hasItemStack()
	{
		return itemStack.isPresent();
	}
}
