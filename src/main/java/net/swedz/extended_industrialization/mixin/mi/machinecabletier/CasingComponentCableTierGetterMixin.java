package net.swedz.extended_industrialization.mixin.mi.machinecabletier;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.components.CasingComponent;
import net.swedz.extended_industrialization.mixinduck.CableTierDuck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CasingComponent.class)
public class CasingComponentCableTierGetterMixin implements CableTierDuck
{
	@Shadow
	private CableTier currentTier;
	
	@Unique
	@Override
	public CableTier getTier()
	{
		return currentTier;
	}
}
