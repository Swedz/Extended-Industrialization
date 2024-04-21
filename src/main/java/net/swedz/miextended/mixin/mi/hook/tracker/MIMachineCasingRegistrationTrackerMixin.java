package net.swedz.miextended.mixin.mi.hook.tracker;

import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineCasings;
import net.swedz.miextended.mi.hook.tracker.MIHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MachineCasings.class)
public class MIMachineCasingRegistrationTrackerMixin
{
	@Inject(
			method = "create",
			at = @At("TAIL")
	)
	private static void create(String name, CallbackInfoReturnable<MachineCasing> callback)
	{
		if(MIHookTracker.isOpen())
		{
			MIHookTracker.addMachineCasingModel(name);
		}
	}
}
