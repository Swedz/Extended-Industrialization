package net.swedz.extended_industrialization.datamaps;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import net.swedz.extended_industrialization.EI;

public final class EIDataMaps
{
	public static final DataMapType<Fluid, FertilizerPotency> FERTILIZER_POTENCY = DataMapType
			.builder(EI.id("fertilizer_potency"), Registries.FLUID, FertilizerPotency.CODEC)
			.synced(FertilizerPotency.CODEC, true)
			.build();
	
	public static final DataMapType<Potion, PotionBrewing> POTION_BREWING = DataMapType
			.builder(EI.id("potion_brewing"), Registries.POTION, PotionBrewing.CODEC)
			.synced(PotionBrewing.CODEC, true)
			.build();
	
	public static void init(RegisterDataMapTypesEvent event)
	{
		event.register(FERTILIZER_POTENCY);
		event.register(POTION_BREWING);
	}
}
