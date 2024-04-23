package net.swedz.miextended.registry.blocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.miextended.registry.api.ModeledRegisteredObjectHolder;
import net.swedz.miextended.registry.api.registerable.BlockRegisterableWrapper;
import net.swedz.miextended.registry.api.registerable.ItemRegisterableWrapper;

import java.util.function.Function;

public class BlockHolder<BlockType extends Block, ItemType extends BlockItem> extends ModeledRegisteredObjectHolder<Block, BlockType, BlockModelBuilder, BlockHolder<BlockType, ItemType>> implements ItemLike
{
	private final BlockRegisterableWrapper<BlockType> registerBlock;
	private final ItemRegisterableWrapper<ItemType>   registerItem;
	
	public BlockHolder(ResourceLocation location, String englishName,
					   DeferredRegister.Blocks registerBlocks, Function<BlockBehaviour.Properties, BlockType> blockCreator,
					   DeferredRegister.Items registerItems, Function<Item.Properties, ItemType> itemCreator)
	{
		super(location, englishName);
		this.registerBlock = new BlockRegisterableWrapper<>(registerBlocks, BlockBehaviour.Properties.of(), blockCreator);
		this.registerItem = new ItemRegisterableWrapper<>(registerItems, new Item.Properties(), itemCreator);
	}
	
	@Override
	public BlockHolder<BlockType, ItemType> register()
	{
		this.guaranteeUnlocked();
		
		registerBlock.register(identifier, DeferredRegister.Blocks::registerBlock);
		registerItem.register(identifier, DeferredRegister.Items::registerItem);
		
		this.lock();
		return this.self();
	}
	
	@Override
	public BlockType get()
	{
		return registerBlock.getOrThrow();
	}
	
	@Override
	public Item asItem()
	{
		return registerItem.getOrThrow();
	}
}
