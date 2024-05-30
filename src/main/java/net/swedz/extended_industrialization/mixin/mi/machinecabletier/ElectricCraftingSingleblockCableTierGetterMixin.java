package net.swedz.extended_industrialization.mixin.mi.machinecabletier;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.blockentities.AbstractCraftingMachineBlockEntity;
import aztech.modern_industrialization.machines.blockentities.ElectricCraftingMachineBlockEntity;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.MachineInventoryComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.guicomponents.ProgressBar;
import aztech.modern_industrialization.machines.guicomponents.RecipeEfficiencyBar;
import aztech.modern_industrialization.machines.init.MachineTier;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.swedz.extended_industrialization.api.CableTierHolder;
import net.swedz.extended_industrialization.machines.guicomponents.exposecabletier.ExposeCableTierGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElectricCraftingMachineBlockEntity.class)
public abstract class ElectricCraftingSingleblockCableTierGetterMixin extends AbstractCraftingMachineBlockEntity implements CableTierHolder
{
	public ElectricCraftingSingleblockCableTierGetterMixin(BEP bep, MachineRecipeType recipeType, MachineInventoryComponent inventory, MachineGuiParameters guiParams, ProgressBar.Parameters progressBarParams, MachineTier tier)
	{
		super(bep, recipeType, inventory, guiParams, progressBarParams, tier);
	}
	
	@Shadow
	@Final
	private CasingComponent casing;
	
	@Unique
	@Override
	public CableTier getCableTier()
	{
		return ((CableTierHolder) casing).getCableTier();
	}
	
	@Inject(
			method = "<init>",
			at = @At("RETURN")
	)
	private void init(BEP bep, MachineRecipeType recipeType, MachineInventoryComponent inventory, MachineGuiParameters guiParams, EnergyBar.Parameters energyBarParams, ProgressBar.Parameters progressBarParams, RecipeEfficiencyBar.Parameters efficiencyBarParams, MachineTier tier, long euCapacity, CallbackInfo callback)
	{
		this.registerGuiComponent(new ExposeCableTierGui.Server(this));
	}
}
