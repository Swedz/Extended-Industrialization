package net.swedz.extended_industrialization.machines.component.chainer.handler;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerLinks;
import net.swedz.extended_industrialization.machines.component.chainer.wrapper.InventoryWrapper;
import net.swedz.tesseract.neoforge.helper.TransferHelper;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class ChainerEnergyHandler extends ChainerHandler<MIEnergyStorage, InventoryWrapper<MIEnergyStorage>> implements MIEnergyStorage
{
	private final Supplier<CableTier> cableTier;
	private final boolean             insertable;
	
	public ChainerEnergyHandler(ChainerLinks chainerLinks, Supplier<CableTier> cableTier, boolean insertable)
	{
		super(chainerLinks);
		this.cableTier = cableTier;
		this.insertable = insertable;
	}
	
	@Override
	public void invalidate()
	{
		List<InventoryWrapper<MIEnergyStorage>> wrappers = Lists.newArrayList();
		
		for(MIEnergyStorage handler : this.getMachineLinks().energyHandlers())
		{
			if(handler.canConnect(cableTier.get()))
			{
				wrappers.add(new InventoryWrapper<>(handler));
			}
		}
		
		this.wrappers = Collections.unmodifiableList(wrappers);
		this.wrappersSlotMap = Maps.newConcurrentMap();
	}
	
	@Override
	public long receive(long maxReceive, boolean simulate)
	{
		if(!chainerLinks.doesAllowOperation() || !insertable)
		{
			return 0;
		}
		return TransferHelper.distributeLong((wrapper, amount, opSimulate) -> wrapper.handler().receive(amount, opSimulate), wrappers, maxReceive, simulate);
	}
	
	@Override
	public long extract(long maxExtract, boolean simulate)
	{
		if(!chainerLinks.doesAllowOperation() || insertable)
		{
			return 0;
		}
		return TransferHelper.distributeLong((wrapper, amount, opSimulate) -> wrapper.handler().extract(amount, opSimulate), wrappers, maxExtract, simulate);
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
	public boolean canConnect(CableTier other)
	{
		return cableTier.get() == other;
	}
}
