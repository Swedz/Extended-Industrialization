package net.swedz.extended_industrialization.item.nanosuit;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.swedz.extended_industrialization.EIArmorMaterials;

import java.util.List;
import java.util.Optional;

public interface NanoSuitAbility
{
	NanoSuitGravichestplateAbility GRAVICHESTPLATE = new NanoSuitGravichestplateAbility();
	NanoSuitNightVisionAbility     NIGHT_VISION    = new NanoSuitNightVisionAbility();
	NanoSuitSpeedAbility           SPEED           = new NanoSuitSpeedAbility();
	
	default Rarity rarity()
	{
		return Rarity.UNCOMMON;
	}
	
	ArmorItem.Type armorType();
	
	default EquipmentSlot equipmentSlot()
	{
		return this.armorType().getSlot();
	}
	
	default EquipmentSlotGroup equipmentSlotGroup()
	{
		return EquipmentSlotGroup.bySlot(this.equipmentSlot());
	}
	
	default long overrideEnergyCapacity()
	{
		return 0;
	}
	
	default int overrideDefaultColor()
	{
		return EIArmorMaterials.NANO_COLOR;
	}
	
	default ItemAttributeModifiers getModifiedDefaultAttributeModifiers(NanoSuitArmorItem item, ItemStack stack, ItemAttributeModifiers modifiers)
	{
		return modifiers;
	}
	
	default Optional<List<Component>> getTooltipLines(NanoSuitArmorItem item, ItemStack stack)
	{
		return Optional.empty();
	}
	
	default List<Component> getHelpTooltipLines(NanoSuitArmorItem item, ItemStack stack)
	{
		return List.of();
	}
	
	default void onActivationChange(NanoSuitArmorItem item, Player player, ItemStack stack, boolean activated)
	{
	}
	
	default void tick(NanoSuitArmorItem item, LivingEntity entity, EquipmentSlot slot, ItemStack stack)
	{
	}
	
	default void onUnequip(NanoSuitArmorItem item, LivingEntity entity, EquipmentSlot slot, ItemStack fromStack, ItemStack toStack)
	{
	}
}
