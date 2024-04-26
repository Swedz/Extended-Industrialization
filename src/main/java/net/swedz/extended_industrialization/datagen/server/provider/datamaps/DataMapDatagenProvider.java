package net.swedz.extended_industrialization.datagen.server.provider.datamaps;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.datamaps.EIDataMaps;
import net.swedz.extended_industrialization.datamaps.FertilizerPotency;
import net.swedz.extended_industrialization.datamaps.PotionBrewingCosts;
import net.swedz.extended_industrialization.registry.fluids.EIFluids;
import net.swedz.extended_industrialization.registry.fluids.FluidHolder;

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
		
		for(Map.Entry<ResourceKey<Potion>, Potion> entry : BuiltInRegistries.POTION.entrySet())
		{
			this.addPotionBrewing(entry.getKey(), 4, 1000, 1, 10 * 20, 4);
		}
	}
	
	private void addFluidFertilizerPotency(FluidHolder fluid, int tickRate, int mbToConsumePerFertilizerTick)
	{
		this.builder(EIDataMaps.FERTILIZER_POTENCY).add(fluid.identifier().location(), new FertilizerPotency(tickRate, mbToConsumePerFertilizerTick), false);
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
