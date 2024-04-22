package net.swedz.miextended.items;

import com.google.common.collect.Sets;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.api.item.ItemWrapper;

import java.util.Collections;
import java.util.Set;

public final class MIEItemWrapper<I extends Item> extends ItemWrapper<I, MIEItemProperties, MIEItemWrapper<I>>
{
	public MIEItemWrapper()
	{
		super(MIExtended.ID);
	}
	
	private final Set<TagKey<Item>> tags = Sets.newHashSet();
	
	public Set<TagKey<Item>> getTags()
	{
		return Set.copyOf(tags);
	}
	
	@SafeVarargs
	public final MIEItemWrapper<I> tag(TagKey<Item>... tags)
	{
		Collections.addAll(this.tags, tags);
		return this;
	}
	
	@Override
	protected MIEItemProperties defaultProperties()
	{
		return new MIEItemProperties();
	}
	
	@Override
	protected DeferredItem<I> commonRegister()
	{
		return MIEItems.include(this);
	}
}
