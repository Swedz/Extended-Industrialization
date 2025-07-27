package net.swedz.extended_industrialization;

import com.google.common.collect.Maps;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.Collections;
import java.util.Map;

public final class EITags
{
	private static final Map<TagKey<Item>, String> TRANSLATIONS = Maps.newHashMap();
	
	public static Map<TagKey<Item>, String> translations()
	{
		return Collections.unmodifiableMap(TRANSLATIONS);
	}
	
	public static final class Items
	{
		public static final TagKey<Item> FARMER_PLANTABLE           = item("farmer_plantable", "Farmer Plantable");
		public static final TagKey<Item> FARMER_VOIDABLE            = item("farmer_voidable", "Farmer Voidable");
		public static final TagKey<Item> PHOTOVOLTAIC_CELL          = item("photovoltaic_cell", "Photovoltaic Cells");
		public static final TagKey<Item> RAINBOW_DYEABLE            = item("rainbow_dyeable", "Rainbow Dyeable");
		public static final TagKey<Item> PROCESSING_ARRAY_BLACKLIST = item("processing_array_blacklist", "Processing Array Blacklist");
		
		public static final class EnchantmentModules
		{
			public static final TagKey<Item> FARMER            = item("enchantment_modules/farmer", "Farmer Enchantment Modules");
			public static final TagKey<Item> LETHAL_TESLA_COIL = item("enchantment_modules/lethal_tesla_coil", "Lethal Tesla Coil Enchantment Modules");
		}
	}
	
	public static final class Fluids
	{
		public static final TagKey<Fluid> HONEY = fluidCommon("honey");
	}
	
	public static final class Blocks
	{
		public static final TagKey<Block> FARMER_DIRT              = block("farmer_dirt");
		public static final TagKey<Block> MACHINE_CHAINER_LINKABLE = block("machine_chainer/linkable");
		public static final TagKey<Block> MACHINE_CHAINER_RELAY    = block("machine_chainer/relay");
	}
	
	public static final class DamageTypes
	{
		public static final TagKey<DamageType> NANO_SABER_SWEEP           = damageType("nano_saber_sweep");
		public static final TagKey<DamageType> NANO_SABER_SWEEP_BEHEADING = damageType("nano_saber_sweep/beheading");
	}
	
	public static final class GeneratedRecipesBlacklist
	{
		public static final TagKey<Item>  CANNING_FOOD    = item("generated_recipes_blacklist/canning_food", "Canning Food Generated Recipes Blacklist");
		public static final TagKey<Fluid> CANNING_BUCKETS = fluid("generated_recipes_blacklist/canning_buckets");
		public static final TagKey<Item>  COMPOSTING      = item("generated_recipes_blacklist/composting", "Composting Generated Recipes Blacklist");
	}
	
	public static TagKey<Item> item(String path, String englishName)
	{
		TagKey<Item> tag = TagKey.create(Registries.ITEM, EI.id(path));
		TRANSLATIONS.put(tag, englishName);
		return tag;
	}
	
	public static TagKey<Item> itemCommon(String path)
	{
		return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
	}
	
	public static TagKey<Fluid> fluid(String path)
	{
		return TagKey.create(Registries.FLUID, EI.id(path));
	}
	
	public static TagKey<Fluid> fluidCommon(String path)
	{
		return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", path));
	}
	
	public static TagKey<Block> block(String path)
	{
		return TagKey.create(Registries.BLOCK, EI.id(path));
	}
	
	public static TagKey<Block> blockCommon(String path)
	{
		return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", path));
	}
	
	public static TagKey<DamageType> damageType(String path)
	{
		return TagKey.create(Registries.DAMAGE_TYPE, EI.id(path));
	}
}
