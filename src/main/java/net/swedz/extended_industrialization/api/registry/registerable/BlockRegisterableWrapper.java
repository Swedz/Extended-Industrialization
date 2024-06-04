package net.swedz.extended_industrialization.api.registry.registerable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class BlockRegisterableWrapper<Type extends Block> extends RegisterableWrapper<Type, DeferredBlock<Type>, DeferredRegister.Blocks, BlockBehaviour.Properties>
{
	public BlockRegisterableWrapper(DeferredRegister.Blocks register, BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, Type> creator)
	{
		super(register, properties, creator);
	}
}
