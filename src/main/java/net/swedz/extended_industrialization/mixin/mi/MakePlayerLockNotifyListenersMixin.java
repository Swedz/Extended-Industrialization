package net.swedz.extended_industrialization.mixin.mi;

import aztech.modern_industrialization.inventory.AbstractConfigurableStack;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.storage.TransferVariant;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(AbstractConfigurableStack.class)
public class MakePlayerLockNotifyListenersMixin
{
	@Inject(
			method = "playerLockNoOverride",
			at = @At(
					value = "RETURN",
					ordinal = 0
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static <T, K extends TransferVariant<T>> void playerLockNoOverride(T instance, List<? extends AbstractConfigurableStack<T, K>> stacks, CallbackInfo ci, int iter, boolean allowEmptyStacks, Iterator var4, AbstractConfigurableStack stack)
	{
		((AbstractConfigurableStackAccessor) stack).invokeNotifyListeners();
	}
	
	@Inject(
			method = "playerLock",
			at = @At(
					value = "FIELD",
					target = "Laztech/modern_industrialization/inventory/AbstractConfigurableStack;playerLocked:Z",
					opcode = Opcodes.PUTFIELD,
					ordinal = 0,
					shift = At.Shift.AFTER
			)
	)
	private void playerLock(CallbackInfoReturnable<Boolean> callback)
	{
		((AbstractConfigurableStackAccessor) this).invokeNotifyListeners();
	}
}
