package net.swedz.miextended.datagen.server.provider.datamaps;

import aztech.modern_industrialization.definition.FluidDefinition;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.miextended.datamaps.FertilizerPotency;
import net.swedz.miextended.datamaps.MIEDataMaps;
import net.swedz.miextended.fluids.MIEFluids;

public final class DataMapDatagenProvider extends DataMapProvider
{
	public DataMapDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider());
	}
	
	@Override
	protected void gather()
	{
		this.addFluidFertilizerPotency(MIEFluids.MANURE, 25, 100);
		this.addFluidFertilizerPotency(MIEFluids.COMPOSTED_MANURE, 25, 50);
		this.addFluidFertilizerPotency(MIEFluids.NPK_FERTILIZER, 10, 10);
	}
	
	private void addFluidFertilizerPotency(FluidDefinition fluidDefinition, int tickRate, int mbToConsumePerFertilizerTick)
	{
		builder(MIEDataMaps.FERTILIZER_POTENCY).add(fluidDefinition.getId(), new FertilizerPotency(tickRate, mbToConsumePerFertilizerTick), false);
	}
}
