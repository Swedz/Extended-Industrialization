package net.swedz.extended_industrialization.item.nanosuit.decoration;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface NanoSuitDecoration
{
	WingNanoDecoration WING = new WingNanoDecoration();
	MeowNanoDecoration MEOW = new MeowNanoDecoration();
	
	static List<NanoSuitDecoration> values()
	{
		return List.of(WING, MEOW);
	}
	
	boolean isActiveFor(NanoSuitArmorItem item, ItemStack stack);
	
	ArmorItem.Type armorType();
	
	default EquipmentSlot equipmentSlot()
	{
		return this.armorType().getSlot();
	}
	
	default EquipmentSlotGroup equipmentSlotGroup()
	{
		return EquipmentSlotGroup.bySlot(this.equipmentSlot());
	}
	
	default String getDescriptionId(NanoSuitArmorItem item, ItemStack stack)
	{
		return null;
	}
	
	default Optional<List<Component>> getTooltipLines(NanoSuitArmorItem item, ItemStack stack)
	{
		return Optional.empty();
	}
	
	default ItemProperty itemProperty(NanoSuitArmorItem item)
	{
		return null;
	}
	
	record ItemProperty(ResourceLocation id, String model, Supplier<DataComponentType<Boolean>> component)
	{
	}
}
