package net.swedz.extended_industrialization.mixin.mi.hook;

import aztech.modern_industrialization.machines.models.MachineCasings;
import net.swedz.extended_industrialization.hook.mi.MIHookDelegator;
import net.swedz.extended_industrialization.hook.mi.tracker.MIHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
		value = MachineCasings.class,
		remap = false
)
public class HookMachineCasingsMixin
{
	@Inject(
			method = "<clinit>",
			at = @At("TAIL")
	)
	private static void init(CallbackInfo callback)
	{
		MIHookTracker.open();
		MIHookDelegator.machineCasings();
		MIHookTracker.close();
	}
}
