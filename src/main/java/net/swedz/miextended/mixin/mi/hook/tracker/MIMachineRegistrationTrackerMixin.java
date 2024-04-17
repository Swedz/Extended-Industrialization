package net.swedz.miextended.mixin.mi.hook.tracker;

import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.init.MachineRegistrationHelper;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.swedz.miextended.mi.hook.tracker.MIHookTracker;
import net.swedz.miextended.tooltips.MIETooltips;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(MachineRegistrationHelper.class)
public class MIMachineRegistrationTrackerMixin
{
	@Inject(
			method = "registerMachine",
			at = @At("HEAD")
	)
	private static void registerMachine(String englishName, String id,
										Function<BEP, MachineBlockEntity> factory,
										Consumer<BlockEntityType<?>>[] extraRegistrators,
										CallbackInfoReturnable<Supplier<BlockEntityType<?>>> callback)
	{
		if(MIHookTracker.isOpen())
		{
			MIHookTracker.addMachineLanguageEntry(id, englishName);
			MIETooltips.addExtendedItem(id);
		}
	}
}
