package net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower;

import aztech.modern_industrialization.api.energy.CableTier;
import net.minecraft.resources.ResourceLocation;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.tieredshapes.MultiblockTier;

public record TeslaTowerTier(ResourceLocation blockId, CableTier cableTier) implements MultiblockTier
{
	public String getTranslationKey()
	{
		return "teslatower_tier.%s.%s.%s".formatted(EI.ID, blockId.getNamespace(), blockId.getPath());
	}
}
