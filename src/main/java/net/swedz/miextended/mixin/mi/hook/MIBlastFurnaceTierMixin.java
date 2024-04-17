package net.swedz.miextended.mixin.mi.hook;

import aztech.modern_industrialization.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import net.swedz.miextended.mi.hook.MIMachineHook;
import net.swedz.miextended.mi.hook.tracker.MIHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(ElectricBlastFurnaceBlockEntity.class)
public class MIBlastFurnaceTierMixin
{
	@ModifyVariable(
			method = "<clinit>",
			at = @At(value = "INVOKE", target = "Ljava/util/List;sort(Ljava/util/Comparator;)V"),
			remap = false
	)
	private static List clinit(List value)
	{
		MIHookTracker.open();
		value.addAll(MIMachineHook.blastFurnaceTiers());
		MIHookTracker.close();
		return value;
	}
}
