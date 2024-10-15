package net.swedz.extended_industrialization.machines.component.itemslot;

import net.swedz.extended_industrialization.EIItems;

public class TeslaTowerUpgradeComponent extends SimpleItemStackComponent
{
	public TeslaTowerUpgradeComponent()
	{
		super("tesla_tower_upgrade_stack");
	}
	
	public boolean isInterdimensional()
	{
		return this.getStack().is(EIItems.TESLA_INTERDIMENSIONAL_UPGRADE.asItem());
	}
}
