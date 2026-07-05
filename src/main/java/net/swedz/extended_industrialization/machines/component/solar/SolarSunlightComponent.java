package net.swedz.extended_industrialization.machines.component.solar;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.MachineComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.swedz.extended_industrialization.EITags;

public final class SolarSunlightComponent implements MachineComponent.ServerOnly
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
	
	/**
	 * <p>Parabola formula calculated using three intersection points:</p>
	 * <ul>
	 *     <li>(0, 0)</li>
	 *     <li>(12000, 0)</li>
	 *     <li>(1500, 1)</li>
	 * </ul>
	 *
	 * @see <a href="https://www.desmos.com/calculator/rwqs1ch9wx">Desmos Graph</a>
	 */
	public float getSolarEfficiency()
	{
		if(!this.canOperate())
		{
			return 0;
		}
		long time = this.getTime();
		if(time >= 1500 && time <= 10500)
		{
			return 1;
		}
		else
		{
			return (-(time * time) / 15750000f) + ((2f * time) / 2625f);
		}
	}
	
	public boolean hasSunlight()
	{
		var level = machine.getLevel();
		return !level.isRaining() && !level.isThundering() &&
			   level.canSeeSky(machine.getBlockPos().above()) &&
			   !level.dimensionTypeRegistration().is(EITags.DimensionTypes.SOLAR_BLACKLIST);
	}
	
	public boolean canOperate()
	{
		return this.isSolarTime() && this.hasSunlight();
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
	}
}
