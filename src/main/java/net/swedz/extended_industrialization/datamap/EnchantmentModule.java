package net.swedz.extended_industrialization.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.swedz.extended_industrialization.EIDataMaps;
import net.swedz.tesseract.neoforge.helper.RegistryHelper;

public record EnchantmentModule(ResourceKey<Enchantment> enchantment, int level)
{
	public static final Codec<EnchantmentModule> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					ResourceKey.codec(Registries.ENCHANTMENT).fieldOf("enchantment").forGetter(EnchantmentModule::enchantment),
					Codec.intRange(1, 255).fieldOf("level").forGetter(EnchantmentModule::level)
			)
			.apply(instance, EnchantmentModule::new));
	
	public Holder<Enchantment> enchantment(RegistryAccess access)
	{
		return access.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment);
	}
	
	public static EnchantmentModule getFor(Item item)
	{
		return RegistryHelper.holder(BuiltInRegistries.ITEM, item).getData(EIDataMaps.ENCHANTMENT_MODULE);
	}
}
