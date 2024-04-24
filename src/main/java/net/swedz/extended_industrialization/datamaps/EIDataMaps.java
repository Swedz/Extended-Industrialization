package net.swedz.extended_industrialization.datamaps;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.swedz.extended_industrialization.EI;

public final class EIDataMaps
{
	public static final DataMapType<Fluid, FertilizerPotency> FERTILIZER_POTENCY = DataMapType
			.builder(EI.id("fertilizer_potency"), Registries.FLUID, FertilizerPotency.CODEC)
			.synced(FertilizerPotency.CODEC, true)
			.build();
}
