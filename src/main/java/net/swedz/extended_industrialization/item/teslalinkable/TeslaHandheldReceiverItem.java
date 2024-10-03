package net.swedz.extended_industrialization.item.teslalinkable;

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
import net.swedz.extended_industrialization.api.WorldPos;
import net.swedz.extended_industrialization.machines.component.tesla.transmitter.TeslaTransmitter;

import java.util.List;

import static aztech.modern_industrialization.MITooltips.*;
import static net.swedz.extended_industrialization.EITooltips.*;

public final class TeslaHandheldReceiverItem extends Item
{
	public TeslaHandheldReceiverItem(Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
	{
		boolean client = context.getLevel().isClientSide();
		Player player = context.getPlayer();
		if(player != null)
		{
			InteractionHand usedHand = context.getHand();
			ItemStack itemStack = player.getItemInHand(usedHand);
			BlockEntity hitBlockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
			if(hitBlockEntity instanceof TeslaTransmitter)
			{
				if(!client)
				{
					itemStack.set(EIComponents.SELECTED_TESLA_NETWORK, new WorldPos(context.getLevel(), context.getClickedPos()));
					player.displayClientMessage(EIText.TESLA_HANDHELD_SELECTED.text(), true);
				}
				return InteractionResult.sidedSuccess(client);
			}
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		if(player.isShiftKeyDown())
		{
			player.getItemInHand(usedHand).remove(EIComponents.SELECTED_TESLA_NETWORK);
			player.displayClientMessage(EIText.TESLA_HANDHELD_CLEAR.text(), true);
			return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide());
		}
		return super.use(level, player, usedHand);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		if(stack.has(EIComponents.SELECTED_TESLA_NETWORK))
		{
			WorldPos key = stack.get(EIComponents.SELECTED_TESLA_NETWORK);
			tooltipComponents.add(EIText.TESLA_HANDHELD_LINKED.text(TESLA_NETWORK_KEY_PARSER.parse(key)).withStyle(DEFAULT_STYLE));
		}
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return !newStack.is(oldStack.getItem());
	}
}
