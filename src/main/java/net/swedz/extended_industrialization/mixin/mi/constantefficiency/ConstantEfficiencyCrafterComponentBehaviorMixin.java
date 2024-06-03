package net.swedz.extended_industrialization.mixin.mi.constantefficiency;

import aztech.modern_industrialization.machines.components.CrafterComponent;
import net.swedz.extended_industrialization.api.ExtendedCableTier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CrafterComponent.Behavior.class)
public interface ConstantEfficiencyCrafterComponentBehaviorMixin
{
	@Redirect(
			method = "banRecipe",
			at = @At(
					value = "INVOKE",
					target = "Laztech/modern_industrialization/machines/components/CrafterComponent$Behavior;getMaxRecipeEu()J"
			)
	)
	private long getMaxRecipeEu(CrafterComponent.Behavior behavior)
	{
		return ExtendedCableTier.getActualMaxRecipeEu(behavior, behavior);
	}
}
