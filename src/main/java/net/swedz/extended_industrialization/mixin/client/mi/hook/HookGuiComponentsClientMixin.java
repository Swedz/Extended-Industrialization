package net.swedz.extended_industrialization.mixin.client.mi.hook;

import aztech.modern_industrialization.machines.GuiComponentsClient;
import net.swedz.extended_industrialization.hook.mi.MIHookDelegator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiComponentsClient.class)
public class HookGuiComponentsClientMixin
{
	@Inject(
			method = "<clinit>",
			at = @At("TAIL"),
			remap = false
	)
	private static void cinit(CallbackInfo callback)
	{
		MIHookDelegator.clientGuiComponents();
	}
}
