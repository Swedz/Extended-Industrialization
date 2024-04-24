package net.swedz.miextended.registry.blocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.miextended.registry.api.ModeledRegisteredObjectHolder;
import net.swedz.miextended.registry.api.registerable.BlockRegisterableWrapper;

import java.util.function.Consumer;
import java.util.function.Function;

public class BlockHolder<BlockType extends Block> extends ModeledRegisteredObjectHolder<Block, BlockType, BlockStateProvider, BlockHolder<BlockType>>
{
	protected final BlockRegisterableWrapper<BlockType> registerableBlock;
	
	public BlockHolder(ResourceLocation location, String englishName,
					   DeferredRegister.Blocks registerBlocks, Function<BlockBehaviour.Properties, BlockType> blockCreator)
	{
		super(location, englishName);
		this.registerableBlock = new BlockRegisterableWrapper<>(registerBlocks, BlockBehaviour.Properties.of(), blockCreator);
	}
	
	public BlockRegisterableWrapper<BlockType> registerableBlock()
	{
		return registerableBlock;
	}
	
	public BlockHolder<BlockType> withProperties(Consumer<BlockBehaviour.Properties> action)
	{
		action.accept(registerableBlock.properties());
		return this;
	}
	
	@Override
	public BlockHolder<BlockType> register()
	{
		this.guaranteeUnlocked();
		
		registerableBlock.register(identifier, DeferredRegister.Blocks::registerBlock);
		
		this.lock();
		return this.self();
	}
	
	@Override
	public BlockType get()
	{
		return registerableBlock.getOrThrow();
	}
}
