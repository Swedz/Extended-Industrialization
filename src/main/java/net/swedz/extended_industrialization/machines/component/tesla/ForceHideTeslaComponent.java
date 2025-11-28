package net.swedz.extended_industrialization.machines.component.tesla;

import aztech.modern_industrialization.machines.MachineComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public final class ForceHideTeslaComponent implements MachineComponent
{
	private boolean forceHide;
	
	public boolean isHidden()
	{
		return forceHide;
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		if(forceHide)
		{
			tag.putBoolean("force_hide_tesla", forceHide);
		}
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
		forceHide = tag.getBoolean("force_hide_tesla");
	}
}
