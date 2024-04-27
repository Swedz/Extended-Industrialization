package net.swedz.extended_industrialization.mixin.mi.hook;

import aztech.modern_industrialization.compat.viewer.abstraction.ViewerCategory;
import aztech.modern_industrialization.compat.viewer.usage.ViewerSetup;
import net.swedz.extended_industrialization.hook.mi.MIViewerSetupHook;
import net.swedz.extended_industrialization.hook.mi.tracker.MIHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ViewerSetup.class)
public class HookViewerSetupMixin
{
	@Inject(
			method = "setup",
			at = @At("RETURN"),
			locals = LocalCapture.CAPTURE_FAILHARD,
			remap = false
	)
	private static void clinit(CallbackInfoReturnable<List<ViewerCategory<?>>> callback, List<ViewerCategory<?>> registry)
	{
		MIHookTracker.open();
		MIViewerSetupHook.hook(registry);
		MIHookTracker.close();
	}
}
