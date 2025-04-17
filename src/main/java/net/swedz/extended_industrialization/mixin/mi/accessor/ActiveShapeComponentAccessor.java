package net.swedz.extended_industrialization.mixin.mi.accessor;

import aztech.modern_industrialization.machines.components.ActiveShapeComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(
		value = ActiveShapeComponent.class,
		remap = false
)
public interface ActiveShapeComponentAccessor
{
	@Accessor
	void setActiveShape(int activeShape);
}
