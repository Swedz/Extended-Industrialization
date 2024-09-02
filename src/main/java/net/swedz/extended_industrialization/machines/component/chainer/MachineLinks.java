package net.swedz.extended_industrialization.machines.component.chainer;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public final class MachineLinks implements ClearableInvalidatable
{
	private final Supplier<Level>     level;
	private final BlockPos            origin;
	private final Supplier<Direction> direction;
	private final int                 maxConnections;
	
	private List<BlockPos> positions = List.of();
	
	private List<IItemHandler>  itemHandlers  = List.of();
	private List<IFluidHandler> fluidHandlers = List.of();
	
	public MachineLinks(Supplier<Level> level, BlockPos origin, Supplier<Direction> direction, int maxConnections)
	{
		this.level = level;
		this.origin = origin;
		this.direction = direction;
		this.maxConnections = maxConnections;
	}
	
	public Level level()
	{
		return level.get();
	}
	
	public BlockPos origin()
	{
		return origin;
	}
	
	public Direction direction()
	{
		return direction.get();
	}
	
	public List<BlockPos> positions()
	{
		return positions;
	}
	
	public int count()
	{
		return positions.size();
	}
	
	public boolean contains(BlockPos pos, boolean recursive)
	{
		if(positions.contains(pos))
		{
			return true;
		}
		if(recursive)
		{
			for(BlockPos link : positions)
			{
				BlockEntity blockEntity = this.level().getBlockEntity(link);
				if(blockEntity instanceof MachineChainerMachineBlockEntity chainerBlockEntity &&
				   chainerBlockEntity.getChainerComponent().links().contains(pos, true))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean contains(BlockPos pos)
	{
		return this.contains(pos, false);
	}
	
	public boolean isJustOutside(BlockPos pos)
	{
		return pos.equals(origin.relative(this.direction(), this.count() + 1));
	}
	
	public int maxConnections()
	{
		return maxConnections;
	}
	
	List<BlockPos> getSpannedBlocks()
	{
		List<BlockPos> blocks = Lists.newArrayList();
		for(int i = 1; i <= maxConnections; i++)
		{
			blocks.add(origin.relative(this.direction(), i));
		}
		return Collections.unmodifiableList(blocks);
	}
	
	Set<ChunkPos> getSpannedChunks()
	{
		Set<ChunkPos> chunks = Sets.newHashSet();
		for(BlockPos block : this.getSpannedBlocks())
		{
			chunks.add(new ChunkPos(block));
		}
		return Collections.unmodifiableSet(chunks);
	}
	
	public List<IItemHandler> itemHandlers()
	{
		return itemHandlers;
	}
	
	public List<IFluidHandler> fluidHandlers()
	{
		return fluidHandlers;
	}
	
	@Override
	public void clear()
	{
		positions = List.of();
		itemHandlers = List.of();
		fluidHandlers = List.of();
	}
	
	@Override
	public void invalidate()
	{
		List<BlockPos> machinesFound = Lists.newArrayList();
		List<IItemHandler> itemHandlers = Lists.newArrayList();
		List<IFluidHandler> fluidHandlers = Lists.newArrayList();
		
		for(BlockPos pos : this.getSpannedBlocks())
		{
			if(this.level().getBlockState(pos).is(EITags.MACHINE_CHAINER_RELAY))
			{
				machinesFound.add(pos);
				continue;
			}
			BlockEntity blockEntity = this.level().getBlockEntity(pos);
			if(!(blockEntity instanceof MachineBlockEntity machineBlockEntity))
			{
				break;
			}
			if(blockEntity instanceof MachineChainerMachineBlockEntity)
			{
				// TODO allow chaining chainers again
				break;
			}
			
			boolean isMachine = false;
			
			IItemHandler itemHandler = this.level().getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
			if(itemHandler != null)
			{
				itemHandlers.add(itemHandler);
				isMachine = true;
			}
			
			IFluidHandler fluidHandler = this.level().getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
			if(fluidHandler != null)
			{
				fluidHandlers.add(fluidHandler);
				isMachine = true;
			}
			
			// TODO energy
			
			if(!isMachine)
			{
				break;
			}
			
			machinesFound.add(pos);
		}
		
		this.positions = Collections.unmodifiableList(machinesFound);
		this.itemHandlers = Collections.unmodifiableList(itemHandlers);
		this.fluidHandlers = Collections.unmodifiableList(fluidHandlers);
	}
}
