package net.swedz.miextended.mixin.mi.hook.tracker;

import aztech.modern_industrialization.definition.FluidDefinition;
import aztech.modern_industrialization.definition.FluidTexture;
import net.swedz.miextended.mi.hook.tracker.MIHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidDefinition.class)
public class MIFluidDefinitionRegistrationTrackerMixin
{
	@Inject(
			method = "<init>",
			at = @At("RETURN")
	)
	private void init(String englishName, String id,
					  int color, int opacity, FluidTexture texture, boolean isGas,
					  CallbackInfo callback)
	{
		if(MIHookTracker.isOpen())
		{
			FluidDefinition fluidDefinition = FluidDefinition.class.cast(this);
			MIHookTracker.addFluidDefinitionLanguageEntry(fluidDefinition);
			MIHookTracker.addFluidDefinition(fluidDefinition);
		}
	}
}
