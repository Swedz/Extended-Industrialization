package net.swedz.extended_industrialization.machines.component.chainer;

import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public final class MachineLinks implements ChainerElement
{
	private final Supplier<Level>     level;
	private final BlockPos            origin;
	private final Supplier<Direction> direction;
	private final int                 maxConnections;
	
	private List<BlockPos> positions = List.of();
	private int            linkCount;
	
	private List<IItemHandler>    itemHandlers   = List.of();
	private List<IFluidHandler>   fluidHandlers  = List.of();
	private List<MIEnergyStorage> energyHandlers = List.of();
	
	public MachineLinks(MachineChainerMachineBlockEntity machine, int maxConnections)
	{
		this.level = machine::getLevel;
		this.origin = machine.getBlockPos();
		this.direction = () -> machine.orientation.facingDirection;
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
		return linkCount;
	}
	
	public void count(int count)
	{
		this.linkCount = count;
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
	
	public BlockPos getJustOutside()
	{
		return origin.relative(this.direction(), this.count() + 1);
	}
	
	public boolean isJustOutside(BlockPos pos)
	{
		return pos.equals(this.getJustOutside());
	}
	
	public int maxConnections()
	{
		return maxConnections;
	}
	
	List<BlockPos> getSpannedBlocks(boolean includeOrigin)
	{
		List<BlockPos> blocks = Lists.newArrayList();
		if(includeOrigin)
		{
			blocks.add(origin);
		}
		for(int i = 1; i <= maxConnections; i++)
		{
			blocks.add(origin.relative(this.direction(), i));
		}
		return Collections.unmodifiableList(blocks);
	}
	
	Set<ChunkPos> getSpannedChunks(boolean includeOrigin)
	{
		Set<ChunkPos> chunks = Sets.newHashSet();
		for(BlockPos block : this.getSpannedBlocks(includeOrigin))
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
	
	public List<MIEnergyStorage> energyHandlers()
	{
		return energyHandlers;
	}
	
	@Override
	public void clear()
	{
		positions = List.of();
		linkCount = 0;
		itemHandlers = List.of();
		fluidHandlers = List.of();
		energyHandlers = List.of();
	}
	
	private Optional<TestResult> test(BlockPos pos, BlockState blockState, BlockEntity blockEntity)
	{
		IItemHandler itemHandler = this.level().getCapability(Capabilities.ItemHandler.BLOCK, pos, blockState, blockEntity, null);
		IFluidHandler fluidHandler = this.level().getCapability(Capabilities.FluidHandler.BLOCK, pos, blockState, blockEntity, null);
		MIEnergyStorage energyHandler = this.level().getCapability(EnergyApi.SIDED, pos, blockState, blockEntity, null);
		if(itemHandler != null || fluidHandler != null || energyHandler != null)
		{
			return Optional.of(TestResult.success(itemHandler, fluidHandler, energyHandler));
		}
		return Optional.empty();
	}
	
	public TestResult test(BlockPos pos)
	{
		BlockState blockState = this.level().getBlockState(pos);
		if(blockState.is(EITags.MACHINE_CHAINER_RELAY))
		{
			return TestResult.success();
		}
		
		BlockEntity blockEntity = this.level().getBlockEntity(pos);
		if(blockEntity instanceof MachineBlockEntity machineBlockEntity)
		{
			if(blockEntity instanceof MachineChainerMachineBlockEntity chainerBlockEntity)
			{
				if(chainerBlockEntity.orientation.facingDirection == this.direction() ||
				   chainerBlockEntity.orientation.facingDirection.getOpposite() == this.direction())
				{
					return TestResult.fail();
				}
				if(chainerBlockEntity.getChainerComponent().links().contains(origin, true))
				{
					return TestResult.fail();
				}
			}
			
			Optional<TestResult> result = this.test(pos, blockState, blockEntity);
			if(result.isPresent())
			{
				return result.get();
			}
		}
		
		return TestResult.fail();
	}
	
	public TestResult test(ItemStack stack)
	{
		if(stack.getItem() instanceof BlockItem blockItem)
		{
			Block block = blockItem.getBlock();
			if(block.defaultBlockState().is(EITags.MACHINE_CHAINER_RELAY))
			{
				return TestResult.success();
			}
			
			if(block instanceof MachineBlock machineBlock)
			{
				Optional<TestResult> result = this.test(BlockPos.ZERO, machineBlock.defaultBlockState(), machineBlock.getBlockEntityInstance());
				if(result.isPresent())
				{
					return result.get();
				}
			}
		}
		return TestResult.fail();
	}
	
	public record TestResult(
			boolean isSuccess,
			Optional<IItemHandler> itemHandler,
			Optional<IFluidHandler> fluidHandler,
			Optional<MIEnergyStorage> energyHandler
	)
	{
		public static TestResult fail()
		{
			return new TestResult(
					false,
					Optional.empty(),
					Optional.empty(),
					Optional.empty()
			);
		}
		
		public static TestResult success(IItemHandler itemHandler, IFluidHandler fluidHandler, MIEnergyStorage energyHandler)
		{
			return new TestResult(
					true,
					Optional.ofNullable(itemHandler),
					Optional.ofNullable(fluidHandler),
					Optional.ofNullable(energyHandler)
			);
		}
		
		public static TestResult success()
		{
			return success(null, null, null);
		}
	}
	
	@Override
	public void invalidate()
	{
		List<BlockPos> machinesFound = Lists.newArrayList();
		List<IItemHandler> itemHandlers = Lists.newArrayList();
		List<IFluidHandler> fluidHandlers = Lists.newArrayList();
		List<MIEnergyStorage> energyHandlers = Lists.newArrayList();
		
		for(BlockPos pos : this.getSpannedBlocks(false))
		{
			TestResult result = this.test(pos);
			if(result.isSuccess())
			{
				machinesFound.add(pos);
				result.itemHandler().ifPresent(itemHandlers::add);
				result.fluidHandler().ifPresent(fluidHandlers::add);
				result.energyHandler().ifPresent(energyHandlers::add);
			}
			else
			{
				break;
			}
		}
		
		this.positions = Collections.unmodifiableList(machinesFound);
		this.linkCount = positions.size();
		this.itemHandlers = Collections.unmodifiableList(itemHandlers);
		this.fluidHandlers = Collections.unmodifiableList(fluidHandlers);
		this.energyHandlers = Collections.unmodifiableList(energyHandlers);
	}
}
