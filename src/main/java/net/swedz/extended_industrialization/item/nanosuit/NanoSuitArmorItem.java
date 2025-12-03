package net.swedz.extended_industrialization.item.nanosuit;

import aztech.modern_industrialization.MIRegistries;
import aztech.modern_industrialization.api.energy.CableTier;
import com.google.common.collect.Lists;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIArmorMaterials;
import net.swedz.extended_industrialization.item.ElectricArmorItem;
import net.swedz.extended_industrialization.item.ToggleableItem;
import net.swedz.extended_industrialization.item.nanosuit.ability.NanoSuitAbility;
import net.swedz.extended_industrialization.item.nanosuit.decoration.NanoSuitDecoration;
import net.swedz.tesseract.neoforge.helper.ColorHelper;
import net.swedz.tesseract.neoforge.item.ArmorTickHandler;
import net.swedz.tesseract.neoforge.item.ArmorUnequippedHandler;
import net.swedz.tesseract.neoforge.item.DynamicDyedItem;
import net.swedz.tesseract.neoforge.item.ItemHurtHandler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = EI.ID)
public final class NanoSuitArmorItem extends ElectricArmorItem implements ArmorTickHandler, ArmorUnequippedHandler, ItemHurtHandler, ToggleableItem, DynamicDyedItem
{
	private static final long DEFAULT_ENERGY_CAPACITY = 60 * 20 * CableTier.MV.getMaxTransfer();
	private static final long DAMAGE_ENERGY           = 1024;
	
	private final Optional<NanoSuitAbility> ability;
	private final boolean                   quantum;
	
	public NanoSuitArmorItem(
			Holder<ArmorMaterial> material, Type type, Properties properties,
			Optional<NanoSuitAbility> ability, boolean quantum
	)
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
		this.quantum = quantum;
	}
	
	public Optional<NanoSuitAbility> ability()
	{
		return ability;
	}
	
	public boolean hasAbility(Class<? extends NanoSuitAbility> abilityClass)
	{
		return ability.filter((a) -> abilityClass.isAssignableFrom(a.getClass())).isPresent();
	}
	
	public boolean isQuantum()
	{
		return quantum;
	}
	
	public List<NanoSuitDecoration> decorations(ItemStack stack)
	{
		List<NanoSuitDecoration> decorations = Lists.newArrayList();
		for(var decoration : NanoSuitDecoration.values())
		{
			if(decoration.armorType() == type && (stack == null || decoration.isActiveFor(this, stack)))
			{
				decorations.add(decoration);
			}
		}
		return Collections.unmodifiableList(decorations);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return !quantum && super.isBarVisible(stack);
	}
	
	@Override
	public long getEnergyCapacity(ItemStack stack)
	{
		return quantum ? 0 : super.getEnergyCapacity(stack);
	}
	
	@Override
	public boolean hasEnergy(ItemStack stack)
	{
		return quantum || super.hasEnergy(stack);
	}
	
	@Override
	public ItemAttributeModifiers getModifiedDefaultAttributeModifiers(ItemStack stack, ItemAttributeModifiers modifiers)
	{
		if(ability.isPresent())
		{
			var ability = this.ability.get();
			modifiers = ability.getModifiedDefaultAttributeModifiers(this, stack, modifiers);
		}
		if(quantum)
		{
			modifiers = modifiers.withModifierAdded(
					MIRegistries.QUANTUM_ARMOR,
					new AttributeModifier(
							EI.id("nano_quantum_armor_%s".formatted(type.getName())),
							1,
							AttributeModifier.Operation.ADD_VALUE
					),
					EquipmentSlotGroup.bySlot(this.getEquipmentSlot())
			);
		}
		return modifiers;
	}
	
	@SubscribeEvent
	private static void getAttributeModifiers(ItemAttributeModifierEvent event)
	{
		var stack = event.getItemStack();
		if(stack.getItem() instanceof NanoSuitArmorItem item &&
		   !item.isQuantum() &&
		   !item.hasEnergy(stack))
		{
			event.clearModifiers();
		}
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
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access)
	{
		if(ability.isPresent() && action == ClickAction.SECONDARY && other.isEmpty())
		{
			this.setActivated(player, stack, !this.isActivated(stack));
			if(player.level().isClientSide())
			{
				player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
			}
			return true;
		}
		return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
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
	public String getDescriptionId(ItemStack stack)
	{
		for(var decoration : this.decorations(stack))
		{
			String descriptionId = decoration.getDescriptionId(this, stack);
			if(descriptionId != null)
			{
				return descriptionId;
			}
		}
		return super.getDescriptionId(stack);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
	{
		for(var decoration : this.decorations(stack))
		{
			decoration.getTooltipLines(this, stack).ifPresent(tooltip::addAll);
		}
		
		ability.flatMap((a) -> a.getTooltipLines(this, stack))
				.ifPresent(tooltip::addAll);
	}
}
