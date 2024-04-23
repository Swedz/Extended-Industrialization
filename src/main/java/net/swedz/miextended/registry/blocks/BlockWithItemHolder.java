package net.swedz.miextended.registry.blocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.miextended.registry.api.registerable.ItemRegisterableWrapper;

import java.util.function.Function;

public class BlockWithItemHolder<BlockType extends Block, ItemType extends BlockItem> extends BlockHolder<BlockType> implements ItemLike
{
	private final ItemRegisterableWrapper<ItemType> registerableItem;
	
	public BlockWithItemHolder(ResourceLocation location, String englishName,
							   DeferredRegister.Blocks registerBlocks, Function<BlockBehaviour.Properties, BlockType> blockCreator,
							   DeferredRegister.Items registerItems, Function<Item.Properties, ItemType> itemCreator)
	{
		super(location, englishName, registerBlocks, blockCreator);
		this.registerableItem = new ItemRegisterableWrapper<>(registerItems, new Item.Properties(), itemCreator);
	}
	
	public ItemRegisterableWrapper<ItemType> registerableItem()
	{
		return registerableItem;
	}
	
	@Override
	public BlockWithItemHolder<BlockType, ItemType> register()
	{
		this.guaranteeUnlocked();
		
		registerableBlock.register(identifier, DeferredRegister.Blocks::registerBlock);
		registerableItem.register(identifier, DeferredRegister.Items::registerItem);
		
		this.lock();
		return this;
	}
	
	@Override
	public BlockType get()
	{
		return registerableBlock.getOrThrow();
	}
	
	@Override
	public Item asItem()
	{
		return registerableItem.getOrThrow();
	}
}
