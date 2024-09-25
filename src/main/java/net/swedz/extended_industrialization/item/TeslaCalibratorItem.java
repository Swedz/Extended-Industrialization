package net.swedz.extended_industrialization.item;

import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.machines.blockentity.TeslaReceiverMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.teslatower.TeslaTowerBlockEntity;

import java.util.List;

import static aztech.modern_industrialization.MITooltips.*;
import static net.swedz.extended_industrialization.EITooltips.*;

public final class TeslaCalibratorItem extends Item
{
	public TeslaCalibratorItem(Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
	{
		Player player = context.getPlayer();
		if(player != null)
		{
			InteractionHand usedHand = context.getHand();
			ItemStack itemStack = player.getItemInHand(usedHand);
			BlockEntity hitBlockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
			if(!context.getLevel().isClientSide())
			{
				if(player.isShiftKeyDown() && hitBlockEntity instanceof TeslaTowerBlockEntity)
				{
					itemStack.set(EIComponents.SELECTED_POSITION, GlobalPos.of(context.getLevel().dimension(), context.getClickedPos()));
					player.displayClientMessage(EIText.TESLA_CALIBRATOR_SELECTED.text(), true);
				}
				else if(!player.isShiftKeyDown() && hitBlockEntity instanceof TeslaReceiverMachineBlockEntity receiver)
				{
					// TODO set pos in receiver
					player.displayClientMessage(EIText.TESLA_CALIBRATOR_LINK_SUCCESS.text(), true);
				}
			}
			return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		if(player.isShiftKeyDown())
		{
			player.getItemInHand(usedHand).remove(EIComponents.SELECTED_POSITION);
			player.displayClientMessage(EIText.TESLA_CALIBRATOR_CLEAR.text(), true);
			return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide());
		}
		return super.use(level, player, usedHand);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		if(stack.has(EIComponents.SELECTED_POSITION))
		{
			GlobalPos position = stack.get(EIComponents.SELECTED_POSITION);
			tooltipComponents.add(EIText.TESLA_CALIBRATOR_LINKED.text(GLOBAL_POS_PARSER.parse(position)).withStyle(DEFAULT_STYLE));
		}
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return !newStack.is(oldStack.getItem());
	}
}
