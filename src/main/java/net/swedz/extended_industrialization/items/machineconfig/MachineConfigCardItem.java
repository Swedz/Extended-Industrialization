package net.swedz.extended_industrialization.items.machineconfig;

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
import net.swedz.extended_industrialization.EIDataComponents;
import net.swedz.extended_industrialization.EIText;

import java.util.List;
import java.util.Optional;

import static aztech.modern_industrialization.MITooltips.*;

public final class MachineConfigCardItem extends Item
{
	public MachineConfigCardItem(Properties properties)
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
			if(hitBlockEntity instanceof MachineBlockEntity machine &&
			   !(hitBlockEntity instanceof MultiblockMachineBlockEntity))
			{
				if(!context.getLevel().isClientSide())
				{
					if(player.isShiftKeyDown())
					{
						MachineConfig config = MachineConfig.from(machine);
						itemStack.set(EIDataComponents.MACHINE_CONFIG, config);
						player.displayClientMessage(EIText.MACHINE_CONFIG_CARD_SAVE.text(), true);
					}
					else
					{
						if(itemStack.has(EIDataComponents.MACHINE_CONFIG))
						{
							MachineConfig config = itemStack.get(EIDataComponents.MACHINE_CONFIG);
							
							if(config.apply(machine, Simulation.SIMULATE))
							{
								config.apply(machine, Simulation.ACT);
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
			player.getItemInHand(usedHand).remove(EIDataComponents.MACHINE_CONFIG);
			player.displayClientMessage(EIText.MACHINE_CONFIG_CARD_CLEAR.text(), true);
			return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide());
		}
		return super.use(level, player, usedHand);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		if(stack.has(EIDataComponents.MACHINE_CONFIG))
		{
			Block machineBlock = stack.get(EIDataComponents.MACHINE_CONFIG).machineBlock();
			tooltipComponents.add(EIText.MACHINE_CONFIG_CARD_CONFIGURED.text(ITEM_PARSER.parse(machineBlock.asItem())));
		}
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
		if(stack.has(EIDataComponents.MACHINE_CONFIG))
		{
			Block machineBlock = stack.get(EIDataComponents.MACHINE_CONFIG).machineBlock();
			Item item = machineBlock.asItem();
			return Optional.of(new TooltipData(item.getDefaultInstance()));
		}
		return Optional.empty();
	}
	
	public record TooltipData(ItemStack machineItemStack) implements TooltipComponent
	{
	}
}
