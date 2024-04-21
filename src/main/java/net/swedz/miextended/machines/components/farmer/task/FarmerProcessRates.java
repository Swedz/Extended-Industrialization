package net.swedz.miextended.machines.components.farmer.task;

import com.google.common.collect.Maps;

import java.util.Map;

public final class FarmerProcessRates
{
	private final int maxOperations;
	
	private final Map<FarmerTaskType, Integer> intervals = Maps.newHashMap();
	
	public FarmerProcessRates(int maxOperations)
	{
		this.maxOperations = maxOperations;
	}
	
	public int maxOperations()
	{
		return maxOperations;
	}
	
	public FarmerProcessRates withInterval(FarmerTaskType task, int interval)
	{
		intervals.put(task, interval);
		return this;
	}
	
	public int interval(FarmerTaskType type)
	{
		return intervals.getOrDefault(type, 1);
	}
}
