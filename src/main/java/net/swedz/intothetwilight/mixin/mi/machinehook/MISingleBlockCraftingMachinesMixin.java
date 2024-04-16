package net.swedz.intothetwilight.mixin.mi.machinehook;

import aztech.modern_industrialization.machines.init.SingleBlockCraftingMachines;
import net.swedz.intothetwilight.mi.machines.MIMachineHook;
import net.swedz.intothetwilight.mi.machines.MIMachineHookTracker;
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
		MIMachineHookTracker.open();
		MIMachineHook.singleBlockCraftingMachines();
		MIMachineHookTracker.close();
	}
}
