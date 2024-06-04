package net.swedz.extended_industrialization.mixin.mi.accessor;

import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(
		value = ConfigurableItemStack.class,
		remap = false
)
public interface ConfigurableItemStackAccessor
{
	@Accessor
	void setAdjustedCapacity(int adjustedCapacity);
}
