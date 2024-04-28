package net.swedz.extended_industrialization.mixin.mi.hook;

import aztech.modern_industrialization.machines.init.SingleBlockSpecialMachines;
import net.swedz.extended_industrialization.hook.mi.MIHookDelegator;
import net.swedz.extended_industrialization.hook.mi.tracker.MIHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SingleBlockSpecialMachines.class)
public class HookSingleBlockSpecialMachinesMixin
{
	@Inject(
			method = "init",
			at = @At("TAIL")
	)
	private static void init(CallbackInfo callback)
	{
		MIHookTracker.open();
		MIHookDelegator.machinesSingleBlockSpecial();
		MIHookTracker.close();
	}
}
