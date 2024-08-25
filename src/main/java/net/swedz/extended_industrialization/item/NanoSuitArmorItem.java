package net.swedz.extended_industrialization.item;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.swedz.tesseract.neoforge.item.ArmorTickHandler;
import net.swedz.tesseract.neoforge.item.ArmorUnequippedHandler;

public final class NanoSuitArmorItem extends ElectricArmorItem implements ArmorTickHandler, ArmorUnequippedHandler
{
	private static final long NIGHT_VISION_ENERGY = 4;
	
	public NanoSuitArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties, long energyCapacity)
	{
		super(material, type, properties, energyCapacity);
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
			if(this.getStoredEnergy(stack) >= NIGHT_VISION_ENERGY)
			{
				this.tryUseEnergy(stack, NIGHT_VISION_ENERGY);
				this.maybeAddNightVision(entity);
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
