package net.swedz.miextended.items;

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
	protected void commonRegister()
	{
		MIEItems.include(this);
	}
}
