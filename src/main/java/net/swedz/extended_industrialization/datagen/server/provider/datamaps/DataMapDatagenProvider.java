package net.swedz.extended_industrialization.datagen.server.provider.datamaps;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.api.energy.CableTier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIDataMaps;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.datamap.EnchantmentModule;
import net.swedz.extended_industrialization.datamap.FarmerSimpleTallCropSize;
import net.swedz.extended_industrialization.datamap.FertilizerPotency;
import net.swedz.extended_industrialization.datamap.LargeElectricFurnaceTier;
import net.swedz.extended_industrialization.datamap.TeslaTowerTierData;
import net.swedz.tesseract.neoforge.registry.holder.FluidHolder;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

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
		
		this.addFluidFertilizerPotency(EIFluids.MANURE, 25, 300);
		this.addFluidFertilizerPotency(EIFluids.COMPOSTED_MANURE, 25, 150);
		this.addFluidFertilizerPotency(EIFluids.NPK_FERTILIZER, 10, 30);
		
		this.addLargeElectricFurnaceTier(MI.id("cupronickel_coil"), 16, 0.75f);
		this.addLargeElectricFurnaceTier(MI.id("kanthal_coil"), 32, 0.75f);
		
		this.addTeslaTowerTier(EI.id("copper_tesla_winding"), CableTier.LV.getMaxTransfer() * 6, 32, 64);
		this.addTeslaTowerTier(EI.id("electrum_tesla_winding"), CableTier.MV.getMaxTransfer() * 6, 32 * 2, 64 * 4);
		this.addTeslaTowerTier(EI.id("aluminum_tesla_winding"), CableTier.HV.getMaxTransfer() * 6, 32 * 2 * 2, 64 * 4 * 4);
		this.addTeslaTowerTier(EI.id("annealed_copper_tesla_winding"), CableTier.EV.getMaxTransfer() * 6, 32 * 2 * 2 * 2, 64 * 4 * 4 * 4);
		this.addTeslaTowerTier(EI.id("superconductor_tesla_winding"), CableTier.SUPERCONDUCTOR.getMaxTransfer() * 6, 32 * 2 * 2 * 2 * 2, 64 * 4 * 4 * 4 * 4);
		
		this.addEnchantmentModule(EIItems.SILK_TOUCH_MODULE, Enchantments.SILK_TOUCH, 1);
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
	
	private void addTeslaTowerTier(ResourceLocation block, long maxTransfer, int maxDistance, long drain)
	{
		this.builder(EIDataMaps.TESLA_TOWER_TIER).add(block, new TeslaTowerTierData(maxTransfer, maxDistance, drain), false);
	}
	
	private void addEnchantmentModule(ItemHolder item, ResourceKey<Enchantment> enchantment, int level)
	{
		this.builder(EIDataMaps.ENCHANTMENT_MODULE).add(item.identifier().location(), new EnchantmentModule(enchantment, level), false);
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
