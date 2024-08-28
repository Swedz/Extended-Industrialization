package net.swedz.extended_industrialization.item.nanosuit;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.EITooltips;

import java.util.List;
import java.util.Optional;

import static net.swedz.extended_industrialization.EITooltips.*;
import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.*;

public final class NanoSuitNightVisionAbility implements NanoSuitAbility
{
	private static final long ENERGY_COST = 4;
	
	@Override
	public ArmorItem.Type armorType()
	{
		return ArmorItem.Type.HELMET;
	}
	
	@Override
	public Optional<List<Component>> getTooltips(NanoSuitArmorItem item, ItemStack stack)
	{
		return Optional.of(List.of(
				line(EIText.NANO_SUIT_NIGHT_VISION).arg(item.isActivated(stack), EITooltips.ACTIVATED_BOOLEAN_PARSER)
		));
	}
	
	@Override
	public Optional<List<Component>> getShiftTooltips(NanoSuitArmorItem item, ItemStack stack)
	{
		return Optional.of(List.of(
				line(EIText.NANO_SUIT_INFO_NIGHT_VISION).arg("%s.toggle_helmet_ability".formatted(EI.ID), KEYBIND_PARSER)
		));
	}
	
	@Override
	public void onActivationChange(NanoSuitArmorItem item, Player player, ItemStack stack, boolean activated)
	{
		player.displayClientMessage((activated ? EIText.NANO_SUIT_NIGHT_VISION_TOGGLED_ON : EIText.NANO_SUIT_NIGHT_VISION_TOGGLED_OFF).text(), true);
	}
	
	private boolean hasNightVision(LivingEntity entity)
	{
		return entity.hasEffect(MobEffects.NIGHT_VISION) &&
			   entity.getEffect(MobEffects.NIGHT_VISION).isInfiniteDuration();
	}
	
	private void maybeAddNightVision(LivingEntity entity)
	{
		if(!this.hasNightVision(entity))
		{
			entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, MobEffectInstance.INFINITE_DURATION, 0, false, false, false));
		}
	}
	
	private void maybeRemoveNightVision(LivingEntity entity)
	{
		if(this.hasNightVision(entity))
		{
			entity.removeEffect(MobEffects.NIGHT_VISION);
		}
	}
	
	@Override
	public void tick(NanoSuitArmorItem item, LivingEntity entity, EquipmentSlot slot, ItemStack stack)
	{
		if(item.isActivated(stack))
		{
			if(item.getStoredEnergy(stack) > 0)
			{
				item.tryUseEnergy(stack, ENERGY_COST);
				this.maybeAddNightVision(entity);
			}
			else
			{
				this.maybeRemoveNightVision(entity);
			}
		}
		else
		{
			this.maybeRemoveNightVision(entity);
		}
	}
	
	@Override
	public void onUnequip(NanoSuitArmorItem item, LivingEntity entity, EquipmentSlot slot, ItemStack fromStack, ItemStack toStack)
	{
		this.maybeRemoveNightVision(entity);
	}
}
