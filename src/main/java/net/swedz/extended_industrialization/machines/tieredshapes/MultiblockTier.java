package net.swedz.extended_industrialization.machines.tieredshapes;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface MultiblockTier
{
	ResourceLocation blockId();
	
	String getTranslationKey();
	
	default Component getDisplayName()
	{
		return Component.translatable(this.getTranslationKey());
	}
}
