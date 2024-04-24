package net.swedz.extended_industrialization.registry.blocks;

import com.google.common.collect.Sets;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.registry.items.EIItems;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class EIBlocks
{
	public static final class Registry
	{
		public static final  DeferredRegister.Blocks              BLOCKS         = DeferredRegister.createBlocks(EI.ID);
		public static final  DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EI.ID);
		private static final Set<BlockHolder>                     HOLDERS        = Sets.newHashSet();
		
		private static void init(IEventBus bus)
		{
			BLOCKS.register(bus);
			BLOCK_ENTITIES.register(bus);
		}
		
		public static void include(BlockHolder holder)
		{
			HOLDERS.add(holder);
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
	
	public static <BlockType extends Block> BlockHolder<BlockType> create(String id, String englishName,
																		  Function<BlockBehaviour.Properties, BlockType> blockCreator)
	{
		BlockHolder<BlockType> holder = new BlockHolder<>(
				EI.id(id), englishName,
				Registry.BLOCKS, blockCreator
		);
		Registry.include(holder);
		return holder;
	}
	
	public static <BlockType extends Block, ItemType extends BlockItem> BlockWithItemHolder<BlockType, ItemType> create(String id, String englishName,
																														Function<BlockBehaviour.Properties, BlockType> blockCreator,
																														BiFunction<Block, Item.Properties, ItemType> itemCreator)
	{
		BlockWithItemHolder<BlockType, ItemType> holder = new BlockWithItemHolder<>(
				EI.id(id), englishName,
				Registry.BLOCKS, blockCreator,
				EIItems.Registry.ITEMS, itemCreator
		);
		Registry.include(holder);
		EIItems.Registry.include(holder.item());
		return holder;
	}
}
