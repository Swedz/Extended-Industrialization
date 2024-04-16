package net.swedz.intothetwilight.mixin.mi.machinehook;

import aztech.modern_industrialization.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import net.swedz.intothetwilight.mi.machines.MIMachineHook;
import net.swedz.intothetwilight.mi.machines.MIMachineHookTracker;
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
		MIMachineHookTracker.open();
		value.addAll(MIMachineHook.blastFurnaceTiers());
		MIMachineHookTracker.close();
		return value;
	}
}
