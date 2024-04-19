package net.swedz.miextended.items;

import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public final class MIEItemProperties extends Item.Properties
{
	// region Inherited methods
	@Override
	public MIEItemProperties food(FoodProperties food)
	{
		super.food(food);
		return this;
	}
	
	@Override
	public MIEItemProperties stacksTo(int pMaxStackSize)
	{
		super.stacksTo(pMaxStackSize);
		return this;
	}
	
	@Override
	public MIEItemProperties defaultDurability(int pMaxDamage)
	{
		super.defaultDurability(pMaxDamage);
		return this;
	}
	
	@Override
	public MIEItemProperties durability(int pMaxDamage)
	{
		super.durability(pMaxDamage);
		return this;
	}
	
	@Override
	public MIEItemProperties craftRemainder(Item pCraftingRemainingItem)
	{
		super.craftRemainder(pCraftingRemainingItem);
		return this;
	}
	
	@Override
	public MIEItemProperties rarity(Rarity pRarity)
	{
		super.rarity(pRarity);
		return this;
	}
	
	@Override
	public MIEItemProperties fireResistant()
	{
		super.fireResistant();
		return this;
	}
	
	@Override
	public MIEItemProperties setNoRepair()
	{
		super.setNoRepair();
		return this;
	}
	
	@Override
	public MIEItemProperties requiredFeatures(FeatureFlag... pRequiredFeatures)
	{
		super.requiredFeatures(pRequiredFeatures);
		return this;
	}
	// endregion
}
