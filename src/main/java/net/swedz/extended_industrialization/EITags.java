package net.swedz.extended_industrialization;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class EITags
{
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
