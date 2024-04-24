package net.swedz.extended_industrialization.mixin.mi.hook;

import aztech.modern_industrialization.machines.init.SingleBlockCraftingMachines;
import net.swedz.extended_industrialization.mi.hook.MIMachineHook;
import net.swedz.extended_industrialization.mi.hook.tracker.MIHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SingleBlockCraftingMachines.class)
public class MISingleBlockCraftingMachinesMixin
{
	@Inject(
			method = "init",
			at = @At("TAIL")
	)
	private static void init(CallbackInfo callback)
	{
		MIHookTracker.open();
		MIMachineHook.singleBlockCraftingMachines();
		MIHookTracker.close();
	}
}
