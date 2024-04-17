package net.swedz.miextended.mixin.mi.machinehook.tracker;

import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.init.MachineRegistrationHelper;
import aztech.modern_industrialization.machines.models.MachineCasing;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.swedz.miextended.mi.machines.MIMachineHookTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
	private static void registerMachine(
			String englishName, String id,
			Function<BEP, MachineBlockEntity> factory,
			Consumer<BlockEntityType<?>>[] extraRegistrators,
			CallbackInfoReturnable<Supplier<BlockEntityType<?>>> callback)
	{
		if(MIMachineHookTracker.isOpen())
		{
			MIMachineHookTracker.addMachineLanguageEntry(id, englishName);
		}
	}
	
	@Inject(
			method = "addMachineModel(Ljava/lang/String;Ljava/lang/String;Laztech/modern_industrialization/machines/models/MachineCasing;ZZZZ)V",
			at = @At("HEAD")
	)
	private static void addMachineModel(
			String id, String overlay,
			MachineCasing defaultCasing,
			boolean front, boolean top, boolean side, boolean active,
			CallbackInfo callback)
	{
		if(MIMachineHookTracker.isOpen())
		{
			MIMachineHookTracker.addMachineModelBlockState(id);
			MIMachineHookTracker.addMachineModelBlockModel(id, overlay, defaultCasing, front, top, side, active);
			MIMachineHookTracker.addMachineModelItemModel(id);
		}
	}
}
