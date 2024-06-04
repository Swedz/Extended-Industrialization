package net.swedz.extended_industrialization.api.registry.holder;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.api.registry.ModeledRegisteredObjectHolder;
import net.swedz.extended_industrialization.api.registry.registerable.BlockRegisterableWrapper;

import java.util.function.Consumer;
import java.util.function.Function;

public class BlockHolder<BlockType extends Block> extends ModeledRegisteredObjectHolder<Block, BlockType, BlockStateProvider, BlockHolder<BlockType>>
{
	protected final BlockRegisterableWrapper<BlockType> registerableBlock;
	
	protected Function<BlockLootSubProvider, LootTable.Builder> lootTableBuilder;
	
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
		return this.self();
	}
	
	public BlockHolder<BlockType> withLootTable(Function<BlockHolder<BlockType>, Function<BlockLootSubProvider, LootTable.Builder>> builder)
	{
		lootTableBuilder = builder.apply(this.self());
		return this.self();
	}
	
	public boolean hasLootTable()
	{
		return lootTableBuilder != null;
	}
	
	public Function<BlockLootSubProvider, LootTable.Builder> getLootTableBuilder()
	{
		return lootTableBuilder;
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
