package net.swedz.miextended.machines.components;

import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.components.TemperatureComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public final class SolarBurningComponent implements IComponent
{
	private final TemperatureComponent temperature;
	
	private final int warmupSpeed, cooldownSpeed;
	
	private boolean working;
	
	public SolarBurningComponent(TemperatureComponent temperature, int warmupSpeed, int cooldownSpeed)
	{
		this.temperature = temperature;
		this.warmupSpeed = warmupSpeed;
		this.cooldownSpeed = cooldownSpeed;
	}
	
	public boolean isWorking()
	{
		return working;
	}
	
	public void tick(Level level, BlockPos blockPos)
	{
		working = level.isDay() && level.canSeeSky(blockPos.above());
		
		if(working)
		{
			temperature.increaseTemperature(warmupSpeed);
		}
		else
		{
			temperature.decreaseTemperature(cooldownSpeed);
		}
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
