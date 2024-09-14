package net.swedz.extended_industrialization.machines.component.chainer.handler;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import com.google.common.collect.Lists;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerLinks;
import net.swedz.extended_industrialization.machines.component.chainer.wrapper.InventoryWrapper;

import java.util.Collections;
import java.util.List;

public final class ChainerEnergyHandler extends ChainerHandler<MIEnergyStorage, InventoryWrapper<MIEnergyStorage>> implements MIEnergyStorage
{
	private final boolean insertable;
	
	public ChainerEnergyHandler(ChainerLinks chainerLinks, boolean insertable)
	{
		super(chainerLinks);
		this.insertable = insertable;
	}
	
	@Override
	public void invalidate()
	{
		List<InventoryWrapper<MIEnergyStorage>> wrappers = Lists.newArrayList();
		
		for(MIEnergyStorage handler : this.getMachineLinks().energyHandlers())
		{
			wrappers.add(new InventoryWrapper<>(handler));
		}
		
		this.wrappers = Collections.unmodifiableList(wrappers);
	}
	
	@Override
	public long receive(long maxReceive, boolean simulate)
	{
		if(!chainerLinks.doesAllowOperation() || !insertable)
		{
			return 0;
		}
		long amountReceived = 0;
		for(int i = 0; i < wrappers.size(); i++)
		{
			var wrapper = wrappers.get(i);
			int remainingStorages = wrappers.size() - i;
			long remainingAmountToReceive = maxReceive - amountReceived;
			long amountToReceive = remainingAmountToReceive / remainingStorages;
			amountReceived += wrapper.handler().receive(amountToReceive, simulate);
		}
		return amountReceived;
	}
	
	@Override
	public long extract(long maxExtract, boolean simulate)
	{
		if(!chainerLinks.doesAllowOperation() || insertable)
		{
			return 0;
		}
		long amountExtracted = 0;
		for(var wrapper : wrappers)
		{
			long remainingAmountToExtract = maxExtract - amountExtracted;
			amountExtracted += wrapper.handler().extract(remainingAmountToExtract, simulate);
			if(amountExtracted == maxExtract)
			{
				break;
			}
		}
		return amountExtracted;
	}
	
	@Override
	public long getAmount()
	{
		return wrappers.stream().mapToLong((w) -> w.handler().getAmount()).sum();
	}
	
	@Override
	public long getCapacity()
	{
		return wrappers.stream().mapToLong((w) -> w.handler().getCapacity()).sum();
	}
	
	@Override
	public boolean canExtract()
	{
		return !insertable;
	}
	
	@Override
	public boolean canReceive()
	{
		return insertable;
	}
	
	@Override
	public boolean canConnect(CableTier cableTier)
	{
		return true;
	}
}
