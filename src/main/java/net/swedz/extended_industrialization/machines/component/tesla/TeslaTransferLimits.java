package net.swedz.extended_industrialization.machines.component.tesla;

import aztech.modern_industrialization.api.energy.CableTier;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower.TeslaTowerTier;

public interface TeslaTransferLimits
{
	static TeslaTransferLimits of(CableTier cableTier, TeslaTowerTier towerTier)
	{
		return new TeslaTransferLimits()
		{
			@Override
			public CableTier getCableTier()
			{
				return cableTier;
			}
			
			@Override
			public long getMaxTransfer()
			{
				return towerTier.maxTransfer();
			}
			
			@Override
			public int getMaxDistance()
			{
				return towerTier.maxDistance();
			}
			
			@Override
			public long getPassiveDrain()
			{
				return towerTier.drain();
			}
		};
	}
	
	CableTier getCableTier();
	
	long getMaxTransfer();
	
	int getMaxDistance();
	
	long getPassiveDrain();
	
	interface Delegate extends TeslaTransferLimits
	{
		TeslaTransferLimits getDelegateTransferLimits();
		
		@Override
		default CableTier getCableTier()
		{
			return this.getDelegateTransferLimits().getCableTier();
		}
		
		@Override
		default long getMaxTransfer()
		{
			return this.getDelegateTransferLimits().getMaxTransfer();
		}
		
		@Override
		default int getMaxDistance()
		{
			return this.getDelegateTransferLimits().getMaxDistance();
		}
		
		@Override
		default long getPassiveDrain()
		{
			return this.getDelegateTransferLimits().getPassiveDrain();
		}
	}
}
