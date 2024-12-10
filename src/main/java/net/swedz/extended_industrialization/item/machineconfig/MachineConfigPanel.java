package net.swedz.extended_industrialization.item.machineconfig;

import aztech.modern_industrialization.MIItem;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.items.RedstoneControlModuleItem;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.DropableComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.components.UpgradeComponent;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.util.Simulation;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.api.ComponentStackHolder;
import net.swedz.extended_industrialization.mixin.mi.accessor.CasingComponentAccessor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public record MachineConfigPanel(
		Map<SlotPanel.SlotType, ItemStack> slotItems
) implements MachineConfigApplicable<MachineBlockEntity>
{
	public static final Codec<MachineConfigPanel> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.unboundedMap(
							Codec.STRING.xmap((key) -> SlotPanel.SlotType.valueOf(key.toUpperCase()), (slotType) -> slotType.name().toLowerCase()),
							ItemStack.CODEC
					).fieldOf("items").forGetter(MachineConfigPanel::slotItems)
			)
			.apply(instance, MachineConfigPanel::new));
	
	public static MachineConfigPanel from(MachineBlockEntity machine)
	{
		Map<SlotPanel.SlotType, ItemStack> slotItems = Maps.newHashMap();
		
		BiConsumer<SlotPanel.SlotType, DropableComponent> action = (slotType, component) ->
		{
			if(!component.getDrop().isEmpty())
			{
				slotItems.put(slotType, component.getDrop());
			}
		};
		
		machine.components.forType(RedstoneControlComponent.class, (component) -> action.accept(SlotPanel.SlotType.REDSTONE_MODULE, component));
		machine.components.forType(UpgradeComponent.class, (component) -> action.accept(SlotPanel.SlotType.UPGRADES, component));
		machine.components.forType(CasingComponent.class, (component) -> action.accept(SlotPanel.SlotType.CASINGS, component));
		
		return new MachineConfigPanel(slotItems);
	}
	
	private static void drop(MachineBlockEntity machine, ItemStack stack)
	{
		BlockPos blockPos = machine.getBlockPos();
		Containers.dropItemStack(
				machine.getLevel(),
				blockPos.getX(), blockPos.getY(), blockPos.getZ(),
				stack
		);
	}
	
	private static List<ItemStack> findItemsMatching(Inventory inventory, ItemStack itemStack)
	{
		List<ItemStack> items = Lists.newArrayList();
		for(int i = 0; i < inventory.items.size(); i++)
		{
			ItemStack slot = inventory.items.get(i);
			if(!slot.isEmpty() && ItemStack.isSameItem(itemStack, slot))
			{
				items.add(slot);
			}
		}
		return items;
	}
	
	@Override
	public boolean matches(MachineBlockEntity target)
	{
		return target.components.mapOrDefault(SlotPanel.Server.class, (component) -> true, false);
	}
	
	private boolean insertItemToRedstoneComponent(Player player, MachineBlockEntity target, RedstoneControlComponent component, ComponentStackHolder componentStackHolder, ItemStack item, Simulation simulation)
	{
		ItemStack componentItem = componentStackHolder.getStack();
		if(MIItem.REDSTONE_CONTROL_MODULE.is(item))
		{
			if(simulation.isActing())
			{
				ItemStack insertItem;
				if(componentItem.isEmpty())
				{
					insertItem = item.copyWithCount(1);
					item.consume(1, player);
				}
				else
				{
					insertItem = componentItem.copy();
				}
				RedstoneControlModuleItem.setRequiresLowSignal(insertItem, RedstoneControlModuleItem.isRequiresLowSignal(slotItems.get(SlotPanel.SlotType.REDSTONE_MODULE)));
				componentStackHolder.setStack(insertItem);
			}
			return true;
		}
		return false;
	}
	
	private boolean insertItemToUpgradeComponent(Player player, MachineBlockEntity target, UpgradeComponent component, ComponentStackHolder componentStackHolder, ItemStack item, Simulation simulation)
	{
		int desiredCount = slotItems.get(SlotPanel.SlotType.UPGRADES).getCount();
		ItemStack componentItem = componentStackHolder.getStack();
		if(UpgradeComponent.getExtraEu(item.getItem()) > 0)
		{
			boolean changed = false;
			if(componentItem.isEmpty())
			{
				if(simulation.isActing())
				{
					ItemStack insertItem = item.copyWithCount(Math.min(item.getCount(), desiredCount));
					componentStackHolder.setStack(insertItem);
					item.consume(insertItem.getCount(), player);
					changed = true;
				}
			}
			else if(item.getItem() == componentItem.getItem())
			{
				if(componentItem.getCount() < desiredCount)
				{
					int added = Math.min(item.getCount(), desiredCount - componentItem.getCount());
					changed = added > 0;
					if(simulation.isActing() && changed)
					{
						ItemStack insertItem = componentItem.copy();
						insertItem.grow(added);
						componentStackHolder.setStack(insertItem);
						item.consume(added, player);
					}
				}
				else if(componentItem.getCount() > desiredCount)
				{
					int subtracted = componentItem.getCount() - desiredCount;
					changed = subtracted > 0;
					if(simulation.isActing() && changed)
					{
						ItemStack insertItem = componentItem.copy();
						insertItem.shrink(subtracted);
						if(insertItem.getCount() == 0)
						{
							insertItem = ItemStack.EMPTY;
						}
						if(!player.hasInfiniteMaterials())
						{
							drop(target, componentItem.copyWithCount(subtracted));
						}
						componentStackHolder.setStack(insertItem);
					}
				}
			}
			else if(item.getItem() != componentItem.getItem())
			{
				if(simulation.isActing())
				{
					if(!player.hasInfiniteMaterials())
					{
						drop(target, componentItem.copy());
					}
					ItemStack insertItem = item.copyWithCount(Math.min(item.getCount(), desiredCount));
					componentStackHolder.setStack(insertItem);
					item.consume(insertItem.getCount(), player);
				}
				changed = true;
			}
			return changed;
		}
		return false;
	}
	
	private boolean insertItemToCasingComponent(Player player, MachineBlockEntity target, CasingComponent component, ComponentStackHolder componentStackHolder, ItemStack item, Simulation simulation)
	{
		CableTier currentTier = ((CasingComponentAccessor) component).getCurrentTier();
		ItemStack componentItem = componentStackHolder.getStack();
		if(!item.isEmpty())
		{
			CableTier newTier = CasingComponent.getCasingTier(item.getItem());
			if(newTier != null && newTier != currentTier)
			{
				if(simulation.isActing())
				{
					if(currentTier != CableTier.LV && !player.hasInfiniteMaterials())
					{
						drop(target, componentItem);
					}
					componentStackHolder.setStack(item.copyWithCount(1));
					item.consume(1, player);
				}
				return true;
			}
		}
		return false;
	}
	
	private <T> boolean insertItemToComponent(Player player, MachineBlockEntity target, T component, ComponentStackHolder componentStackHolder, ItemStack item, Simulation simulation)
	{
		return switch (component)
		{
			case RedstoneControlComponent redstoneComponent ->
					this.insertItemToRedstoneComponent(player, target, redstoneComponent, componentStackHolder, item, simulation);
			case UpgradeComponent upgradeComponent ->
					this.insertItemToUpgradeComponent(player, target, upgradeComponent, componentStackHolder, item, simulation);
			case CasingComponent casingComponent ->
					this.insertItemToCasingComponent(player, target, casingComponent, componentStackHolder, item, simulation);
			default -> false;
		};
	}
	
	private <T> boolean insertToComponent(Player player, MachineBlockEntity target, T component, SlotPanel.SlotType slotType, Simulation simulation)
	{
		if(!(component instanceof ComponentStackHolder componentStackHolder))
		{
			return false;
		}
		boolean success = false;
		
		ItemStack slotItem = slotItems.get(slotType);
		
		List<ItemStack> matchingItems = player.hasInfiniteMaterials() ? List.of(slotItem.copy()) : findItemsMatching(player.getInventory(), slotItem);
		
		if(matchingItems.isEmpty() && componentStackHolder instanceof RedstoneControlComponent && !componentStackHolder.getStack().isEmpty())
		{
			matchingItems.add(componentStackHolder.getStack().copy());
		}
		
		for(ItemStack matchingItem : matchingItems)
		{
			if(this.insertItemToComponent(player, target, component, componentStackHolder, matchingItem, simulation))
			{
				success = true;
			}
			else
			{
				break;
			}
		}
		return success;
	}
	
	private <T> boolean dropFromComponent(Player player, MachineBlockEntity target, T component, ComponentStackHolder componentStackHolder, SlotPanel.SlotType slotType, Simulation simulation)
	{
		ItemStack componentItem = componentStackHolder.getStack();
		if(!componentItem.isEmpty())
		{
			if(!player.hasInfiniteMaterials())
			{
				drop(target, componentItem.copy());
			}
			componentStackHolder.setStack(ItemStack.EMPTY);
			return true;
		}
		return false;
	}
	
	private <T> boolean applyComponent(Player player, MachineBlockEntity target, Class<T> componentType, SlotPanel.SlotType slotType, Simulation simulation)
	{
		AtomicBoolean success = new AtomicBoolean(false);
		
		Inventory inventory = player.getInventory();
		
		target.components.forType(componentType, (component) ->
		{
			if(slotItems.containsKey(slotType))
			{
				if(this.insertToComponent(player, target, component, slotType, simulation))
				{
					success.set(true);
				}
			}
			else if(component instanceof ComponentStackHolder componentStackHolder)
			{
				if(this.dropFromComponent(player, target, component, componentStackHolder, slotType, simulation))
				{
					success.set(true);
				}
			}
		});
		
		return success.get();
	}
	
	@Override
	public boolean apply(Player player, MachineBlockEntity target, Simulation simulation)
	{
		boolean redstone = this.applyComponent(player, target, RedstoneControlComponent.class, SlotPanel.SlotType.REDSTONE_MODULE, simulation);
		boolean upgrade = this.applyComponent(player, target, UpgradeComponent.class, SlotPanel.SlotType.UPGRADES, simulation);
		boolean casing = this.applyComponent(player, target, CasingComponent.class, SlotPanel.SlotType.CASINGS, simulation);
		return redstone || upgrade || casing;
	}
}
