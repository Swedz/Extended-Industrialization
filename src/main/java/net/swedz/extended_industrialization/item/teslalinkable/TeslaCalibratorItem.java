package net.swedz.extended_industrialization.item.teslalinkable;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.machines.component.tesla.network.receiver.TeslaReceiver;
import net.swedz.extended_industrialization.machines.component.tesla.network.transmitter.TeslaTransmitter;
import net.swedz.tesseract.neoforge.api.WorldPos;
import net.swedz.tesseract.neoforge.tooltip.component.ItemStackTooltipComponent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = EI.ID)
public final class TeslaCalibratorItem extends Item
{
	public TeslaCalibratorItem(Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	private static void onPlaceReceiverWithCalibrator(BlockEvent.EntityPlaceEvent event)
	{
		if(event.getEntity() instanceof Player player)
		{
			ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
			if(offhand.has(EIComponents.SELECTED_TESLA_NETWORK))
			{
				WorldPos key = offhand.get(EIComponents.SELECTED_TESLA_NETWORK).key();
				
				BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
				if(blockEntity instanceof MachineBlockEntity machine &&
				   blockEntity instanceof TeslaReceiver receiver)
				{
					receiver.setNetwork(key);
					machine.setChanged();
					machine.sync();
					player.displayClientMessage(EI.text().teslaCalibratorLinkSuccess(), true);
				}
			}
		}
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
			if(player.isShiftKeyDown() &&
			   hitBlockEntity instanceof TeslaTransmitter transmitter)
			{
				if(!client)
				{
					itemStack.set(EIComponents.SELECTED_TESLA_NETWORK, new SelectedTeslaNetwork(transmitter.getPosition(), context.getLevel().getBlockState(context.getClickedPos()).getBlock()));
					player.displayClientMessage(EI.text().teslaCalibratorSelected(), true);
				}
				return InteractionResult.sidedSuccess(client);
			}
			else if(!player.isShiftKeyDown() &&
					hitBlockEntity instanceof MachineBlockEntity machine &&
					hitBlockEntity instanceof TeslaReceiver receiver)
			{
				if(!client)
				{
					if(itemStack.has(EIComponents.SELECTED_TESLA_NETWORK))
					{
						WorldPos key = itemStack.get(EIComponents.SELECTED_TESLA_NETWORK).key();
						receiver.setNetwork(key);
						machine.setChanged();
						machine.sync();
						player.displayClientMessage(EI.text().teslaCalibratorLinkSuccess(), true);
					}
					else
					{
						player.displayClientMessage(EI.text().teslaCalibratorLinkFailedNoSelection(), true);
					}
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
			player.displayClientMessage(EI.text().teslaCalibratorClear(), true);
			return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide());
		}
		return super.use(level, player, usedHand);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag isAdvanced)
	{
		if(stack.has(EIComponents.SELECTED_TESLA_NETWORK))
		{
			var key = stack.get(EIComponents.SELECTED_TESLA_NETWORK).key();
			lines.add(EI.text().teslaCalibratorLinked(key));
		}
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
		if(stack.has(EIComponents.SELECTED_TESLA_NETWORK))
		{
			Block machineBlock = stack.get(EIComponents.SELECTED_TESLA_NETWORK).block();
			Item item = machineBlock.asItem();
			return Optional.of(new ItemStackTooltipComponent(item.getDefaultInstance()));
		}
		return Optional.empty();
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return !newStack.is(oldStack.getItem());
	}
}
