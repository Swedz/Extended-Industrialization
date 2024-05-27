package net.swedz.extended_industrialization.mixin.mi.machinecabletier;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.blockentities.ElectricCraftingMachineBlockEntity;
import aztech.modern_industrialization.machines.components.CasingComponent;
import net.swedz.extended_industrialization.mixinduck.CableTierDuck;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ElectricCraftingMachineBlockEntity.class)
public class ElectricCraftingSingleblockCableTierGetterMixin implements CableTierDuck
{
	@Shadow
	@Final
	private CasingComponent casing;
	
	@Unique
	@Override
	public CableTier getTier()
	{
		return ((CableTierDuck) casing).getTier();
	}
}
