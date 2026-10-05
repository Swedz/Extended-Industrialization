package net.swedz.extended_industrialization;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.advancement.KilledByLethalTeslaCoilTrigger;

import java.util.function.Supplier;

public final class EICriterionTriggers
{
	private static final DeferredRegister<CriterionTrigger<?>> REGISTER = DeferredRegister.create(Registries.TRIGGER_TYPE, EI.ID);
	
	public static final Supplier<KilledByLethalTeslaCoilTrigger> KILLED_BY_LETHAL_TESLA_COIL = REGISTER.register("killed_by_lethal_tesla_coil", KilledByLethalTeslaCoilTrigger::new);
	
	public static void init(IEventBus bus)
	{
		REGISTER.register(bus);
	}
}
