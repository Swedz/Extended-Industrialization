package net.swedz.extended_industrialization;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import net.swedz.extended_industrialization.datamap.FarmerSimpleTallCropSize;
import net.swedz.extended_industrialization.datamap.FertilizerPotency;
import net.swedz.extended_industrialization.datamap.LargeElectricFurnaceTier;

public final class EIDataMaps
{
	public static final DataMapType<Block, FarmerSimpleTallCropSize> FARMER_SIMPLE_TALL_CROP_SIZE = DataMapType
			.builder(EI.id("farmer_simple_tall_crop_size"), Registries.BLOCK, FarmerSimpleTallCropSize.CODEC)
			.synced(FarmerSimpleTallCropSize.CODEC, true)
			.build();
	
	public static final DataMapType<Fluid, FertilizerPotency> FERTILIZER_POTENCY = DataMapType
			.builder(EI.id("fertilizer_potency"), Registries.FLUID, FertilizerPotency.CODEC)
			.synced(FertilizerPotency.CODEC, true)
			.build();
	
	public static final DataMapType<Block, LargeElectricFurnaceTier> LARGE_ELECTRIC_FURNACE_TIER = DataMapType
			.builder(EI.id("large_electric_furnace_tier"), Registries.BLOCK, LargeElectricFurnaceTier.CODEC)
			.synced(LargeElectricFurnaceTier.CODEC, true)
			.build();
	
	public static void init(RegisterDataMapTypesEvent event)
	{
		event.register(FARMER_SIMPLE_TALL_CROP_SIZE);
		event.register(FERTILIZER_POTENCY);
		event.register(LARGE_ELECTRIC_FURNACE_TIER);
	}
}
