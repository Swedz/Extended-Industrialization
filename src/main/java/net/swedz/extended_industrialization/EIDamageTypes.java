package net.swedz.extended_industrialization;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class EIDamageTypes
{
	private static final ResourceKey<DamageType> TESLA = ResourceKey.create(Registries.DAMAGE_TYPE, EI.id("tesla"));
	
	private static Holder<DamageType> tesla(RegistryAccess registry)
	{
		return registry.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(TESLA);
	}
	
	public static DamageSource tesla(Level level, Vec3 origin)
	{
		return new DamageSource(tesla(level.registryAccess()), origin);
	}
}
