package net.swedz.extended_industrialization.api;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.helper.SteamHelper;
import aztech.modern_industrialization.util.Simulation;

public abstract class EuConsumerBehavior
{
	public static EuConsumerBehavior steam(MachineBlockEntity machine)
	{
		return new Steam(machine);
	}
	
	public static EuConsumerBehavior electric(MachineBlockEntity machine, EnergyComponent energy, RedstoneControlComponent redstone)
	{
		return new Electric(machine, energy, redstone);
	}
	
	protected final MachineBlockEntity machine;
	
	private EuConsumerBehavior(MachineBlockEntity machine)
	{
		this.machine = machine;
	}
	
	public abstract long consumeEu(long max, Simulation simulation);
	
	private static final class Steam extends EuConsumerBehavior
	{
		private Steam(MachineBlockEntity machine)
		{
			super(machine);
		}
		
		@Override
		public long consumeEu(long max, Simulation simulation)
		{
			return SteamHelper.consumeSteamEu(machine.getInventory().getFluidStacks(), max, simulation);
		}
	}
	
	private static final class Electric extends EuConsumerBehavior
	{
		private final EnergyComponent          energy;
		private final RedstoneControlComponent redstone;
		
		private Electric(MachineBlockEntity machine, EnergyComponent energy, RedstoneControlComponent redstone)
		{
			super(machine);
			this.energy = energy;
			this.redstone = redstone;
		}
		
		@Override
		public long consumeEu(long max, Simulation simulation)
		{
			return redstone.doAllowNormalOperation(machine) ? energy.consumeEu(max, simulation) : 0;
		}
	}
}
