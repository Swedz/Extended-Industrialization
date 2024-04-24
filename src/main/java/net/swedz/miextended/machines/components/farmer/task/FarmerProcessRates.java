package net.swedz.miextended.machines.components.farmer.task;

import com.google.common.collect.Maps;

import java.util.Map;

public final class FarmerProcessRates
{
	private final Map<FarmerTaskType, Integer> maxOperations = Maps.newHashMap();
	private final Map<FarmerTaskType, Integer> intervals     = Maps.newHashMap();
	
	public FarmerProcessRates with(FarmerTaskType task, int maxOperations, int interval)
	{
		this.maxOperations.put(task, maxOperations);
		this.intervals.put(task, interval);
		return this;
	}
	
	public boolean contains(FarmerTaskType task)
	{
		return maxOperations.containsKey(task);
	}
	
	public int maxOperations(FarmerTaskType task)
	{
		return maxOperations.getOrDefault(task, 0);
	}
	
	public int interval(FarmerTaskType type)
	{
		return intervals.getOrDefault(type, 0);
	}
}
