package net.swedz.miextended.items;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.swedz.miextended.MIExtended;
import net.swedz.miextended.api.item.ItemWrapper;

public class MIEItemWrapper extends ItemWrapper<MIEItemProperties, MIEItemWrapper>
{
	public MIEItemWrapper()
	{
		super(MIExtended.ID);
	}
	
	@Override
	protected MIEItemProperties defaultProperties()
	{
		return new MIEItemProperties();
	}
	
	@Override
	protected DeferredItem<Item> commonRegister()
	{
		return MIEItems.include(this);
	}
}
