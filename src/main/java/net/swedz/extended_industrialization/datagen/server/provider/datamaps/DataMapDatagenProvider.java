package net.swedz.extended_industrialization.datagen.server.provider.datamaps;

import aztech.modern_industrialization.api.energy.CableTier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EIDataMaps;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.datamap.EnchantmentModule;
import net.swedz.extended_industrialization.datamap.FarmerSimpleTallCropSize;
import net.swedz.extended_industrialization.datamap.FertilizerPotency;
import net.swedz.extended_industrialization.datamap.LargeElectricFurnaceTier;
import net.swedz.extended_industrialization.datamap.TeslaTowerTierData;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.LargeElectricFurnaceBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower.TeslaTowerBlockEntity;
import net.swedz.tesseract.neoforge.registry.holder.FluidHolder;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.Map;

public final class DataMapDatagenProvider extends DataMapProvider
{
	public DataMapDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider());
	}
	
	@Override
	protected void gather(HolderLookup.Provider registries)
	{
		this.addFarmerSimpleTallCropSize(Blocks.SUGAR_CANE, 3);
		this.addFarmerSimpleTallCropSize(Blocks.CACTUS, 3);
		
		this.addFluidFertilizerPotency(EIFluids.MANURE, 25, 300);
		this.addFluidFertilizerPotency(EIFluids.COMPOSTED_MANURE, 25, 150);
		this.addFluidFertilizerPotency(EIFluids.NPK_FERTILIZER, 10, 30);
		
		for(var tier : LargeElectricFurnaceBlockEntity.DEFAULT_TIERS)
		{
			this.builder(EIDataMaps.LARGE_ELECTRIC_FURNACE_TIER).add(tier.blockId(), new LargeElectricFurnaceTier(tier.batchSize(), tier.euCostMultiplier()), false);
		}
		
		for(var tier : TeslaTowerBlockEntity.DEFAULT_TIERS)
		{
			this.builder(EIDataMaps.TESLA_TOWER_TIER).add(tier.blockId(), new TeslaTowerTierData(tier.maxTransfer(), tier.maxDistance(), tier.drain()), false);
		}
		
		this.addEnchantmentModule(EIItems.SILK_TOUCH_MODULE, Enchantments.SILK_TOUCH, 1, 16);
		this.addEnchantmentModule(
				EIItems.LOOTING_MODULE, Enchantments.LOOTING,
				1, 32,
				Map.of(
						CableTier.MV, new EnchantmentModule.Value(3, 32 * 4),
						CableTier.HV, new EnchantmentModule.Value(5, 32 * 4 * 4),
						CableTier.EV, new EnchantmentModule.Value(7, 32 * 4 * 4 * 4 * 4),
						CableTier.SUPERCONDUCTOR, new EnchantmentModule.Value(10, 32 * 4 * 4 * 4 * 4 * 4)
				)
		);
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
	
	private void addEnchantmentModule(ItemHolder item, ResourceKey<Enchantment> enchantment, int level, long euCost, Map<CableTier, EnchantmentModule.Value> values)
	{
		this.builder(EIDataMaps.ENCHANTMENT_MODULE).add(item.identifier().location(), new EnchantmentModule(enchantment, new EnchantmentModule.Value(level, euCost), values), false);
	}
	
	private void addEnchantmentModule(ItemHolder item, ResourceKey<Enchantment> enchantment, int level, long euCost)
	{
		this.addEnchantmentModule(item, enchantment, level, euCost, Map.of());
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
