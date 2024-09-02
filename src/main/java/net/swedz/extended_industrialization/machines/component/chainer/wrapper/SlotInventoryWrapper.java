package net.swedz.extended_industrialization.machines.component.chainer.wrapper;

public class SlotInventoryWrapper<H> extends InventoryWrapper<H>
{
	protected final int slotOffset;
	protected final int slots;
	
	public SlotInventoryWrapper(H handler, int slotOffset, int slots)
	{
		super(handler);
		this.slotOffset = slotOffset;
		this.slots = slots;
	}
	
	public int slotOffset()
	{
		return slotOffset;
	}
	
	public int slots()
	{
		return slots;
	}
	
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
