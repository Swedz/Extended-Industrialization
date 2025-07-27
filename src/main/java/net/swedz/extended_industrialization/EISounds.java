package net.swedz.extended_industrialization;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class EISounds
{
	private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, EI.ID);
	
	public static final Supplier<SoundEvent> TESLA_COIL_LOOP             = createVariableRangeEvent("tesla.loop");
	public static final Supplier<SoundEvent> TESLA_COIL_SINGING          = createVariableRangeEvent("tesla.singing");
	public static final Supplier<SoundEvent> NANO_SABER_SWEEP_SWING      = createVariableRangeEvent("nano_saber_sweep.swing");
	public static final Supplier<SoundEvent> NANO_SABER_SWEEP_EXTINGUISH = createVariableRangeEvent("nano_saber_sweep.extinguish");
	
	private static Supplier<SoundEvent> createVariableRangeEvent(String name)
	{
		return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(EI.id(name)));
	}
	
	public static void init(IEventBus bus)
	{
		SOUND_EVENTS.register(bus);
	}
}
