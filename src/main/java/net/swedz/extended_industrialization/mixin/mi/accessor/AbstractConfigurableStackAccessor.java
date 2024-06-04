package net.swedz.extended_industrialization.mixin.mi.accessor;

import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(
		value = AbstractConfigurableStack.class,
		remap = false
)
public interface AbstractConfigurableStackAccessor
{
	@Invoker("notifyListeners")
	void invokeNotifyListeners();
}
