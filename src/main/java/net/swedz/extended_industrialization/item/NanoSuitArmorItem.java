package net.swedz.extended_industrialization.item;

import aztech.modern_industrialization.api.energy.CableTier;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EIText;
import net.swedz.tesseract.neoforge.item.ArmorTickHandler;
import net.swedz.tesseract.neoforge.item.ArmorUnequippedHandler;
import net.swedz.tesseract.neoforge.item.ItemHurtHandler;

public final class NanoSuitArmorItem extends ElectricArmorItem implements ArmorTickHandler, ArmorUnequippedHandler, ItemHurtHandler, ToggleableItem
{
	private static final long ENERGY_CAPACITY     = 60 * 20 * CableTier.MV.getMaxTransfer();
	private static final long DAMAGE_ENERGY       = 1024;
	private static final long NIGHT_VISION_ENERGY = 4;
	
	public NanoSuitArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties)
	{
		super(material, type, properties, ENERGY_CAPACITY, DAMAGE_ENERGY);
	}
	
	@Override
	public boolean getDefaultActivatedState()
	{
		return true;
	}
	
	@Override
	public void setActivated(Player player, ItemStack stack, boolean activated)
	{
		ToggleableItem.super.setActivated(player, stack, activated);
		
		if(!player.level().isClientSide())
		{
			player.displayClientMessage((activated ? EIText.NANO_SUIT_NIGHT_VISION_TOGGLED_ON : EIText.NANO_SUIT_NIGHT_VISION_TOGGLED_OFF).text(), true);
		}
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
	public void armorTick(LivingEntity entity, EquipmentSlot slot, ItemStack stack)
	{
		if(entity.level().isClientSide())
		{
			return;
		}
		if(slot == EquipmentSlot.HEAD && type == Type.HELMET)
		{
			if(this.isActivated(stack))
			{
				if(this.getStoredEnergy(stack) > 0)
				{
					this.tryUseEnergy(stack, NIGHT_VISION_ENERGY);
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
	}
	
	@Override
	public void onUnequipArmor(LivingEntity entity, EquipmentSlot slot, ItemStack fromStack, ItemStack toStack)
	{
		if(!ItemStack.isSameItem(fromStack, toStack))
		{
			this.maybeRemoveNightVision(entity);
		}
	}
}
