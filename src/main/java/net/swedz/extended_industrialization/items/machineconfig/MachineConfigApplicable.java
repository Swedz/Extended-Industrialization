package net.swedz.extended_industrialization.items.machineconfig;

import aztech.modern_industrialization.util.Simulation;

public interface MachineConfigApplicable<T>
{
	boolean matches(T target);
	
	boolean apply(T target, Simulation simulation);
}
