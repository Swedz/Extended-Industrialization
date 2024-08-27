package net.swedz.extended_industrialization.item.nanosuit;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;
import java.util.Optional;

public interface NanoSuitAbility
{
	NanoSuitNightVisionAbility NIGHT_VISION = new NanoSuitNightVisionAbility();
	
	default Rarity rarity()
	{
		return Rarity.UNCOMMON;
	}
	
	ArmorItem.Type armorType();
	
	Optional<List<Component>> getTooltips(NanoSuitArmorItem item, ItemStack stack);
	
	void onActivationChange(NanoSuitArmorItem item, Player player, ItemStack stack, boolean activated);
	
	void tick(NanoSuitArmorItem item, LivingEntity entity, EquipmentSlot slot, ItemStack stack);
	
	void onUnequip(NanoSuitArmorItem item, LivingEntity entity, EquipmentSlot slot, ItemStack fromStack, ItemStack toStack);
}
