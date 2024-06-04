package net.swedz.extended_industrialization.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;
import net.swedz.extended_industrialization.EIItems;

public final class TinCanFoodItem extends Item
{
	public TinCanFoodItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
	{
		ItemStack itemstack = super.finishUsingItem(stack, level, entity);
		if(entity instanceof Player player && !player.getAbilities().instabuild)
		{
			if(itemstack.isEmpty())
			{
				return new ItemStack(EIItems.TIN_CAN);
			}
			else
			{
				PlayerMainInvWrapper inventory = new PlayerMainInvWrapper(player.getInventory());
				ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, new ItemStack(EIItems.TIN_CAN), false);
				if(!remainder.isEmpty())
				{
					ItemHandlerHelper.giveItemToPlayer(player, remainder);
				}
			}
		}
		return itemstack;
	}
}
