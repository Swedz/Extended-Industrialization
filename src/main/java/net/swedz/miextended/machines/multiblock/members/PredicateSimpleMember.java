package net.swedz.miextended.machines.multiblock.members;

import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class PredicateSimpleMember implements SimpleMember
{
	private final Predicate<BlockState> predicate;
	private final Supplier<BlockState>  preview;
	
	public PredicateSimpleMember(Predicate<BlockState> predicate, Supplier<BlockState> preview)
	{
		this.predicate = predicate;
		this.preview = preview;
	}
	
	public PredicateSimpleMember(Predicate<BlockState> predicate, BlockState preview)
	{
		this(predicate, () -> preview);
	}
	
	public PredicateSimpleMember(Predicate<BlockState> predicate, Block preview)
	{
		this(predicate, preview::defaultBlockState);
	}
	
	@Override
	public boolean matchesState(BlockState state)
	{
		return predicate.test(state);
	}
	
	@Override
	public BlockState getPreviewState()
	{
		return preview.get();
	}
}
