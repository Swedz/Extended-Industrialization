package net.swedz.extended_industrialization.machines.component.itemslot;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EIItems;

public class TeslaTowerUpgradeComponent extends SimpleItemStackComponent
{
	public TeslaTowerUpgradeComponent(UpdatedCallback callback)
	{
		super("tesla_tower_upgrade_stack", callback);
	}
	
	public TeslaTowerUpgradeComponent()
	{
		this(null);
	}
	
	public boolean isInterdimensional()
	{
		return this.getStack().is(EIItems.TESLA_INTERDIMENSIONAL_UPGRADE.asItem());
	}
	
	public ItemInteractionResult onUse(MachineBlockEntity blockEntity, Player player, InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		if(!stack.is(EIItems.TESLA_INTERDIMENSIONAL_UPGRADE.asItem()))
		{
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		if(this.getStack().isEmpty())
		{
			ItemStack copy = stack.copyWithCount(1);
			stack.consume(1, player);
			if(!blockEntity.getLevel().isClientSide())
			{
				this.setStackServer(blockEntity, copy);
			}
			return ItemInteractionResult.sidedSuccess(blockEntity.getLevel().isClientSide());
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}
}
