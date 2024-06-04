package net.swedz.extended_industrialization.mixin.mi.hack;

import aztech.modern_industrialization.datagen.model.MachineModelsToGenerate;
import aztech.modern_industrialization.machines.models.MachineCasing;
import net.swedz.extended_industrialization.hook.mi.tracker.MIHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
		value = MachineModelsToGenerate.class,
		remap = false
)
public class InterceptRegisterMachineModelMixin
{
	@Inject(
			method = "register",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void register(String id, MachineCasing defaultCasing, String overlay,
								 boolean front, boolean top, boolean side, boolean active,
								 CallbackInfo callback)
	{
		if(MIHookTracker.isOpen())
		{
			MIHookTracker.addMachineModel(id, defaultCasing, overlay, front, top, side, active);
			callback.cancel();
		}
	}
}
