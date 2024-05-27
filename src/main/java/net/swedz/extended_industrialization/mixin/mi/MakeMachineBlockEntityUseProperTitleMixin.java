package net.swedz.extended_industrialization.mixin.mi;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.network.chat.Component;
import net.swedz.extended_industrialization.mixinduck.CableTierDuck;
import net.swedz.extended_industrialization.text.EIText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MachineBlockEntity.class)
public abstract class MakeMachineBlockEntityUseProperTitleMixin
{
	@Inject(
			method = "getDisplayName",
			at = @At("HEAD"),
			cancellable = true
	)
	private void getDisplayName(CallbackInfoReturnable<Component> callback)
	{
		// TODO split this into 2 mixins
		MachineBlockEntity machine = (MachineBlockEntity) (Object) this;
		Component title = machine.getBlockState().getBlock().getName();
		if(machine instanceof CableTierDuck machineTiered)
		{
			// TODO this doesnt get updated when the voltage changes
			callback.setReturnValue(EIText.MACHINE_VOLTAGE_PREFIX.text(Component.translatable(machineTiered.getTier().shortEnglishKey())).append(title));
		}
		else
		{
			callback.setReturnValue(title);
		}
	}
}
