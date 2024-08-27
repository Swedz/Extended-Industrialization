package net.swedz.extended_industrialization.item.nanosuit;

import aztech.modern_industrialization.items.armor.GraviChestPlateItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIArmorMaterials;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.EITooltips;

import java.util.List;
import java.util.Optional;

import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.*;

public final class NanoSuitGravichestplateAbility implements NanoSuitAbility
{
	private static final long ENERGY_COST = 1024;
	
	@Override
	public Rarity rarity()
	{
		return Rarity.EPIC;
	}
	
	@Override
	public ArmorItem.Type armorType()
	{
		return ArmorItem.Type.CHESTPLATE;
	}
	
	@Override
	public long overrideEnergyCapacity()
	{
		return GraviChestPlateItem.ENERGY_CAPACITY;
	}
	
	@Override
	public int overrideDefaultColor()
	{
		return EIArmorMaterials.NANO_GRAVICHESTPLATE_COLOR;
	}
	
	@Override
	public ItemAttributeModifiers getModifiedDefaultAttributeModifiers(NanoSuitArmorItem item, ItemStack stack, ItemAttributeModifiers modifiers)
	{
		if(item.getStoredEnergy(stack) > 0 && item.isActivated(stack))
		{
			modifiers = modifiers.withModifierAdded(
					NeoForgeMod.CREATIVE_FLIGHT,
					new AttributeModifier(
							EI.id("nano_gravichestplate"),
							1,
							AttributeModifier.Operation.ADD_VALUE
					),
					this.equipmentSlotGroup()
			);
		}
		return modifiers;
	}
	
	@Override
	public Optional<List<Component>> getTooltips(NanoSuitArmorItem item, ItemStack stack)
	{
		return Optional.of(List.of(
				line(EIText.NANO_SUIT_CREATIVE_FLIGHT).arg(item.isActivated(stack), EITooltips.ACTIVATED_BOOLEAN_PARSER)
		));
	}
	
	@Override
	public void tick(NanoSuitArmorItem item, LivingEntity entity, EquipmentSlot slot, ItemStack stack)
	{
		if(entity instanceof Player player && item.isActivated(stack) && player.getAbilities().flying)
		{
			item.tryUseEnergy(stack, ENERGY_COST);
		}
	}
}
