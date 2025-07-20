package net.swedz.extended_industrialization;

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
import net.swedz.extended_industrialization.lootmodifier.SimpleEnergyItemHasChargePredicate;

import java.util.function.Supplier;

public final class EILootModifiers
{
	public static final class LootModifiers
	{
		private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, EI.ID);
		
		public static final Supplier<MapCodec<AutoSmeltLootModifier>> AUTO_SMELT = LOOT_MODIFIERS.register("auto_smelt", () -> AutoSmeltLootModifier.CODEC);
		public static final Supplier<MapCodec<BeheadingLootModifier>> BEHEADING  = LOOT_MODIFIERS.register("beheading", () -> BeheadingLootModifier.CODEC);
	}
	
	public static final class ItemSubPredicates
	{
		private static final DeferredRegister<ItemSubPredicate.Type<?>> ITEM_SUB_PREDICATE_TYPES = DeferredRegister.create(Registries.ITEM_SUB_PREDICATE_TYPE, EI.ID);
		
		public static final Supplier<ItemSubPredicate.Type<SimpleEnergyItemHasChargePredicate>> SIMPLE_ENERGY_ITEM_HAS_CHARGE = ITEM_SUB_PREDICATE_TYPES.register("simple_energy_item_has_charge", () -> new ItemSubPredicate.Type<>(SimpleEnergyItemHasChargePredicate.CODEC));
		public static final Supplier<ItemSubPredicate.Type<ElectricToolModePredicate>> ELECTRIC_TOOL_MODE            = ITEM_SUB_PREDICATE_TYPES.register("electric_tool_mode", () -> new ItemSubPredicate.Type<>(ElectricToolModePredicate.CODEC));
	}
	
	public static void init(IEventBus bus)
	{
		LootModifiers.LOOT_MODIFIERS.register(bus);
		ItemSubPredicates.ITEM_SUB_PREDICATE_TYPES.register(bus);
	}
}
