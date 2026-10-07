package net.swedz.extended_industrialization;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.advancement.BuiltFullyNetheriteElectricBeaconTrigger;
import net.swedz.extended_industrialization.advancement.KilledByLethalTeslaCoilTrigger;
import net.swedz.extended_industrialization.advancement.LinkedManyChainersTrigger;
import net.swedz.extended_industrialization.advancement.MusicalTeslaCoilTrigger;

import java.util.function.Supplier;

public final class EICriterionTriggers
{
	private static final DeferredRegister<CriterionTrigger<?>> REGISTER = DeferredRegister.create(Registries.TRIGGER_TYPE, EI.ID);
	
	public static final Supplier<BuiltFullyNetheriteElectricBeaconTrigger> BUILT_FULLY_NETHERITE_ELECTRIC_BEACON = REGISTER.register("built_fully_netherite_electric_beacon", BuiltFullyNetheriteElectricBeaconTrigger::new);
	public static final Supplier<KilledByLethalTeslaCoilTrigger>           KILLED_BY_LETHAL_TESLA_COIL           = REGISTER.register("killed_by_lethal_tesla_coil", KilledByLethalTeslaCoilTrigger::new);
	public static final Supplier<LinkedManyChainersTrigger>                LINKED_MANY_CHAINERS                  = REGISTER.register("linked_many_chainers", LinkedManyChainersTrigger::new);
	public static final Supplier<MusicalTeslaCoilTrigger>                  MUSICAL_TESLA_COIL                    = REGISTER.register("musical_tesla_coil", MusicalTeslaCoilTrigger::new);
	
	public static void init(IEventBus bus)
	{
		REGISTER.register(bus);
	}
}
