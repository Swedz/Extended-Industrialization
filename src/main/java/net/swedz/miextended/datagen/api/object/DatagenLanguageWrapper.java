package net.swedz.miextended.datagen.api.object;

import net.swedz.miextended.datagen.api.DatagenOutputTarget;
import net.swedz.miextended.datagen.api.DatagenProvider;

public class DatagenLanguageWrapper extends DatagenJsonObjectWrapper
{
	public DatagenLanguageWrapper(DatagenProvider provider)
	{
		super(provider, DatagenOutputTarget.RESOURCE_PACK, (p) -> p.resolve("lang").resolve("en_us.json"));
	}
	
	private String keyItem(String key)
	{
		return "item.%s.%s".formatted(provider.modId(), key);
	}
	
	private String keyBlock(String key)
	{
		return "block.%s.%s".formatted(provider.modId(), key);
	}
	
	public void add(String key, String value)
	{
		this.get().addProperty(key, value);
	}
	
	public void addItem(String key, String value)
	{
		if(this.get().has(this.keyBlock(key)))
		{
			return;
		}
		this.add(this.keyItem(key), value);
	}
	
	public void addBlock(String key, String value)
	{
		this.get().remove(this.keyItem(key));
		this.add(this.keyBlock(key), value);
	}
	
	public void addRecipeCategory(String key, String value)
	{
		this.add("rei_categories.%s.%s".formatted(provider.modId(), key), value);
	}
	
	public void addFluidDefinition(String key, String value)
	{
		this.add("block.%s.%s".formatted(provider.modId(), key), value);
	}
}
