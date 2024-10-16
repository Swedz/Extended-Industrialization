package net.swedz.extended_industrialization.item.nanosuit;

import aztech.modern_industrialization.api.energy.CableTier;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.swedz.extended_industrialization.EIArmorMaterials;
import net.swedz.extended_industrialization.item.ElectricArmorItem;
import net.swedz.extended_industrialization.item.ToggleableItem;
import net.swedz.tesseract.neoforge.helper.ColorHelper;
import net.swedz.tesseract.neoforge.item.ArmorTickHandler;
import net.swedz.tesseract.neoforge.item.ArmorUnequippedHandler;
import net.swedz.tesseract.neoforge.item.DynamicDyedItem;
import net.swedz.tesseract.neoforge.item.ItemHurtHandler;

import java.util.List;
import java.util.Optional;

public final class NanoSuitArmorItem extends ElectricArmorItem implements ArmorTickHandler, ArmorUnequippedHandler, ItemHurtHandler, ToggleableItem, DynamicDyedItem
{
	private static final long DEFAULT_ENERGY_CAPACITY = 60 * 20 * CableTier.MV.getMaxTransfer();
	private static final long DAMAGE_ENERGY           = 1024;
	
	private final Optional<NanoSuitAbility> ability;
	
	public NanoSuitArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties, Optional<NanoSuitAbility> ability)
	{
		super(
				material, type, properties,
				ability.map(NanoSuitAbility::overrideEnergyCapacity).filter((e) -> e > 0).orElse(DEFAULT_ENERGY_CAPACITY),
				DAMAGE_ENERGY
		);
		if(ability.isPresent() && type != ability.get().armorType())
		{
			throw new IllegalArgumentException("Mismatching armor type for item and ability");
		}
		this.ability = ability;
	}
	
	public Optional<NanoSuitAbility> ability()
	{
		return ability;
	}
	
	public boolean hasAbility(Class<? extends NanoSuitAbility> abilityClass)
	{
		return ability.filter((a) -> abilityClass.isAssignableFrom(a.getClass())).isPresent();
	}
	
	@Override
	public ItemAttributeModifiers getModifiedDefaultAttributeModifiers(ItemStack stack, ItemAttributeModifiers modifiers)
	{
		if(ability.isPresent())
		{
			NanoSuitAbility ability = this.ability.get();
			modifiers = ability.getModifiedDefaultAttributeModifiers(this, stack, modifiers);
		}
		return modifiers;
	}
	
	@Override
	public int getDyeColor(DyeColor dyeColor)
	{
		return ColorHelper.getVibrantColor(dyeColor);
	}
	
	@Override
	public int getDefaultDyeColor()
	{
		return ability.map(NanoSuitAbility::overrideDefaultColor).orElse(EIArmorMaterials.NANO_COLOR);
	}
	
	@Override
	public boolean getDefaultActivatedState()
	{
		return true;
	}
	
	@Override
	public void setActivated(Player player, ItemStack stack, boolean activated)
	{
		ability.ifPresent((ability) ->
		{
			ToggleableItem.super.setActivated(player, stack, activated);
			
			if(!player.level().isClientSide())
			{
				ability.onActivationChange(this, player, stack, activated);
			}
		});
	}
	
	@Override
	public void armorTick(LivingEntity entity, EquipmentSlot slot, ItemStack stack)
	{
		if(entity.level().isClientSide())
		{
			return;
		}
		
		ability.ifPresent((ability) ->
				ability.tick(this, entity, slot, stack));
	}
	
	@Override
	public void onUnequipArmor(LivingEntity entity, EquipmentSlot slot, ItemStack fromStack, ItemStack toStack)
	{
		if(!ItemStack.isSameItem(fromStack, toStack))
		{
			ability.ifPresent((ability) ->
					ability.onUnequip(this, entity, slot, fromStack, toStack));
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
	{
		ability.flatMap((a) -> a.getTooltipLines(this, stack))
				.ifPresent(tooltip::addAll);
	}
}
