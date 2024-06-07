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
	
	public float getSolarEfficiency()
	{
		if(!this.canOperate())
		{
			return 0;
		}
		long time = this.getTime();
		if(time >= 4000 && time <= 8000)
		{
			return 1;
		}
		else if(time < 4000)
		{
			return (-1f / 16000000f) * time * time + (1f / 2000f) * time;
		}
		else if(time > 8000)
		{
			return (-1f / 16000000f) * time * time + (1f / 1000f) * time - 3f;
		}
		else
		{
			throw new IllegalStateException();
		}
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
