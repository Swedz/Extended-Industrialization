package net.swedz.extended_industrialization.datagen.server.provider.datamaps;

import aztech.modern_industrialization.MI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.datamaps.EIDataMaps;
import net.swedz.extended_industrialization.datamaps.FertilizerPotency;
import net.swedz.extended_industrialization.datamaps.LargeElectricFurnaceTier;
import net.swedz.extended_industrialization.datamaps.PhotovoltaicCell;
import net.swedz.extended_industrialization.datamaps.PotionBrewingCosts;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;
import net.swedz.extended_industrialization.registry.fluids.FluidHolder;
import net.swedz.extended_industrialization.registry.items.EIItems;
import net.swedz.extended_industrialization.registry.items.ItemHolder;

import java.util.Map;

public final class DataMapDatagenProvider extends DataMapProvider
{
	public DataMapDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider());
	}
	
	@Override
	protected void gather()
	{
		this.addFluidFertilizerPotency(EIFluids.MANURE, 25, 100);
		this.addFluidFertilizerPotency(EIFluids.COMPOSTED_MANURE, 25, 50);
		this.addFluidFertilizerPotency(EIFluids.NPK_FERTILIZER, 10, 10);
		
		this.addLargeElectricFurnaceTier(MI.id("cupronickel_coil"), 8, 0.75f);
		this.addLargeElectricFurnaceTier(MI.id("kanthal_coil"), 32, 0.75f);
		
		this.addPhotovoltaicCell(EIItems.LV_PHOTOVOLTAIC_CELL, 32);
		this.addPhotovoltaicCell(EIItems.MV_PHOTOVOLTAIC_CELL, 128);
		this.addPhotovoltaicCell(EIItems.HV_PHOTOVOLTAIC_CELL, 512);
		this.addPhotovoltaicCell(EIItems.EV_PHOTOVOLTAIC_CELL, 2048);
		this.addPhotovoltaicCell(EIItems.PERFECTED_PHOTOVOLTAIC_CELL, 8192);
		
		for(Map.Entry<ResourceKey<Potion>, Potion> entry : BuiltInRegistries.POTION.entrySet())
		{
			this.addPotionBrewing(entry.getKey(), 4, 1000, 1, 10 * 20, 4);
		}
	}
	
	private void addFluidFertilizerPotency(FluidHolder fluid, int tickRate, int mbToConsumePerFertilizerTick)
	{
		this.builder(EIDataMaps.FERTILIZER_POTENCY).add(fluid.identifier().location(), new FertilizerPotency(tickRate, mbToConsumePerFertilizerTick), false);
	}
	
	private void addLargeElectricFurnaceTier(ResourceLocation block, int batchSize, float euCostMultiplier)
	{
		this.builder(EIDataMaps.LARGE_ELECTRIC_FURNACE_TIER).add(block, new LargeElectricFurnaceTier(batchSize, euCostMultiplier), false);
	}
	
	private void addPhotovoltaicCell(ItemHolder item, int euPerTick)
	{
		this.builder(EIDataMaps.PHOTOVOLTAIC_CELL).add(item.identifier().location(), new PhotovoltaicCell(euPerTick), false);
	}
	
	private void addPotionBrewing(ResourceKey<Potion> potion, int bottles, int water, int blazingEssence, int time, int euCost)
	{
		this.builder(EIDataMaps.POTION_BREWING).add(potion, new PotionBrewingCosts(bottles, water, blazingEssence, time, euCost), false);
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}
}
