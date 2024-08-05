package net.swedz.extended_industrialization.datagen.server.provider.datamaps;

import aztech.modern_industrialization.MI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EIDataMaps;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.datamaps.FarmerSimpleTallCropSize;
import net.swedz.extended_industrialization.datamaps.FertilizerPotency;
import net.swedz.extended_industrialization.datamaps.LargeElectricFurnaceTier;
import net.swedz.tesseract.neoforge.registry.holder.FluidHolder;

public final class DataMapDatagenProvider extends DataMapProvider
{
	public DataMapDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider());
	}
	
	@Override
	protected void gather()
	{
		this.addFarmerSimpleTallCropSize(Blocks.SUGAR_CANE, 3);
		this.addFarmerSimpleTallCropSize(Blocks.CACTUS, 3);
		
		this.addFluidFertilizerPotency(EIFluids.MANURE, 25, 100);
		this.addFluidFertilizerPotency(EIFluids.COMPOSTED_MANURE, 25, 50);
		this.addFluidFertilizerPotency(EIFluids.NPK_FERTILIZER, 10, 10);
		
		this.addLargeElectricFurnaceTier(MI.id("cupronickel_coil"), 8, 0.75f);
		this.addLargeElectricFurnaceTier(MI.id("kanthal_coil"), 32, 0.75f);
	}
	
	private void addFarmerSimpleTallCropSize(ResourceLocation block, int maxHeight)
	{
		this.builder(EIDataMaps.FARMER_SIMPLE_TALL_CROP_SIZE).add(block, new FarmerSimpleTallCropSize(maxHeight), false);
	}
	
	private void addFarmerSimpleTallCropSize(Block block, int maxHeight)
	{
		this.addFarmerSimpleTallCropSize(BuiltInRegistries.BLOCK.getKey(block), maxHeight);
	}
	
	private void addFluidFertilizerPotency(FluidHolder fluid, int tickRate, int mbToConsumePerFertilizerTick)
	{
		this.builder(EIDataMaps.FERTILIZER_POTENCY).add(fluid.identifier().location(), new FertilizerPotency(tickRate, mbToConsumePerFertilizerTick), false);
	}
	
	private void addLargeElectricFurnaceTier(ResourceLocation block, int batchSize, float euCostMultiplier)
	{
		this.builder(EIDataMaps.LARGE_ELECTRIC_FURNACE_TIER).add(block, new LargeElectricFurnaceTier(batchSize, euCostMultiplier), false);
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
