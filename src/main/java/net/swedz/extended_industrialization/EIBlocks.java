package net.swedz.extended_industrialization;

import aztech.modern_industrialization.util.TagHelper;
import com.google.common.collect.Sets;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.tesseract.neoforge.registry.SortOrder;
import net.swedz.tesseract.neoforge.registry.common.CommonLootTableBuilders;
import net.swedz.tesseract.neoforge.registry.common.CommonModelBuilders;
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder;
import net.swedz.tesseract.neoforge.registry.holder.BlockWithItemHolder;

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
	
	public static final BlockHolder<Block> MACHINE_CHAINER_RELAY = create("machine_chainer_relay", "Machine Chainer Relay", Block::new, BlockItem::new, EISortOrder.MACHINES).withProperties((p) -> p.mapColor(MapColor.METAL).destroyTime(4f).requiresCorrectToolForDrops()).tag(TagHelper.getMiningLevelTag(1)).tag(EITags.Blocks.MACHINE_CHAINER_RELAY).withLootTable(CommonLootTableBuilders::self).withModel(CommonModelBuilders::blockstateOnly).register();
	public static final BlockHolder<Block> STEEL_PLATED_BRICKS   = create("steel_plated_bricks", "Steel Plated Bricks", Block::new, BlockItem::new, EISortOrder.CASINGS).withProperties((p) -> p.destroyTime(5f).explosionResistance(6f).requiresCorrectToolForDrops()).tag(TagHelper.getMiningLevelTag(1)).withLootTable(CommonLootTableBuilders::self).withModel(CommonModelBuilders::blockCubeAll).register();
	
	public static Set<BlockHolder> values()
	{
		return Set.copyOf(Registry.HOLDERS);
	}
	
	public static Block get(String id)
	{
		return Registry.HOLDERS.stream().filter((b) -> b.identifier().id().equals(id)).findFirst().orElseThrow().get();
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
	
	public static <BlockType extends Block, ItemType extends BlockItem> BlockWithItemHolder<BlockType, ItemType> create(
			String id, String englishName,
			Function<BlockBehaviour.Properties, BlockType> blockCreator,
			BiFunction<Block, Item.Properties, ItemType> itemCreator,
			SortOrder sortOrder
	)
	{
		BlockWithItemHolder<BlockType, ItemType> holder = new BlockWithItemHolder<>(
				EI.id(id), englishName,
				Registry.BLOCKS, blockCreator,
				EIItems.Registry.ITEMS, itemCreator
		);
		holder.item().sorted(sortOrder);
		Registry.include(holder);
		EIItems.Registry.include(holder.item());
		return holder;
	}
}
