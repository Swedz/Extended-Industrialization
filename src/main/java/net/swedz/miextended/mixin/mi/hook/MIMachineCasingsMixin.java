package net.swedz.miextended.mixin.mi.hook;

import aztech.modern_industrialization.machines.models.MachineCasings;
import net.swedz.miextended.mi.hook.MIMachineHook;
import net.swedz.miextended.mi.hook.tracker.MIHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MachineCasings.class)
public class MIMachineCasingsMixin
{
	@Inject(
			method = "<clinit>",
			at = @At("TAIL")
	)
	private static void init(CallbackInfo callback)
	{
		MIHookTracker.open();
		MIMachineHook.machineCasings();
		MIHookTracker.close();
	}
}
