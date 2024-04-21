package net.swedz.miextended.machines.components.farmer.task;

public final class FarmerTaskOperations
{
	private final int max;
	
	private int operations;
	
	public FarmerTaskOperations(int max)
	{
		this.max = max;
	}
	
	public int max()
	{
		return max;
	}
	
	public boolean operate()
	{
		return ++operations >= max;
	}
	
	public boolean didOperate()
	{
		return operations > 0;
	}
	
	public void reset()
	{
		operations = 0;
	}
}
