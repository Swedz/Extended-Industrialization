package net.swedz.extended_industrialization.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.alchemy.Potion;

public record PotionBrewing(int bottles, int water, int blazingEssence, int time, int euCost, int totalEuCost)
{
	public static final Codec<PotionBrewing> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					ExtraCodecs.POSITIVE_INT.fieldOf("bottles").forGetter(PotionBrewing::bottles),
					ExtraCodecs.POSITIVE_INT.fieldOf("water").forGetter(PotionBrewing::water),
					ExtraCodecs.POSITIVE_INT.fieldOf("blazing_essence").forGetter(PotionBrewing::blazingEssence),
					ExtraCodecs.POSITIVE_INT.fieldOf("time").forGetter(PotionBrewing::time),
					ExtraCodecs.POSITIVE_INT.fieldOf("eu").forGetter(PotionBrewing::euCost)
			)
			.apply(instance, PotionBrewing::new)
	);
	
	@SuppressWarnings("deprecation")
	public static PotionBrewing getFor(Potion potion)
	{
		return potion.builtInRegistryHolder().getData(EIDataMaps.POTION_BREWING);
	}
	
	public PotionBrewing(int bottles, int water, int blazingEssence, int time, int euCost)
	{
		this(bottles, water, blazingEssence, time, euCost, time * euCost);
	}
}
