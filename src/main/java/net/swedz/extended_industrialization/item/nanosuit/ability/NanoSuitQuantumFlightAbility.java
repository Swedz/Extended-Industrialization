package net.swedz.extended_industrialization.item.nanosuit.ability;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;

import java.util.List;
import java.util.Optional;

public final class NanoSuitQuantumFlightAbility implements NanoSuitAbility
{
	@Override
	public ArmorItem.Type armorType()
	{
		return ArmorItem.Type.CHESTPLATE;
	}
	
	@Override
	public ItemAttributeModifiers getModifiedDefaultAttributeModifiers(NanoSuitArmorItem item, ItemStack stack, ItemAttributeModifiers modifiers)
	{
		if(item.isActivated(stack))
		{
			modifiers = modifiers.withModifierAdded(
					NeoForgeMod.CREATIVE_FLIGHT,
					new AttributeModifier(
							EI.id("nano_quantum_flight"),
							1,
							AttributeModifier.Operation.ADD_VALUE
					),
					EquipmentSlotGroup.CHEST
			);
		}
		return modifiers;
	}
	
	@Override
	public Optional<List<Component>> getTooltipLines(NanoSuitArmorItem item, ItemStack stack)
	{
		return Optional.of(List.of(
				EI.text().nanoSuitCreativeFlight(item.isActivated(stack))
		));
	}
	
	@Override
	public List<Component> getHelpTooltipLines(NanoSuitArmorItem item, ItemStack stack)
	{
		return List.of(
				EI.text().nanoSuitHelpCreativeFlight("%s.toggle_chestplate_ability".formatted(EI.ID), "mouse.right")
		);
	}
}
