package net.swedz.extended_industrialization.machines.component.tesla;

import aztech.modern_industrialization.api.energy.CableTier;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower.TeslaTowerTier;

public interface TeslaTransferLimits
{
	static TeslaTransferLimits of(CableTier cableTier, TeslaTowerTier towerTier)
	{
		return of(cableTier, towerTier.maxTransfer(), towerTier.maxDistance(), towerTier.drain());
	}
	
	static TeslaTransferLimits of(CableTier cableTier, long maxTransfer, int maxDistance, long passiveDrain)
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
				return maxTransfer;
			}
			
			@Override
			public int getMaxDistance()
			{
				return maxDistance;
			}
			
			@Override
			public long getPassiveDrain()
			{
				return passiveDrain;
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
