package net.swedz.extended_industrialization.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.alchemy.Potion;
import net.swedz.extended_industrialization.EIDataMaps;

public record PotionBrewingCosts(int bottles, int water, int blazingEssence, int time, int euCost, int totalEuCost)
{
	public static final Codec<PotionBrewingCosts> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					ExtraCodecs.POSITIVE_INT.fieldOf("bottles").forGetter(PotionBrewingCosts::bottles),
					ExtraCodecs.POSITIVE_INT.fieldOf("water").forGetter(PotionBrewingCosts::water),
					ExtraCodecs.POSITIVE_INT.fieldOf("blazing_essence").forGetter(PotionBrewingCosts::blazingEssence),
					ExtraCodecs.POSITIVE_INT.fieldOf("time").forGetter(PotionBrewingCosts::time),
					ExtraCodecs.POSITIVE_INT.fieldOf("eu").forGetter(PotionBrewingCosts::euCost)
			)
			.apply(instance, PotionBrewingCosts::new)
	);
	
	@SuppressWarnings("deprecation")
	public static PotionBrewingCosts getFor(Potion potion)
	{
		return potion.builtInRegistryHolder().getData(EIDataMaps.POTION_BREWING);
	}
	
	public PotionBrewingCosts(int bottles, int water, int blazingEssence, int time, int euCost)
	{
		this(bottles, water, blazingEssence, time, euCost, time * euCost);
	}
}
