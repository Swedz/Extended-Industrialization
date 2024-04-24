package net.swedz.miextended.registry.blocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.miextended.registry.items.ItemHolder;

import java.util.function.BiFunction;
import java.util.function.Function;

public class BlockWithItemHolder<BlockType extends Block, ItemType extends BlockItem> extends BlockHolder<BlockType> implements ItemLike
{
	private final ItemHolder<ItemType> itemHolder;
	
	public BlockWithItemHolder(ResourceLocation location, String englishName,
							   DeferredRegister.Blocks registerBlocks, Function<BlockBehaviour.Properties, BlockType> blockCreator,
							   DeferredRegister.Items registerItems, BiFunction<Block, Item.Properties, ItemType> itemCreator)
	{
		super(location, englishName, registerBlocks, blockCreator);
		this.itemHolder = new ItemHolder<>(location, englishName, registerItems, (p) -> itemCreator.apply(this.get(), p));
	}
	
	public ItemHolder<ItemType> item()
	{
		return itemHolder;
	}
	
	@Override
	public BlockWithItemHolder<BlockType, ItemType> register()
	{
		this.guaranteeUnlocked();
		
		registerableBlock.register(identifier, DeferredRegister.Blocks::registerBlock);
		itemHolder.register();
		
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
		return itemHolder.asItem();
	}
}
