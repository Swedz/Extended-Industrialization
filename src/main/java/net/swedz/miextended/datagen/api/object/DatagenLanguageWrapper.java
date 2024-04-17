package net.swedz.miextended.datagen.api.object;

import net.swedz.miextended.datagen.api.DatagenOutputTarget;
import net.swedz.miextended.datagen.api.DatagenProvider;

public class DatagenLanguageWrapper extends DatagenJsonObjectWrapper
{
	public DatagenLanguageWrapper(DatagenProvider provider)
	{
		super(provider, DatagenOutputTarget.RESOURCE_PACK, (p) -> p.resolve("lang").resolve("en_us.json"));
	}
	
	public void add(String key, String value)
	{
		this.get().addProperty(key, value);
	}
	
	public void addBlock(String key, String value)
	{
		this.add("block.%s.%s".formatted(provider.modId(), key), value);
	}
	
	public void addRecipeCategory(String key, String value)
	{
		this.add("rei_categories.%s.%s".formatted(provider.modId(), key), value);
	}
}
