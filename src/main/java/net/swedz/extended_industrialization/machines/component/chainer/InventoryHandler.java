package net.swedz.extended_industrialization.machines.component.chainer;

public record InventoryHandler<H>(int slotOffset, int slots, H handler)
{
	public int slotStart()
	{
		return slotOffset;
	}
	
	public int slotEnd()
	{
		return slots + slotOffset - 1;
	}
	
	public boolean contains(int globalSlot)
	{
		return globalSlot >= this.slotStart() &&
			   globalSlot <= this.slotEnd();
	}
	
	public int toGlobalSlot(int localSlot)
	{
		return localSlot + slotOffset;
	}
	
	public int toLocalSlot(int globalSlot)
	{
		return globalSlot - slotOffset;
	}
}
