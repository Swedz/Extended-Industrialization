package net.swedz.extended_industrialization;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import net.swedz.extended_industrialization.datamap.EnchantmentModule;
import net.swedz.extended_industrialization.datamap.FarmerSimpleTallCropSize;
import net.swedz.extended_industrialization.datamap.FertilizerPotency;
import net.swedz.extended_industrialization.datamap.LargeElectricFurnaceTier;
import net.swedz.extended_industrialization.datamap.TeslaTowerTierData;

import java.util.Set;

public final class EIDataMaps
{
	private static final Set<DataMapType<?, ?>> DATA_MAPS = Sets.newHashSet();
	
	public static final DataMapType<Block, FarmerSimpleTallCropSize> FARMER_SIMPLE_TALL_CROP_SIZE = create("farmer_simple_tall_crop_size", Registries.BLOCK, FarmerSimpleTallCropSize.CODEC, true);
	public static final DataMapType<Fluid, FertilizerPotency>        FERTILIZER_POTENCY           = create("fertilizer_potency", Registries.FLUID, FertilizerPotency.CODEC, true);
	public static final DataMapType<Block, LargeElectricFurnaceTier> LARGE_ELECTRIC_FURNACE_TIER  = create("large_electric_furnace_tier", Registries.BLOCK, LargeElectricFurnaceTier.CODEC, true);
	public static final DataMapType<Block, TeslaTowerTierData>       TESLA_TOWER_TIER             = create("tesla_tower_tier", Registries.BLOCK, TeslaTowerTierData.CODEC, true);
	public static final DataMapType<Item, EnchantmentModule>         ENCHANTMENT_MODULE           = create("enchantment_module", Registries.ITEM, EnchantmentModule.CODEC, true);
	
	private static <R, T> DataMapType<R, T> create(String name, ResourceKey<Registry<R>> registry, Codec<T> codec, boolean sync)
	{
		var builder = DataMapType.builder(EI.id(name), registry, codec);
		if(sync)
		{
			builder = builder.synced(codec, true);
		}
		var type = builder.build();
		DATA_MAPS.add(type);
		return type;
	}
	
	public static void init(RegisterDataMapTypesEvent event)
	{
		DATA_MAPS.forEach(event::register);
	}
}
