package net.swedz.extended_industrialization.machines.components.solar;

import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public final class SolarSunlightComponent implements IComponent.ServerOnly
{
	private final MachineBlockEntity machine;
	
	public SolarSunlightComponent(MachineBlockEntity machine)
	{
		this.machine = machine;
	}
	
	public long getTime()
	{
		return machine.getLevel().getDayTime() % 24000;
	}
	
	public boolean isSolarTime()
	{
		long time = this.getTime();
		return time >= 0 && time <= 12000;
	}
	
	public float getClosenessToNoon()
	{
		if(!this.isSolarTime())
		{
			return 0f;
		}
		long time = this.getTime();
		long timeFromNoon = Math.abs(6000 - time);
		return 1 - (timeFromNoon / 6000f);
	}
	
	public boolean hasSunlight()
	{
		Level level = machine.getLevel();
		return !level.isRaining() && !level.isThundering() &&
			   level.canSeeSky(machine.getBlockPos().above());
	}
	
	public boolean canOperate()
	{
		return this.isSolarTime() && this.hasSunlight();
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
	}
	
	@Override
	public void readNbt(CompoundTag tag, boolean isUpgradingMachine)
	{
	}
}
