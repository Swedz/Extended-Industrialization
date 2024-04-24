package net.swedz.extended_industrialization.api;

import net.minecraft.resources.ResourceLocation;

public record MCIdentifier(ResourceLocation location, String englishName)
{
	public String modId()
	{
		return location.getNamespace();
	}
	
	public String id()
	{
		return location.getPath();
	}
}
