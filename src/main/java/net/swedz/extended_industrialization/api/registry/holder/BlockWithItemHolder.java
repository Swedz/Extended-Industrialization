package net.swedz.extended_industrialization.api.registry.holder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.api.registry.CommonModelBuilders;

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
		this.itemHolder = new ItemHolder<>(location, englishName, registerItems, (p) -> itemCreator.apply(this.get(), p))
				.withModel(CommonModelBuilders::block);
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
