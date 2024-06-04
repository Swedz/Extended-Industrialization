package net.swedz.extended_industrialization.mixin.mi.constantefficiency;

import aztech.modern_industrialization.machines.components.UpgradeComponent;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import net.swedz.extended_industrialization.EIConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
		value = SlotPanel.Server.class,
		remap = false
)
public class ConstantEfficiencyPreventUpgradeSlotMixin
{
	@Inject(
			method = "withUpgrades",
			at = @At("HEAD"),
			cancellable = true
	)
	private void withUpgrades(UpgradeComponent upgradeComponent, CallbackInfoReturnable<SlotPanel.Server> callback)
	{
		if(EIConfig.machineEfficiencyHack.preventsUpgrades())
		{
			callback.setReturnValue((SlotPanel.Server) (Object) this);
		}
	}
}
