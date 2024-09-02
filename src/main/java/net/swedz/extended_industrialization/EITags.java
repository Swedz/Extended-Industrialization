package net.swedz.extended_industrialization;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class EITags
{
	public static final TagKey<Item> FARMER_PLANTABLE  = item("farmer_plantable");
	public static final TagKey<Item> FARMER_VOIDABLE   = item("farmer_voidable");
	public static final TagKey<Item> PHOTOVOLTAIC_CELL = item("photovoltaic_cell");
	public static final TagKey<Item> RAINBOW_DYEABLE   = item("rainbow_dyeable");
	
	public static final TagKey<Block> FARMER_DIRT           = block("farmer_dirt");
	public static final TagKey<Block> MACHINE_CHAINER_RELAY = block("machine_chainer_relay");
	
	public static TagKey<Item> item(String path)
	{
		return TagKey.create(BuiltInRegistries.ITEM.key(), EI.id(path));
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
