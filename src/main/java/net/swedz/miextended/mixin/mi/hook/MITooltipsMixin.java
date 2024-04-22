package net.swedz.miextended.mixin.mi.hook;

import aztech.modern_industrialization.MITooltips;
import net.swedz.miextended.mi.hook.MITooltipHook;
import net.swedz.miextended.mi.hook.tracker.MIHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MITooltips.class)
public class MITooltipsMixin
{
	@Inject(
			method = "<clinit>",
			at = @At("TAIL")
	)
	private static void clinit(CallbackInfo callback)
	{
		MIHookTracker.open();
		MITooltipHook.hook();
		MIHookTracker.close();
	}
}
