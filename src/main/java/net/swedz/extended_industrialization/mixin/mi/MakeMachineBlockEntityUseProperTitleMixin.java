package net.swedz.extended_industrialization.mixin.mi;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.network.chat.Component;
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
		MachineBlockEntity machine = (MachineBlockEntity) (Object) this;
		callback.setReturnValue(machine.getBlockState().getBlock().getName());
	}
}
