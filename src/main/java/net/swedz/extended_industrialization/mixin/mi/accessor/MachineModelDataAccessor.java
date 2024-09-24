package net.swedz.extended_industrialization.mixin.mi.accessor;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(
		value = MachineBlockEntity.class,
		remap = false
)
public interface MachineModelDataAccessor
{
	@Invoker("getMachineModelData")
	MachineModelClientData getMachineModelData();
}
