package net.swedz.extended_industrialization.machines.component.chainer;

import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.component.chainer.link.ChainerLinkable;
import net.swedz.extended_industrialization.machines.component.chainer.link.LinkContext;
import net.swedz.extended_industrialization.machines.component.chainer.link.LinkResult;
import net.swedz.extended_industrialization.machines.component.chainer.link.LinkableBehaviorHolder;
import net.swedz.extended_industrialization.machines.component.chainer.link.linkable.ChainerRelayLinkable;
import net.swedz.extended_industrialization.machines.component.chainer.link.linkable.MachineBlockLinkable;
import net.swedz.extended_industrialization.machines.component.chainer.link.linkable.TaggedLinkable;
import net.swedz.tesseract.neoforge.behavior.BehaviorRegistry;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public final class ChainerLinks implements ChainerElement
{
	private static final BehaviorRegistry<LinkableBehaviorHolder, ChainerLinkable, LinkContext> BEHAVIOR_REGISTRY = BehaviorRegistry.create(LinkableBehaviorHolder::new);
	
	public static void registerLinkable(Supplier<ChainerLinkable> creator)
	{
		BEHAVIOR_REGISTRY.register(creator);
	}
	
	static
	{
		registerLinkable(ChainerRelayLinkable::new);
		registerLinkable(MachineBlockLinkable::new);
		registerLinkable(TaggedLinkable::new);
	}
	
	private final Supplier<Level>     level;
	private final BlockPos            origin;
	private final Supplier<Direction> direction;
	private final int                 maxConnections;
	private final Supplier<Boolean>   allowOperation;
	
	private final LinkableBehaviorHolder behaviorHolder;
	
	private List<BlockPos> positions = List.of();
	private int            linkCount;
	
	private List<IItemHandler>    itemHandlers   = List.of();
	private List<IFluidHandler>   fluidHandlers  = List.of();
	private List<MIEnergyStorage> energyHandlers = List.of();
	
	private Optional<BlockPos> failPosition = Optional.empty();
	
	public ChainerLinks(MachineChainerMachineBlockEntity machine, int maxConnections, Supplier<Boolean> allowOperation)
	{
		this.level = machine::getLevel;
		this.origin = machine.getBlockPos();
		this.direction = () -> machine.orientation.facingDirection;
		this.maxConnections = maxConnections;
		this.allowOperation = allowOperation;
		
		this.behaviorHolder = BEHAVIOR_REGISTRY.createHolder();
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
	
	public BlockPos position(int offset)
	{
		return origin.relative(this.direction(), offset);
	}
	
	public BlockPos positionAfter()
	{
		return this.position(this.count() + 1);
	}
	
	public boolean isAfter(BlockPos pos)
	{
		return pos.equals(this.positionAfter());
	}
	
	public int count()
	{
		return linkCount;
	}
	
	public boolean hasConnections()
	{
		return this.count() > 0;
	}
	
	void count(int count)
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
	
	public int maxConnections()
	{
		return maxConnections;
	}
	
	List<BlockPos> getSpannedBlocks(boolean includeOrigin, boolean includeFailure)
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
		if(includeFailure && this.hasFailure())
		{
			blocks.add(this.failPosition().orElseThrow());
		}
		return Collections.unmodifiableList(blocks);
	}
	
	Set<ChunkPos> getSpannedChunks(boolean includeOrigin, boolean includeFailure)
	{
		Set<ChunkPos> chunks = Sets.newHashSet();
		for(BlockPos block : this.getSpannedBlocks(includeOrigin, includeFailure))
		{
			chunks.add(new ChunkPos(block));
		}
		return Collections.unmodifiableSet(chunks);
	}
	
	public boolean doesAllowOperation()
	{
		return allowOperation.get();
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
	
	public Optional<BlockPos> failPosition()
	{
		return failPosition;
	}
	
	public boolean hasFailure()
	{
		return failPosition.isPresent();
	}
	
	public int failPositionOffset()
	{
		return failPosition.map((fail) -> fail.distManhattan(origin)).orElseThrow();
	}
	
	public boolean isFailPosition(BlockPos pos)
	{
		return failPosition.map((fail) -> fail.equals(pos)).orElse(false);
	}
	
	void failPosition(BlockPos pos)
	{
		failPosition = Optional.ofNullable(pos);
	}
	
	@Override
	public void clear()
	{
		positions = List.of();
		linkCount = 0;
		itemHandlers = List.of();
		fluidHandlers = List.of();
		energyHandlers = List.of();
		failPosition = Optional.empty();
	}
	
	public LinkResult test(BlockPos pos)
	{
		return behaviorHolder.test(LinkContext.of(this, pos));
	}
	
	public LinkResult test(ItemStack stack)
	{
		return behaviorHolder.test(LinkContext.of(this, stack));
	}
	
	@Override
	public void invalidate()
	{
		List<BlockPos> machinesFound = Lists.newArrayList();
		List<IItemHandler> itemHandlers = Lists.newArrayList();
		List<IFluidHandler> fluidHandlers = Lists.newArrayList();
		List<MIEnergyStorage> energyHandlers = Lists.newArrayList();
		
		LinkResult result = null;
		for(BlockPos pos : this.getSpannedBlocks(false, false))
		{
			result = this.test(pos);
			if(result.isSuccess())
			{
				machinesFound.add(pos);
				result.itemHandler().ifPresent(itemHandlers::add);
				result.fluidHandler().ifPresent(fluidHandlers::add);
				result.energyHandler().ifPresent(energyHandlers::add);
			}
			else if(result.invalidatesEverything())
			{
				this.clear();
				this.failPosition = result.failPosition();
				return;
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
		this.failPosition = result == null ? Optional.empty() : result.failPosition();
	}
}
