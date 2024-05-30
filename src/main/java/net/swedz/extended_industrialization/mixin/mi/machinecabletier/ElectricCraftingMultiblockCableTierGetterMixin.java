package net.swedz.extended_industrialization.mixin.mi.machinecabletier;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.blockentities.hatches.EnergyHatch;
import aztech.modern_industrialization.machines.blockentities.multiblocks.AbstractCraftingMultiblockBlockEntity;
import aztech.modern_industrialization.machines.blockentities.multiblocks.AbstractElectricCraftingMultiblockBlockEntity;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import net.swedz.extended_industrialization.api.CableTierHolder;
import net.swedz.extended_industrialization.machines.guicomponents.exposecabletier.ExposeCableTierGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractElectricCraftingMultiblockBlockEntity.class)
public abstract class ElectricCraftingMultiblockCableTierGetterMixin extends AbstractCraftingMultiblockBlockEntity implements CableTierHolder
{
	public ElectricCraftingMultiblockCableTierGetterMixin(BEP bep, String name, OrientationComponent.Params orientationParams, ShapeTemplate[] shapeTemplates)
	{
		super(bep, name, orientationParams, shapeTemplates);
	}
	
	@Unique
	private CableTier cableTier = CableTier.LV;
	
	@Unique
	@Override
	public CableTier getCableTier()
	{
		return cableTier;
	}
	
	@Inject(
			method = "<init>",
			at = @At("RETURN")
	)
	private void init(BEP bep, String name, OrientationComponent.Params orientationParams, ShapeTemplate[] shapeTemplates, CallbackInfo callback)
	{
		this.registerGuiComponent(new ExposeCableTierGui.Server(this));
	}
	
	@Inject(
			method = "onSuccessfulMatch",
			at = @At("HEAD")
	)
	private void onSuccessfulMatch(ShapeMatcher shapeMatcher, CallbackInfo callback)
	{
		cableTier = CableTier.LV;
		for(HatchBlockEntity hatch : shapeMatcher.getMatchedHatches())
		{
			if(hatch instanceof EnergyHatch)
			{
				CableTierHolder energyHatch = (CableTierHolder) hatch;
				if(cableTier.eu < energyHatch.getCableTier().eu)
				{
					cableTier = energyHatch.getCableTier();
				}
			}
		}
	}
}
