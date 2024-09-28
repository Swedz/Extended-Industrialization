package net.swedz.extended_industrialization.item.teslalinkable;

import aztech.modern_industrialization.api.energy.EnergyApi;
import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
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
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetwork;
import net.swedz.extended_industrialization.machines.component.tesla.TeslaNetworkCache;
import net.swedz.extended_industrialization.machines.component.tesla.transmitter.TeslaTransmitter;
import net.swedz.extended_industrialization.proxy.modslot.EIModSlotProxy;
import net.swedz.tesseract.neoforge.compat.mi.helper.ChargeInventoryHelper;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.List;

import static aztech.modern_industrialization.MITooltips.*;
import static net.swedz.extended_industrialization.EITooltips.*;

public final class TeslaCarryOnReceiverItem extends Item
{
	public TeslaCarryOnReceiverItem(Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	private long charge(Player player, long maxEu)
	{
		Inventory inventory = player.getInventory();
		
		List<ItemStack> items = Lists.newArrayList();
		items.addAll(inventory.armor);
		items.addAll(inventory.items);
		items.addAll(inventory.offhand);
		items.addAll(Proxies.get(EIModSlotProxy.class).getContents(player, (stack) -> stack.getCapability(EnergyApi.ITEM) != null));
		
		return ChargeInventoryHelper.charge(items, maxEu, false);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected)
	{
		if(level.isClientSide())
		{
			return;
		}
		
		if(entity instanceof Player player && stack.has(EIComponents.SELECTED_TESLA_NETWORK))
		{
			WorldPos networkKey = stack.get(EIComponents.SELECTED_TESLA_NETWORK);
			TeslaNetworkCache cache = level.getServer().getTeslaNetworks();
			if(cache.exists(networkKey))
			{
				TeslaNetwork network = cache.get(networkKey);
				// TODO check if player is within range
				if(network.canExtract())
				{
					// TODO limit transmit rate
					long eu = Long.MAX_VALUE;
					eu = network.extract(eu, true);
					eu = this.charge(player, eu);
					network.extract(eu, false);
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
			if(!context.getLevel().isClientSide())
			{
				if(hitBlockEntity instanceof TeslaTransmitter)
				{
					itemStack.set(EIComponents.SELECTED_TESLA_NETWORK, new WorldPos(context.getLevel(), context.getClickedPos()));
					player.displayClientMessage(EIText.TESLA_CARRYON_SELECTED.text(), true);
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
			player.getItemInHand(usedHand).remove(EIComponents.SELECTED_TESLA_NETWORK);
			player.displayClientMessage(EIText.TESLA_CARRYON_CLEAR.text(), true);
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
			tooltipComponents.add(EIText.TESLA_CARRYON_LINKED.text(TESLA_NETWORK_KEY_PARSER.parse(key)).withStyle(DEFAULT_STYLE));
		}
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return !newStack.is(oldStack.getItem());
	}
}
