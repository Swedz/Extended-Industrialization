package net.swedz.extended_industrialization;

import aztech.modern_industrialization.api.energy.CableTier;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.swedz.extended_industrialization.datamap.EnchantmentModule;

import java.util.Optional;
import java.util.UUID;

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
	
	public static DamageSource tesla(Level level, Vec3 origin, Entity damager)
	{
		return new DamageSource(tesla(level.registryAccess()), damager, damager, origin);
	}
	
	public static DamageSource teslaFakePlayer(Level level, Vec3 origin, UUID ownerUUID,
											   Optional<EnchantmentModule> enchantment, CableTier tier)
	{
		var damager = FakePlayerFactory.get((ServerLevel) level, new GameProfile(ownerUUID, "Tesla"));
		var item = ItemStack.EMPTY;
		if(enchantment.isPresent())
		{
			item = new ItemStack(Items.DIAMOND_SWORD);
			enchantment.get().applyEnchantment(level.registryAccess(), item, tier);
		}
		damager.setItemInHand(InteractionHand.MAIN_HAND, item);
		return tesla(level, origin, damager);
	}
}
