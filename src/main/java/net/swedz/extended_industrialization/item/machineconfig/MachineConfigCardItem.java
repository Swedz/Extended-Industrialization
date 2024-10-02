package net.swedz.extended_industrialization.item.machineconfig;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.util.Simulation;
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
import net.swedz.extended_industrialization.EIText;

import java.util.List;
import java.util.Optional;

import static aztech.modern_industrialization.MITooltips.*;

@EventBusSubscriber(modid = EI.ID)
public final class MachineConfigCardItem extends Item
{
	public MachineConfigCardItem(Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	private static void onPlaceMachineWithConfig(BlockEvent.EntityPlaceEvent event)
	{
		if(event.getEntity() instanceof Player player)
		{
			ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
			if(offhand.has(EIComponents.MACHINE_CONFIG))
			{
				MachineConfig config = offhand.get(EIComponents.MACHINE_CONFIG);
				
				BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
				if(blockEntity instanceof MachineBlockEntity machine &&
				   !(blockEntity instanceof MultiblockMachineBlockEntity))
				{
					if(config.apply(player, machine, Simulation.SIMULATE))
					{
						config.apply(player, machine, Simulation.ACT);
						player.displayClientMessage(EIText.MACHINE_CONFIG_CARD_APPLY_SUCCESS.text(), true);
					}
					else
					{
						player.displayClientMessage(EIText.MACHINE_CONFIG_CARD_APPLY_FAILED.text(), true);
					}
				}
			}
		}
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
			if(hitBlockEntity instanceof MachineBlockEntity machine &&
			   !(hitBlockEntity instanceof MultiblockMachineBlockEntity))
			{
				if(!context.getLevel().isClientSide())
				{
					if(player.isShiftKeyDown())
					{
						MachineConfig config = MachineConfig.from(machine);
						itemStack.set(EIComponents.MACHINE_CONFIG, config);
						player.displayClientMessage(EIText.MACHINE_CONFIG_CARD_SAVE.text(), true);
					}
					else
					{
						if(itemStack.has(EIComponents.MACHINE_CONFIG))
						{
							MachineConfig config = itemStack.get(EIComponents.MACHINE_CONFIG);
							
							if(config.apply(player, machine, Simulation.SIMULATE))
							{
								config.apply(player, machine, Simulation.ACT);
								player.displayClientMessage(EIText.MACHINE_CONFIG_CARD_APPLY_SUCCESS.text(), true);
							}
							else
							{
								player.displayClientMessage(EIText.MACHINE_CONFIG_CARD_APPLY_FAILED.text(), true);
							}
						}
					}
				}
				return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
			}
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		if(player.isShiftKeyDown())
		{
			player.getItemInHand(usedHand).remove(EIComponents.MACHINE_CONFIG);
			player.displayClientMessage(EIText.MACHINE_CONFIG_CARD_CLEAR.text(), true);
			return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide());
		}
		return super.use(level, player, usedHand);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		if(stack.has(EIComponents.MACHINE_CONFIG))
		{
			Block machineBlock = stack.get(EIComponents.MACHINE_CONFIG).machineBlock();
			tooltipComponents.add(EIText.MACHINE_CONFIG_CARD_CONFIGURED.arg(machineBlock.asItem(), ITEM_PARSER));
		}
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
		if(stack.has(EIComponents.MACHINE_CONFIG))
		{
			Block machineBlock = stack.get(EIComponents.MACHINE_CONFIG).machineBlock();
			Item item = machineBlock.asItem();
			return Optional.of(new TooltipData(item.getDefaultInstance()));
		}
		return Optional.empty();
	}
	
	public record TooltipData(ItemStack machineItemStack) implements TooltipComponent
	{
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return !newStack.is(oldStack.getItem());
	}
}
