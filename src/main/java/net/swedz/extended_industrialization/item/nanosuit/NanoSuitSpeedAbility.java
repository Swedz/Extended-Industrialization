package net.swedz.extended_industrialization.item.nanosuit;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.EITooltips;

import java.util.List;
import java.util.Optional;

import static net.swedz.extended_industrialization.EITooltips.*;
import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.*;

public final class NanoSuitSpeedAbility implements NanoSuitAbility
{
	private static final long   ENERGY_COST = 2;
	private static final double SPEED_BOOST = 0.4;
	
	@Override
	public ArmorItem.Type armorType()
	{
		return ArmorItem.Type.LEGGINGS;
	}
	
	@Override
	public ItemAttributeModifiers getModifiedDefaultAttributeModifiers(NanoSuitArmorItem item, ItemStack stack, ItemAttributeModifiers modifiers)
	{
		if(item.getStoredEnergy(stack) > 0 && item.isActivated(stack))
		{
			modifiers = modifiers.withModifierAdded(
					Attributes.MOVEMENT_SPEED,
					new AttributeModifier(
							EI.id("nano_speed_boost"),
							SPEED_BOOST,
							AttributeModifier.Operation.ADD_MULTIPLIED_BASE
					),
					this.equipmentSlotGroup()
			);
		}
		return modifiers;
	}
	
	@Override
	public Optional<List<Component>> getTooltipLines(NanoSuitArmorItem item, ItemStack stack)
	{
		return Optional.of(List.of(
				line(EIText.NANO_SUIT_SPEED).arg(item.isActivated(stack), EITooltips.ACTIVATED_BOOLEAN_PARSER)
		));
	}
	
	@Override
	public List<Component> getHelpTooltipLines(NanoSuitArmorItem item, ItemStack stack)
	{
		return List.of(
				line(EIText.NANO_SUIT_HELP_SPEED).arg("%s.toggle_leggings_ability".formatted(EI.ID), KEYBIND_PARSER)
		);
	}
	
	@Override
	public void onActivationChange(NanoSuitArmorItem item, Player player, ItemStack stack, boolean activated)
	{
		player.displayClientMessage((activated ? EIText.NANO_SUIT_SPEED_TOGGLED_ON : EIText.NANO_SUIT_SPEED_TOGGLED_OFF).text(), true);
	}
	
	@Override
	public void tick(NanoSuitArmorItem item, LivingEntity entity, EquipmentSlot slot, ItemStack stack)
	{
		if(item.isActivated(stack))
		{
			item.tryUseEnergy(stack, ENERGY_COST);
		}
	}
}
