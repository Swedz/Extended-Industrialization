package net.swedz.miextended.mi.machines;

import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.init.MachineRegistrationHelper;
import aztech.modern_industrialization.machines.models.MachineCasing;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;
import java.util.function.Function;

public final class MIMachineHookHelper
{
	@SafeVarargs
	public static void registerSingleBlockSpecialMachine(String englishName, String id, String overlayFolder,
														 MachineCasing defaultCasing, boolean frontOverlay, boolean topOverlay, boolean sideOverlay, boolean hasActive,
														 Function<BEP, MachineBlockEntity> factory,
														 Consumer<BlockEntityType<?>>... extraRegistrators)
	{
		MachineRegistrationHelper.registerMachine(englishName, id, factory, extraRegistrators);
		MachineRegistrationHelper.addMachineModel(id, overlayFolder, defaultCasing, frontOverlay, topOverlay, sideOverlay, hasActive);
	}
	
	@SafeVarargs
	public static void registerSingleBlockSpecialMachine(String englishName, String id, String overlayFolder,
														 MachineCasing defaultCasing, boolean frontOverlay, boolean topOverlay, boolean sideOverlay,
														 Function<BEP, MachineBlockEntity> factory,
														 Consumer<BlockEntityType<?>>... extraRegistrators)
	{
		registerSingleBlockSpecialMachine(englishName, id, overlayFolder, defaultCasing, frontOverlay, topOverlay, sideOverlay, true, factory, extraRegistrators);
	}
}