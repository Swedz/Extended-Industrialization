package net.swedz.extended_industrialization.item.nanosuit.ability;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;

import java.util.List;
import java.util.Optional;

public final class NanoSuitNightVisionAbility implements NanoSuitAbility
{
	private static final long ENERGY_COST = 4;
	
	@Override
	public ArmorItem.Type armorType()
	{
		return ArmorItem.Type.HELMET;
	}
	
	@Override
	public Optional<List<Component>> getTooltipLines(NanoSuitArmorItem item, ItemStack stack)
	{
		return Optional.of(List.of(
				EI.text().nanoSuitNightVision(item.isActivated(stack))
		));
	}
	
	@Override
	public List<Component> getHelpTooltipLines(NanoSuitArmorItem item, ItemStack stack)
	{
		return List.of(
				EI.text().nanoSuitHelpNightVision("%s.toggle_helmet_ability".formatted(EI.ID), "mouse.right")
		);
	}
	
	@Override
	public void onActivationChange(NanoSuitArmorItem item, Player player, ItemStack stack, boolean activated)
	{
		player.displayClientMessage(activated ? EI.text().nanoSuitNightVisionToggledOn() : EI.text().nanoSuitNightVisionToggledOff(), true);
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
			if(item.hasEnergy(stack))
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
