package net.swedz.miextended.mixin.mi.hook;

import aztech.modern_industrialization.MIFluids;
import net.swedz.miextended.mi.hook.MIFluidHook;
import net.swedz.miextended.mi.hook.tracker.MIHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MIFluids.class)
public class MIFluidsMixin
{
	@Inject(
			method = "<clinit>",
			at = @At("TAIL")
	)
	private static void clinit(CallbackInfo callback)
	{
		MIHookTracker.open();
		MIFluidHook.hook();
		MIHookTracker.close();
	}
}
