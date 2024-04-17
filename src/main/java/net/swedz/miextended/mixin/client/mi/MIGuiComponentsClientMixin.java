package net.swedz.miextended.mixin.client.mi;

import aztech.modern_industrialization.machines.GuiComponentsClient;
import net.swedz.miextended.mi.hook.MIGuiComponentsClientHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiComponentsClient.class)
public class MIGuiComponentsClientMixin
{
	@Inject(
			method = "<clinit>",
			at = @At("TAIL")
	)
	private static void cinit(CallbackInfo callback)
	{
		MIGuiComponentsClientHook.hook();
	}
}
