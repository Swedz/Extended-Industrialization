package net.swedz.extended_industrialization.mixin.mi.machinecabletier;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.blockentities.hatches.EnergyHatch;
import net.swedz.extended_industrialization.api.CableTierHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnergyHatch.class)
public class EnergyHatchCableTierGetterMixin implements CableTierHolder
{
	@Unique
	private CableTier tier = CableTier.LV;
	
	@Unique
	@Override
	public CableTier getCableTier()
	{
		return tier;
	}
	
	@Inject(
			method = "<init>",
			at = @At("RETURN")
	)
	private void init(BEP bep, String name, boolean input, CableTier tier, CallbackInfo callback)
	{
		this.tier = tier;
	}
}
