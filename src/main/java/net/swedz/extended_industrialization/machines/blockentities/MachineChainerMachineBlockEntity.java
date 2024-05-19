package net.swedz.extended_industrialization.machines.blockentities;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyHandler;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public final class MachineChainerMachineBlockEntity extends MachineBlockEntity implements Tickable
{
	private final ItemHandler   items  = new ItemHandler();
	private final FluidHandler  fluids = new FluidHandler();
	private final EnergyHandler energy = new EnergyHandler();
	
	private long tick;
	
	public MachineChainerMachineBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder("machine_chainer", false).backgroundHeight(180).build(),
				new OrientationComponent.Params(false, false, false)
		);
		
		items.rebuildHandlers(this.findItemHandlers());
	}
	
	@Override
	public MIInventory getInventory()
	{
		return MIInventory.EMPTY;
	}
	
	@Override
	protected MachineModelClientData getMachineModelData()
	{
		MachineModelClientData data = new MachineModelClientData();
		orientation.writeModelData(data);
		return data;
	}
	
	private List<IItemHandler> findItemHandlers()
	{
		List<IItemHandler> itemHandlers = Lists.newArrayList();
		if(level == null)
		{
			return itemHandlers;
		}
		for(int i = 1; i <= 64; i++)
		{
			BlockPos pos = worldPosition.relative(orientation.facingDirection, i);
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if(blockEntity instanceof MachineBlockEntity machineBlockEntity && machineBlockEntity.getInventory() != MIInventory.EMPTY)
			{
				itemHandlers.add(machineBlockEntity.getInventory().itemStorage.itemHandler);
			}
			else
			{
				break;
			}
		}
		return itemHandlers;
	}
	
	@Override
	public void tick()
	{
		if(level.isClientSide())
		{
			return;
		}
		
		if(++tick >= 20)
		{
			tick = 0;
			
			items.rebuildHandlers(this.findItemHandlers());
		}
	}
	
	private static final class ItemHandler implements IItemHandler
	{
		private final List<IItemHandler> handlers    = Lists.newArrayList();
		private final List<Integer>      baseIndexes = Lists.newArrayList();
		
		private int slotCount;
		
		public void rebuildHandlers(List<IItemHandler> handlers)
		{
			this.handlers.clear();
			this.handlers.addAll(handlers);
			baseIndexes.clear();
			int index = 0;
			for(IItemHandler handler : this.handlers)
			{
				index += handler.getSlots();
				baseIndexes.add(index);
			}
			slotCount = index;
		}
		
		private int getIndexForSlot(int slot)
		{
			if(slot < 0)
			{
				return -1;
			}
			
			for(int i = 0; i < baseIndexes.size(); i++)
			{
				if(slot - baseIndexes.get(i) < 0)
				{
					return i;
				}
			}
			
			return -1;
		}
		
		private IItemHandler getHandlerFromIndex(int index)
		{
			if(index < 0 || index >= handlers.size())
			{
				return EmptyHandler.INSTANCE;
			}
			return handlers.get(index);
		}
		
		private int getSlotFromIndex(int slot, int index)
		{
			if(index <= 0 || index >= baseIndexes.size())
			{
				return slot;
			}
			return slot - baseIndexes.get(index - 1);
		}
		
		@Override
		public int getSlots()
		{
			return slotCount;
		}
		
		@Override
		public ItemStack getStackInSlot(int slot)
		{
			int index = this.getIndexForSlot(slot);
			IItemHandler handler = this.getHandlerFromIndex(index);
			slot = this.getSlotFromIndex(slot, index);
			return handler.getStackInSlot(slot);
		}
		
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
		{
			int index = this.getIndexForSlot(slot);
			IItemHandler handler = this.getHandlerFromIndex(index);
			slot = this.getSlotFromIndex(slot, index);
			return handler.insertItem(slot, stack, simulate);
		}
		
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate)
		{
			int index = this.getIndexForSlot(slot);
			IItemHandler handler = this.getHandlerFromIndex(index);
			slot = this.getSlotFromIndex(slot, index);
			return handler.extractItem(slot, amount, simulate);
		}
		
		@Override
		public int getSlotLimit(int slot)
		{
			int index = this.getIndexForSlot(slot);
			IItemHandler handler = this.getHandlerFromIndex(index);
			int localSlot = this.getSlotFromIndex(slot, index);
			return handler.getSlotLimit(localSlot);
		}
		
		@Override
		public boolean isItemValid(int slot, ItemStack stack)
		{
			int index = this.getIndexForSlot(slot);
			IItemHandler handler = this.getHandlerFromIndex(index);
			int localSlot = this.getSlotFromIndex(slot, index);
			return handler.isItemValid(localSlot, stack);
		}
	}
	
	private static final class FluidHandler implements IFluidHandler
	{
		@Override
		public int getTanks()
		{
			return 0;
		}
		
		@Override
		public FluidStack getFluidInTank(int tank)
		{
			return null;
		}
		
		@Override
		public int getTankCapacity(int tank)
		{
			return 0;
		}
		
		@Override
		public boolean isFluidValid(int tank, FluidStack stack)
		{
			return false;
		}
		
		@Override
		public int fill(FluidStack resource, FluidAction action)
		{
			return 0;
		}
		
		@Override
		public FluidStack drain(FluidStack resource, FluidAction action)
		{
			return null;
		}
		
		@Override
		public FluidStack drain(int maxDrain, FluidAction action)
		{
			return null;
		}
	}
	
	private static final class EnergyHandler implements MIEnergyStorage
	{
		@Override
		public boolean canConnect(CableTier cableTier)
		{
			return false;
		}
		
		@Override
		public long receive(long maxReceive, boolean simulate)
		{
			return 0;
		}
		
		@Override
		public long extract(long maxExtract, boolean simulate)
		{
			return 0;
		}
		
		@Override
		public long getAmount()
		{
			return 0;
		}
		
		@Override
		public long getCapacity()
		{
			return 0;
		}
		
		@Override
		public boolean canExtract()
		{
			return false;
		}
		
		@Override
		public boolean canReceive()
		{
			return false;
		}
	}
	
	public static void registerCapabilities(BlockEntityType<?> bet)
	{
		MICapabilities.onEvent((event) ->
		{
			event.registerBlockEntity(
					Capabilities.ItemHandler.BLOCK, bet,
					(be, direction) -> ((MachineChainerMachineBlockEntity) be).items
			);
			event.registerBlockEntity(
					Capabilities.FluidHandler.BLOCK, bet,
					(be, direction) -> ((MachineChainerMachineBlockEntity) be).fluids
			);
			event.registerBlockEntity(
					EnergyApi.SIDED, bet,
					(be, direction) -> ((MachineChainerMachineBlockEntity) be).energy
			);
		});
	}
}
