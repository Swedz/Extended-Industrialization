package net.swedz.extended_industrialization.item;

import aztech.modern_industrialization.MIComponents;
import dev.technici4n.grandpower.api.ISimpleEnergyItem;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.tesseract.neoforge.item.ItemHurtHandler;

public class ElectricArmorItem extends ArmorItem implements ISimpleEnergyItem, ItemHurtHandler
{
	private final long energyCapacity;
	private final long damageCostEnergy;
	
	public ElectricArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties,
							 long energyCapacity, long damageCostEnergy)
	{
		super(material, type, properties.stacksTo(1));
		this.energyCapacity = energyCapacity;
		this.damageCostEnergy = damageCostEnergy;
	}
	
	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
	{
		return this.getModifiedDefaultAttributeModifiers(stack, this.getStoredEnergy(stack) > 0 ? defaultModifiers.get() : ItemAttributeModifiers.EMPTY);
	}
	
	public ItemAttributeModifiers getModifiedDefaultAttributeModifiers(ItemStack stack, ItemAttributeModifiers modifiers)
	{
		return modifiers;
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public DataComponentType<Long> getEnergyComponent()
	{
		return MIComponents.ENERGY.get();
	}
	
	@Override
	public long getEnergyCapacity(ItemStack stack)
	{
		return energyCapacity;
	}
	
	@Override
	public long getEnergyMaxInput(ItemStack stack)
	{
		return energyCapacity;
	}
	
	@Override
	public long getEnergyMaxOutput(ItemStack stack)
	{
		return energyCapacity;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return !stack.getOrDefault(EIComponents.HIDE_BAR, false);
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return (int) Math.round(this.getStoredEnergy(stack) / (double) energyCapacity * 13);
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		return 0xFF0000;
	}
	
	@Override
	public void onHurt(LivingEntity entity, ItemStack stack, int damageAmount)
	{
		if(this.getStoredEnergy(stack) > 0)
		{
			this.tryUseEnergy(stack, damageCostEnergy);
		}
	}
}
