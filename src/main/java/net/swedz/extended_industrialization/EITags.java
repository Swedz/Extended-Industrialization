package net.swedz.extended_industrialization;

import com.google.common.collect.Maps;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
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
	
	public interface Items
	{
		TagKey<Item> FARMER_PLANTABLE           = item("farmer_plantable", "Farmer Plantable");
		TagKey<Item> FARMER_VOIDABLE            = item("farmer_voidable", "Farmer Voidable");
		TagKey<Item> PHOTOVOLTAIC_CELL          = item("photovoltaic_cell", "Photovoltaic Cells");
		TagKey<Item> RAINBOW_DYEABLE            = item("rainbow_dyeable", "Rainbow Dyeable");
		TagKey<Item> PROCESSING_ARRAY_BLACKLIST = item("processing_array_blacklist", "Processing Array Blacklist");
		
		TagKey<Item> BUCKETS_HONEY = itemCommon("buckets/honey");
		
		interface EnchantmentModules
		{
			TagKey<Item> FARMER            = item("enchantment_modules/farmer", "Farmer Enchantment Modules");
			TagKey<Item> LETHAL_TESLA_COIL = item("enchantment_modules/lethal_tesla_coil", "Lethal Tesla Coil Enchantment Modules");
		}
	}
	
	public interface Fluids
	{
		TagKey<Fluid> HONEY = fluidCommon("honey");
	}
	
	public interface Blocks
	{
		TagKey<Block> FARMER_DIRT               = block("farmer_dirt");
		TagKey<Block> MACHINE_CHAINER_LINKABLE  = block("machine_chainer/linkable");
		TagKey<Block> MACHINE_CHAINER_RELAY     = block("machine_chainer/relay");
		TagKey<Block> MACHINE_CHAINER_BLACKLIST = block("machine_chainer/blacklist");
	}
	
	public interface DimensionTypes
	{
		TagKey<DimensionType> SOLAR_BLACKLIST = dimensionType("solar_blacklist");
	}
	
	public interface DamageTypes
	{
		TagKey<DamageType> NANO_SABER_SWEEP           = damageType("nano_saber_sweep");
		TagKey<DamageType> NANO_SABER_SWEEP_BEHEADING = damageType("nano_saber_sweep/beheading");
	}
	
	public interface MobEffects
	{
		TagKey<MobEffect> ELECTRIC_BEACON_BLACKLIST = mobEffect("electric_beacon/blacklist");
	}
	
	public interface GeneratedRecipesBlacklist
	{
		TagKey<Item>  CANNING_FOOD    = item("generated_recipes_blacklist/canning_food", "Canning Food Generated Recipes Blacklist");
		TagKey<Fluid> CANNING_BUCKETS = fluid("generated_recipes_blacklist/canning_buckets");
		TagKey<Item>  COMPOSTING      = item("generated_recipes_blacklist/composting", "Composting Generated Recipes Blacklist");
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
	
	public static TagKey<DimensionType> dimensionType(String path)
	{
		return TagKey.create(Registries.DIMENSION_TYPE, EI.id(path));
	}
	
	public static TagKey<DamageType> damageType(String path)
	{
		return TagKey.create(Registries.DAMAGE_TYPE, EI.id(path));
	}
	
	public static TagKey<MobEffect> mobEffect(String path)
	{
		return TagKey.create(Registries.MOB_EFFECT, EI.id(path));
	}
}
