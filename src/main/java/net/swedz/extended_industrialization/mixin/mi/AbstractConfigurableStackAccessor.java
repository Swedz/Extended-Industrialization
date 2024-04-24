package net.swedz.extended_industrialization.mixin.mi;

import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractConfigurableStack.class)
public interface AbstractConfigurableStackAccessor
{
	@Invoker("notifyListeners")
	void invokeNotifyListeners();
}
