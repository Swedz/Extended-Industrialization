package net.swedz.extended_industrialization.mixin.client.mi;

import aztech.modern_industrialization.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.machines.gui.MachineScreen;
import aztech.modern_industrialization.machines.guicomponents.RecipeEfficiencyBarClient;
import net.swedz.extended_industrialization.EIConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeEfficiencyBarClient.class)
public class HideEfficiencyBarClientMixin
{
	@Inject(
			method = "createRenderer",
			at = @At("HEAD"),
			cancellable = true
	)
	private void createRenderer(MachineScreen machineScreen, CallbackInfoReturnable<ClientComponentRenderer> callback)
	{
		if(EIConfig.machineEfficiencyHack.hideEfficiency())
		{
			callback.setReturnValue((guiGraphics, leftPos, topPos) ->
			{
			});
		}
	}
}
