package net.swedz.extended_industrialization.item.machineconfig;

import aztech.modern_industrialization.MIItem;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.items.RedstoneControlModuleItem;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.OverdriveComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.components.UpgradeComponent;
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
import net.swedz.extended_industrialization.machines.component.craft.processingarray.ProcessingArrayMachineComponent;
import net.swedz.extended_industrialization.machines.guicomponent.processingarraymachineslot.ProcessingArrayMachineSlot;
import net.swedz.tesseract.neoforge.compat.mi.api.ComponentStackHolder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public record MachineConfigPanel(
		Map<String, ItemStack> slotItems
) implements MachineConfigApplicable<MachineBlockEntity>
{
	public static final Codec<MachineConfigPanel> CODEC = RecordCodecBuilder.create((instance) -> instance
			.group(
					Codec.unboundedMap(Codec.STRING, ItemStack.CODEC).fieldOf("items").forGetter(MachineConfigPanel::slotItems)
			)
			.apply(instance, MachineConfigPanel::new));
	
	private static Map<String, RegisteredComponentType> REGISTERED_COMPONENT_TYPES = Maps.newHashMap();
	private static Map<Class<?>, String>                REGISTERED_COMPONENT_TYPES_BY_TYPE = Maps.newHashMap();
	
	/**
	 * Register a component for slot handling. Use this if you have custom component types that hold items you want supported by the machine config card. All component types to be registered should be registered on startup.
	 *
	 * @param key           the unique string key used in the config card item component
	 * @param componentType the machine component type to register for
	 * @param handler       the handler called when applying a component to a machinne
	 * @param <T>           the component type
	 */
	public static <T> void register(String key, Class<T> componentType, ComponentTypeHandler<T> handler)
	{
		if(REGISTERED_COMPONENT_TYPES.containsKey(key))
		{
			throw new IllegalArgumentException("There already exists a registered component type for the key '" + key + "'");
		}
		else if(REGISTERED_COMPONENT_TYPES_BY_TYPE.containsKey(componentType))
		{
			throw new IllegalArgumentException("There already exists a registered component type for the type '" + componentType + "'");
		}
		else if(!ComponentStackHolder.class.isAssignableFrom(componentType))
		{
			throw new IllegalArgumentException("The component type '" + componentType + "' must implement ComponentStackHolder");
		}
		var registeredType = new RegisteredComponentType(componentType, handler);
		REGISTERED_COMPONENT_TYPES.put(key, registeredType);
		REGISTERED_COMPONENT_TYPES_BY_TYPE.put(componentType, key);
	}
	
	static
	{
		register("redstone_module", RedstoneControlComponent.class, (player, target, component, holder, slotItem, item, simulation) ->
		{
			ItemStack componentItem = holder.getStack();
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
					RedstoneControlModuleItem.setRequiresLowSignal(insertItem, RedstoneControlModuleItem.isRequiresLowSignal(slotItem));
					holder.setStack(insertItem);
				}
				return true;
			}
			return false;
		});
		
		register("upgrades", UpgradeComponent.class, (player, target, component, holder, slotItem, item, simulation) ->
		{
			if(UpgradeComponent.getExtraEu(item.getItem()) > 0)
			{
				return ComponentTypeHandler.multiInsert(player, target, holder, slotItem, item, simulation);
			}
			return false;
		});
		
		register("casings", CasingComponent.class, (player, target, component, holder, slotItem, item, simulation) ->
		{
			CableTier currentTier = component.getCableTier();
			ItemStack componentItem = holder.getStack();
			if(!item.isEmpty())
			{
				CableTier newTier = CasingComponent.getCasingTier(item.getItem());
				if(newTier != null && newTier != currentTier)
				{
					if(simulation.isActing())
					{
						if(currentTier != CableTier.LV && !player.hasInfiniteMaterials())
						{
							ComponentTypeHandler.drop(target, componentItem);
						}
						holder.setStack(item.copyWithCount(1));
						item.consume(1, player);
					}
					return true;
				}
			}
			return false;
		});
		
		register("overdrive_module", OverdriveComponent.class, (player, target, component, holder, slotItem, item, simulation) ->
		{
			ItemStack componentItem = holder.getStack();
			if(MIItem.OVERDRIVE_MODULE.is(item))
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
					holder.setStack(insertItem);
				}
				return true;
			}
			return false;
		});
		
		register("processing_array_machines", ProcessingArrayMachineComponent.class, (player, target, component, holder, slotItem, item, simulation) ->
		{
			if(ProcessingArrayMachineSlot.isMachine(item))
			{
				return ComponentTypeHandler.multiInsert(player, target, holder, slotItem, item, simulation);
			}
			return false;
		});
	}
	
	private record RegisteredComponentType<T>(Class<T> componentType, ComponentTypeHandler<T> handler)
	{
	}
	
	public interface ComponentTypeHandler<T>
	{
		boolean handle(Player player, MachineBlockEntity target, T component, ComponentStackHolder holder, ItemStack slotItem, ItemStack item, Simulation simulation);
		
		static void drop(MachineBlockEntity machine, ItemStack stack)
		{
			BlockPos blockPos = machine.getBlockPos();
			Containers.dropItemStack(
					machine.getLevel(),
					blockPos.getX(), blockPos.getY(), blockPos.getZ(),
					stack
			);
		}
		
		static boolean multiInsert(Player player, MachineBlockEntity target, ComponentStackHolder holder, ItemStack slotItem, ItemStack item, Simulation simulation)
		{
			int desiredCount = slotItem.getCount();
			ItemStack componentItem = holder.getStack();
			boolean changed = false;
			if(componentItem.isEmpty())
			{
				if(simulation.isActing())
				{
					ItemStack insertItem = item.copyWithCount(Math.min(item.getCount(), desiredCount));
					holder.setStack(insertItem);
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
						holder.setStack(insertItem);
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
							ComponentTypeHandler.drop(target, componentItem.copyWithCount(subtracted));
						}
						holder.setStack(insertItem);
					}
				}
			}
			else if(item.getItem() != componentItem.getItem())
			{
				if(simulation.isActing())
				{
					if(!player.hasInfiniteMaterials())
					{
						ComponentTypeHandler.drop(target, componentItem.copy());
					}
					ItemStack insertItem = item.copyWithCount(Math.min(item.getCount(), desiredCount));
					holder.setStack(insertItem);
					item.consume(insertItem.getCount(), player);
				}
				changed = true;
			}
			return changed;
		}
	}
	
	public static MachineConfigPanel from(MachineBlockEntity machine)
	{
		Map<String, ItemStack> slotItems = Maps.newHashMap();
		for(var componentType : REGISTERED_COMPONENT_TYPES_BY_TYPE.keySet())
		{
			String key = REGISTERED_COMPONENT_TYPES_BY_TYPE.get(componentType);
			machine.components.forType(componentType, (component) ->
			{
				if(component instanceof ComponentStackHolder holder)
				{
					ItemStack drop = holder.getStack().copy();
					if(!drop.isEmpty())
					{
						slotItems.put(key, drop);
					}
				}
			});
		}
		return new MachineConfigPanel(slotItems);
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
		for(var componentType : REGISTERED_COMPONENT_TYPES_BY_TYPE.keySet())
		{
			if(target.components.get(componentType) != null)
			{
				return true;
			}
		}
		return false;
	}
	
	private <T extends ComponentStackHolder> boolean insertItemToComponent(String key, Player player, MachineBlockEntity target, T component, ItemStack item, Simulation simulation)
	{
		ItemStack slotItem = slotItems.get(key);
		var handler = REGISTERED_COMPONENT_TYPES.get(key).handler();
		return handler.handle(player, target, component, component, slotItem, item, simulation);
	}
	
	private <T> boolean insertToComponent(Player player, MachineBlockEntity target, T component, String key, Simulation simulation)
	{
		if(!(component instanceof ComponentStackHolder componentStackHolder))
		{
			return false;
		}
		boolean success = false;
		
		ItemStack slotItem = slotItems.get(key).copy();
		List<ItemStack> matchingItems = player.hasInfiniteMaterials() ? List.of(slotItem) : findItemsMatching(player.getInventory(), slotItem);
		
		if(matchingItems.isEmpty() && componentStackHolder instanceof RedstoneControlComponent)
		{
			ItemStack componentStack = componentStackHolder.getStack().copy();
			if(!componentStack.isEmpty())
			{
				matchingItems.add(componentStack);
			}
		}
		
		for(ItemStack matchingItem : matchingItems)
		{
			if(this.insertItemToComponent(key, player, target, componentStackHolder, matchingItem, simulation))
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
	
	private <T extends ComponentStackHolder> boolean dropFromComponent(Player player, MachineBlockEntity target, T component, Simulation simulation)
	{
		ItemStack componentItem = component.getStack();
		if(!componentItem.isEmpty())
		{
			if(!player.hasInfiniteMaterials())
			{
				ComponentTypeHandler.drop(target, componentItem.copy());
			}
			component.setStack(ItemStack.EMPTY);
			return true;
		}
		return false;
	}
	
	private <T> boolean applyComponent(Player player, MachineBlockEntity target, String key, Simulation simulation)
	{
		AtomicBoolean success = new AtomicBoolean(false);
		
		Inventory inventory = player.getInventory();
		
		var componentType = REGISTERED_COMPONENT_TYPES.get(key).componentType();
		target.components.forType(componentType, (component) ->
		{
			if(slotItems.containsKey(key))
			{
				if(this.insertToComponent(player, target, component, key, simulation))
				{
					success.set(true);
				}
			}
			else if(component instanceof ComponentStackHolder componentStackHolder)
			{
				if(this.dropFromComponent(player, target, componentStackHolder, simulation))
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
		boolean success = false;
		for(String key : REGISTERED_COMPONENT_TYPES.keySet())
		{
			if(this.applyComponent(player, target, key, simulation))
			{
				success = true;
			}
		}
		return success;
	}
}
