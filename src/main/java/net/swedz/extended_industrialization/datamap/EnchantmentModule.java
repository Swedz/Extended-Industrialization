package net.swedz.extended_industrialization.datamap;

import aztech.modern_industrialization.api.energy.CableTier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.swedz.extended_industrialization.EIDataMaps;
import net.swedz.tesseract.neoforge.compat.mi.serialization.MICodecs;
import net.swedz.tesseract.neoforge.helper.RegistryHelper;

import java.util.Collections;
import java.util.Map;

public record EnchantmentModule(ResourceKey<Enchantment> enchantment, Value fallback, Map<CableTier, Value> values)
{
	public static final Codec<EnchantmentModule> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					ResourceKey.codec(Registries.ENCHANTMENT).fieldOf("enchantment").forGetter(EnchantmentModule::enchantment),
					Value.CODEC.fieldOf("fallback").forGetter(EnchantmentModule::fallback),
					Codec.unboundedMap(MICodecs.CABLE_TIER, Value.CODEC).fieldOf("values").forGetter(EnchantmentModule::values)
			)
			.apply(instance, EnchantmentModule::new));
	
	public record Value(int level, long euCost)
	{
		public static final Codec<Value> CODEC = RecordCodecBuilder.create((instance) -> instance
				.group(
						Codec.intRange(1, 255).fieldOf("level").forGetter(Value::level),
						Codec.LONG.fieldOf("eu_cost").forGetter(Value::euCost)
				)
				.apply(instance, Value::new));
	}
	
	public EnchantmentModule
	{
		values = Collections.unmodifiableMap(values);
	}
	
	public Holder<Enchantment> enchantment(RegistryAccess access)
	{
		return access.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment);
	}
	
	public Value get(CableTier tier)
	{
		return tier == null ? fallback : values.getOrDefault(tier, fallback);
	}
	
	public int getLevel(CableTier tier)
	{
		return this.get(tier).level();
	}
	
	public long getEuCost(CableTier tier)
	{
		return this.get(tier).euCost();
	}
	
	public void applyEnchantment(RegistryAccess access, ItemStack stack, CableTier tier)
	{
		var currentEnchantments = stack.get(DataComponents.ENCHANTMENTS);
		if(currentEnchantments == null)
		{
			currentEnchantments = ItemEnchantments.EMPTY;
		}
		var enchantments = new ItemEnchantments.Mutable(currentEnchantments);
		enchantments.set(this.enchantment(access), this.getLevel(tier));
		stack.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
	}
	
	public static EnchantmentModule getFor(Item item)
	{
		return RegistryHelper.holder(BuiltInRegistries.ITEM, item).getData(EIDataMaps.ENCHANTMENT_MODULE);
	}
}
