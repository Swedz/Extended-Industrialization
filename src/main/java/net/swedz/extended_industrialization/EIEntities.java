package net.swedz.extended_industrialization;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.entity.NanoSaberSweepEntity;

import java.util.function.Supplier;

public final class EIEntities
{
	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, EI.ID);
	
	public static final Supplier<EntityType<NanoSaberSweepEntity>> NANO_SABER_SWEEP = create("nano_saber_sweep", () -> EntityType.Builder
			.<NanoSaberSweepEntity>of(NanoSaberSweepEntity::new, MobCategory.MISC)
			.sized(1.5f, 6f / 16f)
			.clientTrackingRange(4)
			.updateInterval(10));
	
	private static <T extends Entity> Supplier<EntityType<T>> create(String name, Supplier<EntityType.Builder<T>> builder)
	{
		return ENTITY_TYPES.register(name, () -> builder.get().build(name));
	}
	
	public static void init(IEventBus bus)
	{
		ENTITY_TYPES.register(bus);
	}
}
