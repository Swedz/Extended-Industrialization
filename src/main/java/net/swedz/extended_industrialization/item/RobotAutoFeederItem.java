package net.swedz.extended_industrialization.item;

import aztech.modern_industrialization.MIComponents;
import aztech.modern_industrialization.api.energy.CableTier;
import com.google.common.collect.Lists;
import dev.technici4n.grandpower.api.ISimpleEnergyItem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.proxy.modslot.EIModSlotProxy;
import net.swedz.tesseract.neoforge.proxy.Proxies;

import java.util.List;
import java.util.Optional;

public final class RobotAutoFeederItem extends Item implements ISimpleEnergyItem
{
	private static final long EAT_ENERGY_COST = 256;
	private static final long ENERGY_CAPACITY = CableTier.LV.getMaxTransfer() * EAT_ENERGY_COST;
	
	public RobotAutoFeederItem(Properties properties)
	{
		super(properties
				.stacksTo(1)
				.component(EIComponents.HIDE_BAR, false)
				.component(MIComponents.ENERGY, 0L));
	}
	
	@Override
	public DataComponentType<Long> getEnergyComponent()
	{
		return MIComponents.ENERGY.get();
	}
	
	@Override
	public long getEnergyCapacity(ItemStack stack)
	{
		return ENERGY_CAPACITY;
	}
	
	@Override
	public long getEnergyMaxInput(ItemStack stack)
	{
		return ENERGY_CAPACITY;
	}
	
	@Override
	public long getEnergyMaxOutput(ItemStack stack)
	{
		return ENERGY_CAPACITY;
	}
	
	public boolean hasEnergy(ItemStack stack)
	{
		return this.getStoredEnergy(stack) > 0;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return !stack.getOrDefault(EIComponents.HIDE_BAR, false);
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return (int) Math.round(this.getStoredEnergy(stack) / (double) ENERGY_CAPACITY * 13);
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		return 0xFF0000;
	}
	
	private ItemStack extractCannedFood(Player player)
	{
		List<ItemStack> contents = Lists.newArrayList();
		contents.addAll(player.getInventory().items);
		contents.addAll(Proxies.get(EIModSlotProxy.class).getContents(player, (stack) -> true));
		for(var stack : contents)
		{
			if(stack.is(EIItems.CANNED_FOOD.asItem()))
			{
				ItemStack extracted = stack.copyWithCount(1);
				stack.consume(1, player);
				return extracted;
			}
			var capability = stack.getCapability(Capabilities.ItemHandler.ITEM);
			if(capability != null)
			{
				for(int slot = 0; slot < capability.getSlots(); slot++)
				{
					var innerStack = capability.getStackInSlot(slot);
					if(innerStack.is(EIItems.CANNED_FOOD.asItem()))
					{
						return capability.extractItem(slot, 1, false);
					}
				}
			}
		}
		return ItemStack.EMPTY;
	}
	
	/**
	 * I would just use {@link Player#eat(Level, ItemStack, FoodProperties)} however that conditionally adds to the
	 * inventory on its own and I want to do that myself and there's no good way to prevent double adding.
	 */
	private ItemStack eat(Player player, ItemStack stack, FoodProperties food)
	{
		var level = player.level();
		
		player.getFoodData().eat(food);
		player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
		level.playSound(
				null,
				player.getX(), player.getY(), player.getZ(),
				SoundEvents.PLAYER_BURP,
				SoundSource.PLAYERS,
				0.5f,
				level.random.nextFloat() * 0.1f + 0.9f
		);
		if(player instanceof ServerPlayer serverPlayer)
		{
			CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
		}
		
		level.playSound(
				null,
				player.getX(), player.getY(), player.getZ(),
				player.getEatingSound(stack),
				SoundSource.NEUTRAL,
				1,
				1 + (level.random.nextFloat() - level.random.nextFloat()) * 0.4f
		);
		player.addEatEffect(food);
		stack.consume(1, player);
		player.gameEvent(GameEvent.EAT);
		
		Optional<ItemStack> container = food.usingConvertsTo();
		if(container.isPresent() && !player.hasInfiniteMaterials())
		{
			return container.get().copy();
		}
		return ItemStack.EMPTY;
	}
	
	private ItemStack eat(Player player, ItemStack stack)
	{
		FoodProperties food = stack.getFoodProperties(player);
		return food == null ? ItemStack.EMPTY : this.eat(player, stack, food);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected)
	{
		if(level.isClientSide() || level.getGameTime() % 20 != 0)
		{
			return;
		}
		if(entity instanceof Player player &&
		   player.getFoodData().getFoodLevel() <= 18 &&
		   this.hasEnergy(stack))
		{
			var cannedFoodStack = this.extractCannedFood(player);
			if(!cannedFoodStack.isEmpty())
			{
				var container = this.eat(player, cannedFoodStack);
				if(!container.isEmpty() &&
				   !player.getInventory().add(container))
				{
					player.drop(container, false);
				}
				this.tryUseEnergy(stack, EAT_ENERGY_COST);
			}
		}
	}
}
