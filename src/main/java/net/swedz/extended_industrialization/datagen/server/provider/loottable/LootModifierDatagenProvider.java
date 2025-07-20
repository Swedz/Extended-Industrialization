package net.swedz.extended_industrialization.datagen.server.provider.loottable;

import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EILootModifiers;
import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.lootmodifier.AutoSmeltLootModifier;
import net.swedz.extended_industrialization.lootmodifier.BeheadingLootModifier;
import net.swedz.extended_industrialization.lootmodifier.ElectricToolModePredicate;
import net.swedz.extended_industrialization.lootmodifier.SimpleEnergyItemHasChargePredicate;

public final class LootModifierDatagenProvider extends GlobalLootModifierProvider
{
	public LootModifierDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider(), EI.ID);
	}
	
	private void addBeheading(EntityType entityType, float chance, Item headItem)
	{
		var typeKey = EntityType.getKey(entityType);
		this.add(
				"nano_saber/beheading/%s/%s".formatted(typeKey.getNamespace(), typeKey.getPath()),
				new BeheadingLootModifier(
						new LootItemCondition[]{
								LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().entityType(EntityTypePredicate.of(entityType))).build(),
								LootItemRandomChanceCondition.randomChance(chance).build(),
								AnyOfCondition.anyOf(
										DamageSourceCondition.hasDamageSource(new DamageSourcePredicate.Builder()
												.tag(new TagPredicate<>(EITags.DamageTypes.BEHEADING, true))),
										LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, new EntityPredicate.Builder().equipment(new EntityEquipmentPredicate.Builder()
												.mainhand(ItemPredicate.Builder.item()
														.of(EIItems.NANO_SABER.asItem())
														.withSubPredicate(EILootModifiers.ItemSubPredicates.SIMPLE_ENERGY_ITEM_HAS_CHARGE.get(), new SimpleEnergyItemHasChargePredicate())
														.withSubPredicate(EILootModifiers.ItemSubPredicates.ELECTRIC_TOOL_MODE.get(), new ElectricToolModePredicate(false)))))
								).build()
						},
						new ItemStack(headItem)
				)
		);
	}
	
	@Override
	protected void start()
	{
		this.add(
				"nano_saber/auto_smelting",
				new AutoSmeltLootModifier(
						new LootItemCondition[]{
								AnyOfCondition.anyOf(
										DamageSourceCondition.hasDamageSource(new DamageSourcePredicate.Builder()
												.tag(new TagPredicate<>(EITags.DamageTypes.AUTO_SMELT, true))),
										LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, new EntityPredicate.Builder().equipment(new EntityEquipmentPredicate.Builder()
												.mainhand(ItemPredicate.Builder.item()
														.of(EIItems.NANO_SABER.asItem())
														.withSubPredicate(EILootModifiers.ItemSubPredicates.SIMPLE_ENERGY_ITEM_HAS_CHARGE.get(), new SimpleEnergyItemHasChargePredicate()))))
								).build()
						}
				)
		);
		
		// This is the same drop chance as wither skeleton skulls with looting 10
		float headChance = 0.125f;
		this.addBeheading(EntityType.WITHER_SKELETON, headChance, Items.WITHER_SKELETON_SKULL);
		this.addBeheading(EntityType.SKELETON, headChance, Items.SKELETON_SKULL);
		this.addBeheading(EntityType.ZOMBIE, headChance, Items.ZOMBIE_HEAD);
		this.addBeheading(EntityType.CREEPER, headChance, Items.CREEPER_HEAD);
		this.addBeheading(EntityType.PIGLIN, headChance, Items.PIGLIN_HEAD);
	}
}
