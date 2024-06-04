package net.swedz.extended_industrialization.mixin.mi.hook;

import aztech.modern_industrialization.machines.recipe.condition.MachineProcessConditions;
import net.swedz.extended_industrialization.hook.mi.MIHookDelegator;
import net.swedz.extended_industrialization.hook.mi.tracker.MIHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
		value = MachineProcessConditions.class,
		remap = false
)
public class HookMachineProcessConditionsMixin
{
	@Inject(
			method = "<clinit>",
			at = @At("RETURN")
	)
	private static void clinit(CallbackInfo callback)
	{
		MIHookTracker.open();
		MIHookDelegator.machineProcessConditions();
		MIHookTracker.close();
	}
}
