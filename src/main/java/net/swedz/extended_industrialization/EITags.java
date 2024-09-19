package net.swedz.extended_industrialization;

import com.google.common.collect.Maps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.Map;

public final class EITags
{
	private static final Map<TagKey<Item>, String> TAG_NAMES = Maps.newHashMap();
	
	public static Map<TagKey<Item>, String> tagNames()
	{
		return Collections.unmodifiableMap(TAG_NAMES);
	}
	
	public static final class Items
	{
		public static final TagKey<Item> FARMER_PLANTABLE  = item("farmer_plantable", "Farmer Plantable");
		public static final TagKey<Item> FARMER_VOIDABLE   = item("farmer_voidable", "Farmer Voidable");
		public static final TagKey<Item> PHOTOVOLTAIC_CELL = item("photovoltaic_cell", "Photovoltaic Cells");
		public static final TagKey<Item> RAINBOW_DYEABLE   = item("rainbow_dyeable", "Rainbow Dyeable");
	}
	
	public static final class Blocks
	{
		public static final TagKey<Block> FARMER_DIRT              = block("farmer_dirt");
		public static final TagKey<Block> MACHINE_CHAINER_LINKABLE = block("machine_chainer/linkable");
		public static final TagKey<Block> MACHINE_CHAINER_RELAY    = block("machine_chainer/relay");
	}
	
	public static TagKey<Item> item(String path, String englishName)
	{
		TagKey<Item> tag = TagKey.create(BuiltInRegistries.ITEM.key(), EI.id(path));
		TAG_NAMES.put(tag, englishName);
		return tag;
	}
	
	public static TagKey<Item> itemCommon(String path)
	{
		return TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("c", path));
	}
	
	public static TagKey<Block> block(String path)
	{
		return TagKey.create(BuiltInRegistries.BLOCK.key(), EI.id(path));
	}
	
	public static TagKey<Block> blockCommon(String path)
	{
		return TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath("c", path));
	}
}
