package net.swedz.extended_industrialization;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.swedz.extended_industrialization.lootmodifier.AutoSmeltLootModifier;
import net.swedz.extended_industrialization.lootmodifier.BeheadingLootModifier;
import net.swedz.extended_industrialization.lootmodifier.ElectricToolModePredicate;

import java.util.function.Supplier;

public final class EILootModifiers
{
	public static final class LootModifiers
	{
		private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, EI.ID);
		
		public static final Supplier<MapCodec<AutoSmeltLootModifier>> AUTO_SMELT = create("auto_smelt", AutoSmeltLootModifier.CODEC);
		public static final Supplier<MapCodec<BeheadingLootModifier>> BEHEADING  = create("beheading", BeheadingLootModifier.CODEC);
		
		private static <T extends IGlobalLootModifier> Supplier<MapCodec<T>> create(String name, MapCodec<T> codec)
		{
			return LOOT_MODIFIERS.register(name, () -> codec);
		}
	}
	
	public static final class ItemSubPredicates
	{
		private static final DeferredRegister<ItemSubPredicate.Type<?>> ITEM_SUB_PREDICATE_TYPES = DeferredRegister.create(Registries.ITEM_SUB_PREDICATE_TYPE, EI.ID);
		
		public static final Supplier<ItemSubPredicate.Type<ElectricToolModePredicate>> ELECTRIC_TOOL_MODE = create("electric_tool_mode", ElectricToolModePredicate.CODEC);
		
		private static <T extends ItemSubPredicate> Supplier<ItemSubPredicate.Type<T>> create(String name, Codec<T> codec)
		{
			return ITEM_SUB_PREDICATE_TYPES.register(name, () -> new ItemSubPredicate.Type<>(codec));
		}
	}
	
	public static void init(IEventBus bus)
	{
		LootModifiers.LOOT_MODIFIERS.register(bus);
		ItemSubPredicates.ITEM_SUB_PREDICATE_TYPES.register(bus);
	}
}
