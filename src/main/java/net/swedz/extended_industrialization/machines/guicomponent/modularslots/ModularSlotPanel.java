package net.swedz.extended_industrialization.machines.guicomponent.modularslots;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.inventory.HackySlot;
import aztech.modern_industrialization.inventory.SlotGroup;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.components.OverdriveComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.components.UpgradeComponent;
import aztech.modern_industrialization.machines.gui.GuiComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.component.itemslot.SimpleItemStackComponent;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.*;

public final class ModularSlotPanel
{
	public static final ResourceLocation ID = EI.id("modular_slot_panel");
	
	private static final Map<ResourceLocation, Slot> SLOTS = Maps.newHashMap();
	
	public static final ResourceLocation REDSTONE_MODULE  = registerMISlot("redstone_module", SlotPanel.SlotType.REDSTONE_MODULE);
	public static final ResourceLocation UPGRADES         = registerMISlot("upgrades", SlotPanel.SlotType.UPGRADES);
	public static final ResourceLocation CASINGS          = registerMISlot("casings", SlotPanel.SlotType.CASINGS);
	public static final ResourceLocation OVERDRIVE_MODULE = registerMISlot("overdrive_module", SlotPanel.SlotType.OVERDRIVE_MODULE);
	
	public static ResourceLocation registerSlot(ResourceLocation id, SlotGroup group,
												int stackLimit, Predicate<ItemStack> insertionChecker,
												int u, int v,
												Supplier<Component> tooltip)
	{
		if(SLOTS.containsKey(id))
		{
			throw new IllegalArgumentException("There is already a slot type registered for the id '" + id.toString() + "'");
		}
		SLOTS.put(id, new Slot(id, group, stackLimit, insertionChecker, u, v, tooltip));
		return id;
	}
	
	private static ResourceLocation registerMISlot(String name, SlotPanel.SlotType slotType)
	{
		return registerSlot(MI.id(name), slotType.group, slotType.slotLimit, slotType.insertionChecker, slotType.u, slotType.v, () -> line(slotType.tooltip));
	}
	
	static Slot getSlot(ResourceLocation id)
	{
		Slot slot = SLOTS.get(id);
		if(slot == null)
		{
			throw new IllegalArgumentException("Could not find slot with id '" + id.toString() + "'");
		}
		return slot;
	}
	
	static int getSlotX(MachineGuiParameters guiParameters)
	{
		return guiParameters.backgroundWidth + 6;
	}
	
	static int getSlotY(int index)
	{
		return 19 + (index * 20);
	}
	
	public static final class Server implements GuiComponent.Server<Data>
	{
		private final MachineBlockEntity machine;
		
		private final int offsetY;
		
		private final List<ResourceLocation>         slotIds        = Lists.newArrayList();
		private final List<Slot>                     slots          = Lists.newArrayList();
		private final List<Supplier<Integer>>        stackLimits    = Lists.newArrayList();
		private final List<SimpleItemStackComponent> slotComponents = Lists.newArrayList();
		
		public Server(MachineBlockEntity machine, int offsetY)
		{
			this.machine = machine;
			this.offsetY = offsetY;
		}
		
		private Server with(Slot slot, Supplier<Integer> stackLimit, SimpleItemStackComponent component)
		{
			slotIds.add(slot.id());
			slots.add(slot);
			stackLimits.add(stackLimit);
			slotComponents.add(component);
			return this;
		}
		
		public Server with(ResourceLocation slotId, Supplier<Integer> stackLimit, SimpleItemStackComponent component)
		{
			return this.with(getSlot(slotId), stackLimit, component);
		}
		
		private Server with(Slot slot, SimpleItemStackComponent component)
		{
			return this.with(slot, slot::stackLimit, component);
		}
		
		public Server with(ResourceLocation slotId, SimpleItemStackComponent component)
		{
			return this.with(getSlot(slotId), component);
		}
		
		public Server withRedstoneModule(RedstoneControlComponent component)
		{
			return this.with(REDSTONE_MODULE, SimpleItemStackComponent.wrap(component));
		}
		
		public Server withUpgrades(UpgradeComponent component)
		{
			return this.with(UPGRADES, SimpleItemStackComponent.wrap(component));
		}
		
		public Server withCasings(CasingComponent component)
		{
			return this.with(CASINGS, SimpleItemStackComponent.wrap(component));
		}
		
		public Server withOverdrive(OverdriveComponent component)
		{
			return this.with(OVERDRIVE_MODULE, SimpleItemStackComponent.wrap(component));
		}
		
		@Override
		public Data copyData()
		{
			return new Data(stackLimits);
		}
		
		@Override
		public boolean needsSync(Data cachedData)
		{
			return !cachedData.equals(this.copyData());
		}
		
		@Override
		public void writeInitialData(RegistryFriendlyByteBuf buf)
		{
			buf.writeVarInt(offsetY);
			
			buf.writeVarInt(slots.size());
			for(ResourceLocation slotId : slotIds)
			{
				buf.writeResourceLocation(slotId);
			}
			
			this.writeCurrentData(buf);
		}
		
		@Override
		public void writeCurrentData(RegistryFriendlyByteBuf buf)
		{
			for(Supplier<Integer> limit : stackLimits)
			{
				buf.writeVarInt(limit.get());
			}
		}
		
		@Override
		public void setupMenu(GuiComponent.MenuFacade menu)
		{
			for(int i = 0; i < slots.size(); i++)
			{
				Slot slot = slots.get(i);
				Supplier<Integer> stackLimit = stackLimits.get(i);
				SimpleItemStackComponent component = slotComponents.get(i);
				
				menu.addSlotToMenu(new HackySlot(getSlotX(machine.guiParams), getSlotY(i))
				{
					@Override
					protected ItemStack getRealStack()
					{
						return component.getStack().copy();
					}
					
					@Override
					protected void setRealStack(ItemStack stack)
					{
						component.setStackServer(machine, stack);
					}
					
					@Override
					public boolean mayPlace(ItemStack stack)
					{
						return slot.insertionChecker().test(stack);
					}
					
					@Override
					public int getMaxStackSize()
					{
						return stackLimit.get();
					}
				}, slot.group());
			}
		}
		
		@Override
		public ResourceLocation getId()
		{
			return ID;
		}
	}
	
	public record Data(List<Supplier<Integer>> stackLimits)
	{
		@Override
		public boolean equals(Object o)
		{
			if(this == o)
			{
				return true;
			}
			if(o == null || this.getClass() != o.getClass())
			{
				return false;
			}
			
			Data other = (Data) o;
			
			if(stackLimits.size() != other.stackLimits().size())
			{
				return false;
			}
			for(int i = 0; i < stackLimits.size(); i++)
			{
				if(!stackLimits.get(i).equals(other.stackLimits.get(i)))
				{
					return false;
				}
			}
			
			return true;
		}
	}
	
	public record Slot(
			ResourceLocation id, SlotGroup group,
			int stackLimit, Predicate<ItemStack> insertionChecker,
			int u, int v,
			Supplier<Component> tooltip
	)
	{
	}
}
