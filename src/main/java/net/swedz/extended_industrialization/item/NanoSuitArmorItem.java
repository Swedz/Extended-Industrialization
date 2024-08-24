package net.swedz.extended_industrialization.item;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class NanoSuitArmorItem extends ElectricArmorItem
{
	public NanoSuitArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties, long energyCapacity)
	{
		super(material, type, properties, energyCapacity);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected)
	{
		if(level.isClientSide())
		{
			return;
		}
		
		if(type == Type.HELMET && entity instanceof Player player && stack == player.getItemBySlot(EquipmentSlot.HEAD))
		{
			// TODO night vision
		}
	}
}
