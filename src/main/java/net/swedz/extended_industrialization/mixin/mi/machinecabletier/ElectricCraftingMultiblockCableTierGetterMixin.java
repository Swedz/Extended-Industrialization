package net.swedz.extended_industrialization.mixin.mi.machinecabletier;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.blockentities.hatches.EnergyHatch;
import aztech.modern_industrialization.machines.blockentities.multiblocks.AbstractElectricCraftingMultiblockBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import net.swedz.extended_industrialization.mixinduck.CableTierDuck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractElectricCraftingMultiblockBlockEntity.class)
public abstract class ElectricCraftingMultiblockCableTierGetterMixin implements CableTierDuck
{
	@Unique
	private CableTier tier = CableTier.LV;
	
	@Unique
	@Override
	public CableTier getTier()
	{
		return tier;
	}
	
	@Inject(
			method = "onSuccessfulMatch",
			at = @At("HEAD")
	)
	private void onSuccessfulMatch(ShapeMatcher shapeMatcher, CallbackInfo callback)
	{
		for(HatchBlockEntity hatch : shapeMatcher.getMatchedHatches())
		{
			if(hatch instanceof EnergyHatch)
			{
				CableTierDuck energyHatch = (CableTierDuck) hatch;
				if(tier.eu < energyHatch.getTier().eu)
				{
					tier = energyHatch.getTier();
				}
			}
		}
	}
}
