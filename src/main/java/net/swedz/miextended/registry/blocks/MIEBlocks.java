package net.swedz.miextended.registry.blocks;

import com.google.common.collect.Sets;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.registry.items.MIEItems;

import java.util.Set;
import java.util.function.Function;

public final class MIEBlocks
{
	public static final class Registry
	{
		public static final  DeferredRegister.Blocks BLOCKS  = DeferredRegister.createBlocks(MIExtended.ID);
		private static final Set<BlockHolder>        HOLDERS = Sets.newHashSet();
		
		private static void init(IEventBus bus)
		{
			BLOCKS.register(bus);
		}
	}
	
	public static void init(IEventBus bus)
	{
		Registry.init(bus);
	}
	
	public static Set<BlockHolder> values()
	{
		return Set.copyOf(Registry.HOLDERS);
	}
	
	public static <BlockType extends Block, ItemType extends BlockItem> BlockHolder<BlockType, ItemType> create(String id, String englishName,
																												Function<BlockBehaviour.Properties, BlockType> blockCreator,
																												Function<Item.Properties, ItemType> itemCreator)
	{
		BlockHolder<BlockType, ItemType> holder = new BlockHolder<>(
				MIExtended.id(id), englishName,
				Registry.BLOCKS, blockCreator,
				MIEItems.Registry.ITEMS, itemCreator
		);
		Registry.HOLDERS.add(holder);
		return holder;
	}
}
