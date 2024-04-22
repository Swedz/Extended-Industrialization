package net.swedz.miextended.datamaps;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.swedz.miextended.MIExtended;

public final class MIEDataMaps
{
	public static final DataMapType<Fluid, FertilizerPotency> FERTILIZER_POTENCY = DataMapType
			.builder(MIExtended.id("fertilizer_potency"), Registries.FLUID, FertilizerPotency.CODEC)
			.synced(FertilizerPotency.CODEC, true)
			.build();
}
