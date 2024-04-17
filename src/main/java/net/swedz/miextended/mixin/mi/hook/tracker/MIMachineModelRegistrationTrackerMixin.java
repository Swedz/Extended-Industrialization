package net.swedz.miextended.mixin.mi.hook.tracker;

import aztech.modern_industrialization.datagen.model.MachineModelsToGenerate;
import aztech.modern_industrialization.machines.models.MachineCasing;
import net.swedz.miextended.mi.hook.tracker.MIHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MachineModelsToGenerate.class)
public class MIMachineModelRegistrationTrackerMixin
{
	@Inject(
			method = "register",
			at = @At("HEAD")
	)
	private static void register(String id, MachineCasing defaultCasing, String overlay,
								 boolean front, boolean top, boolean side, boolean active,
								 CallbackInfo callback)
	{
		if(MIHookTracker.isOpen())
		{
			MIHookTracker.addMachineModelBlockState(id);
			MIHookTracker.addMachineModelBlockModel(id, overlay, defaultCasing, front, top, side, active);
			MIHookTracker.addMachineModelItemModel(id);
		}
	}
}
