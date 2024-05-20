package net.swedz.extended_industrialization.machines.components;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.IComponent;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.swedz.extended_industrialization.api.isolatedlistener.IsolatedListener;
import net.swedz.extended_industrialization.api.isolatedlistener.IsolatedListeners;
import net.swedz.extended_industrialization.machines.blockentities.MachineChainerMachineBlockEntity;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MachineChainerComponent implements IComponent.ServerOnly
{
	private final MachineChainerMachineBlockEntity parentBlockEntity;
	private final int                              maxConnectedMachines;
	
	private final IsolatedListener<BlockEvent.NeighborNotifyEvent> listenerNeighborNotify;
	
	private List<BlockPos>                        machineLinks          = Lists.newArrayList();
	private List<StorageWrapper<IItemHandler>>    machineItemWrappers   = Lists.newArrayList();
	private List<StorageWrapper<IFluidHandler>>   machineFluidWrappers  = Lists.newArrayList();
	private List<StorageWrapper<MIEnergyStorage>> machineEnergyWrappers = Lists.newArrayList();
	
	private int machineItemSlots;
	private int machineFluidSlots;
	
	public final ItemHandler   itemHandler   = new ItemHandler();
	public final FluidHandler  fluidHandler  = new FluidHandler();
	public final EnergyHandler energyHandler = new EnergyHandler();
	
	private int tick;
	
	public MachineChainerComponent(MachineChainerMachineBlockEntity parentBlockEntity, int maxConnectedMachines)
	{
		this.parentBlockEntity = parentBlockEntity;
		this.maxConnectedMachines = maxConnectedMachines;
		
		this.listenerNeighborNotify = (event) ->
		{
			if(machineLinks.contains(event.getPos()) || event.getPos().equals(parentBlockEntity.getBlockPos().relative(parentBlockEntity.orientation.facingDirection, machineLinks.size() + 1)))
			{
				this.buildLinks();
			}
		};
	}
	
	public Level getLevel()
	{
		return parentBlockEntity.getLevel();
	}
	
	public int getMaxConnectedMachinesCount()
	{
		return maxConnectedMachines;
	}
	
	public int getConnectedMachineCount()
	{
		return machineLinks.size();
	}
	
	private List<BlockPos> getSpannedBlocks()
	{
		List<BlockPos> blocks = Lists.newArrayList();
		for(int i = 1; i <= maxConnectedMachines; i++)
		{
			blocks.add(parentBlockEntity.getBlockPos().relative(parentBlockEntity.orientation.facingDirection, i));
		}
		return Collections.unmodifiableList(blocks);
	}
	
	private List<ChunkPos> getSpannedChunks()
	{
		List<ChunkPos> chunks = Lists.newArrayList();
		for(BlockPos block : this.getSpannedBlocks())
		{
			chunks.add(new ChunkPos(block));
		}
		return Collections.unmodifiableList(chunks);
	}
	
	public void registerListeners()
	{
		IsolatedListeners.register(this.getLevel(), this.getSpannedChunks(), BlockEvent.NeighborNotifyEvent.class, listenerNeighborNotify);
	}
	
	public void unregisterListeners()
	{
		IsolatedListeners.unregister(this.getLevel(), this.getSpannedChunks(), BlockEvent.NeighborNotifyEvent.class, listenerNeighborNotify);
	}
	
	public void clearLinks()
	{
		machineLinks = List.of();
		machineItemWrappers = List.of();
		machineFluidWrappers = List.of();
		machineEnergyWrappers = List.of();
		machineItemSlots = 0;
		machineFluidSlots = 0;
	}
	
	public void buildLinks()
	{
		List<BlockPos> machinesFound = Lists.newArrayList();
		List<StorageWrapper<IItemHandler>> itemWrappers = Lists.newArrayList();
		List<StorageWrapper<IFluidHandler>> fluidWrappers = Lists.newArrayList();
		List<StorageWrapper<MIEnergyStorage>> energyWrappers = Lists.newArrayList();
		int itemSlots = 0;
		int fluidSlots = 0;
		
		for(BlockPos blockPos : this.getSpannedBlocks())
		{
			BlockEntity blockEntity = this.getLevel().getBlockEntity(blockPos);
			if(!(blockEntity instanceof MachineBlockEntity))
			{
				break;
			}
			if(blockEntity instanceof MachineChainerMachineBlockEntity chainerBlockEntity &&
			   (chainerBlockEntity.orientation.facingDirection == parentBlockEntity.orientation.facingDirection || chainerBlockEntity.orientation.facingDirection.getOpposite() == parentBlockEntity.orientation.facingDirection))
			{
				break;
			}
			
			boolean isMachine = false;
			
			IItemHandler itemHandler = this.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, blockPos, null);
			if(itemHandler != null)
			{
				int itemSlotStart = itemSlots;
				itemSlots += itemHandler.getSlots();
				int itemSlotEnd = itemSlots - 1;
				itemWrappers.add(new StorageWrapper<>(blockPos, itemHandler, itemSlotStart, itemSlotEnd));
				isMachine = true;
			}
			
			IFluidHandler fluidHandler = this.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, blockPos, null);
			if(fluidHandler != null)
			{
				int fluidSlotStart = fluidSlots;
				fluidSlots += fluidHandler.getTanks();
				int fluidSlotEnd = fluidSlots - 1;
				fluidWrappers.add(new StorageWrapper<>(blockPos, fluidHandler, fluidSlotStart, fluidSlotEnd));
				isMachine = true;
			}
			
			MIEnergyStorage energyStorage = this.getLevel().getCapability(EnergyApi.SIDED, blockPos, null);
			if(energyStorage != null)
			{
				energyWrappers.add(new StorageWrapper<>(blockPos, energyStorage, -1, -1));
				isMachine = true;
			}
			
			if(!isMachine)
			{
				break;
			}
			machinesFound.add(blockPos);
		}
		
		machineLinks = Collections.unmodifiableList(machinesFound);
		machineItemWrappers = Collections.unmodifiableList(itemWrappers);
		machineFluidWrappers = Collections.unmodifiableList(fluidWrappers);
		machineEnergyWrappers = Collections.unmodifiableList(energyWrappers);
		machineItemSlots = itemSlots;
		machineFluidSlots = fluidSlots;
	}
	
	private record StorageWrapper<H>(BlockPos blockPos, H handler, int slotStart, int slotEnd)
	{
		public boolean contains(int slot)
		{
			return slot >= slotStart && slot <= slotEnd;
		}
		
		public int getHandlerSlot(int slot)
		{
			return slot - slotStart;
		}
	}
	
	private <T> StorageWrapper<T> getStorageFromSlot(int slot, List<StorageWrapper<T>> storages)
	{
		if(slot < 0)
		{
			return null;
		}
		
		for(StorageWrapper<T> storage : storages)
		{
			if(storage.contains(slot))
			{
				return storage;
			}
		}
		
		return null;
	}
	
	private final class ItemHandler implements IItemHandler
	{
		@Override
		public int getSlots()
		{
			return machineItemSlots;
		}
		
		@Override
		public ItemStack getStackInSlot(int slot)
		{
			StorageWrapper<IItemHandler> wrapper = getStorageFromSlot(slot, machineItemWrappers);
			return wrapper == null ? ItemStack.EMPTY : wrapper.handler().getStackInSlot(wrapper.getHandlerSlot(slot));
		}
		
		@Override
		public ItemStack insertItem(int __, ItemStack stack, boolean simulate)
		{
			List<StorageWrapper<IItemHandler>> storagesShuffled = new ArrayList<>(machineItemWrappers);
			Collections.shuffle(storagesShuffled);
			
			ItemStack remaining = stack;
			for(StorageWrapper<IItemHandler> wrapper : storagesShuffled)
			{
				for(int slot = 0; slot < wrapper.handler().getSlots(); slot++)
				{
					remaining = wrapper.handler().insertItem(slot, remaining, simulate);
					if(remaining.isEmpty())
					{
						return remaining;
					}
				}
			}
			
			return stack;
		}
		
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate)
		{
			StorageWrapper<IItemHandler> wrapper = getStorageFromSlot(slot, machineItemWrappers);
			return wrapper == null ? ItemStack.EMPTY : wrapper.handler().extractItem(wrapper.getHandlerSlot(slot), amount, simulate);
		}
		
		@Override
		public int getSlotLimit(int slot)
		{
			StorageWrapper<IItemHandler> wrapper = getStorageFromSlot(slot, machineItemWrappers);
			return wrapper == null ? 0 : wrapper.handler().getSlotLimit(wrapper.getHandlerSlot(slot));
		}
		
		@Override
		public boolean isItemValid(int slot, ItemStack stack)
		{
			StorageWrapper<IItemHandler> wrapper = getStorageFromSlot(slot, machineItemWrappers);
			return wrapper != null && wrapper.handler().isItemValid(wrapper.getHandlerSlot(slot), stack);
		}
	}
	
	private final class FluidHandler implements IFluidHandler
	{
		@Override
		public int getTanks()
		{
			return machineFluidSlots;
		}
		
		@Override
		public FluidStack getFluidInTank(int tank)
		{
			StorageWrapper<IFluidHandler> wrapper = getStorageFromSlot(tank, machineFluidWrappers);
			return wrapper == null ? FluidStack.EMPTY : wrapper.handler().getFluidInTank(wrapper.getHandlerSlot(tank));
		}
		
		@Override
		public int getTankCapacity(int tank)
		{
			StorageWrapper<IFluidHandler> wrapper = getStorageFromSlot(tank, machineFluidWrappers);
			return wrapper == null ? 0 : wrapper.handler().getTankCapacity(wrapper.getHandlerSlot(tank));
		}
		
		@Override
		public boolean isFluidValid(int tank, FluidStack stack)
		{
			StorageWrapper<IFluidHandler> wrapper = getStorageFromSlot(tank, machineFluidWrappers);
			return wrapper != null && wrapper.handler().isFluidValid(wrapper.getHandlerSlot(tank), stack);
		}
		
		@Override
		public int fill(FluidStack resource, FluidAction action)
		{
			int amountFilled = 0;
			for(int i = 0; i < machineFluidWrappers.size(); i++)
			{
				StorageWrapper<IFluidHandler> wrapper = machineFluidWrappers.get(i);
				int remainingStorages = machineFluidWrappers.size() - i;
				int remainingAmountToInsert = resource.getAmount() - amountFilled;
				int amountToInsert = remainingAmountToInsert / remainingStorages;
				amountFilled += wrapper.handler().fill(resource.copyWithAmount(amountToInsert), action);
			}
			return amountFilled;
		}
		
		private FluidStack drain(Fluid fluid, int maxAmount, FluidAction action)
		{
			int amountTransferred = 0;
			for(int i = 0; i < machineFluidWrappers.size(); i++)
			{
				StorageWrapper<IFluidHandler> wrapper = machineFluidWrappers.get(i);
				int remainingStorages = machineFluidWrappers.size() - i;
				int remainingAmountToTransfer = maxAmount - amountTransferred;
				int amountToTansfer = remainingAmountToTransfer / remainingStorages;
				FluidStack transferred = fluid == null ?
						wrapper.handler().drain(amountToTansfer, action) :
						wrapper.handler().drain(new FluidStack(fluid, amountToTansfer), action);
				if(!transferred.isEmpty())
				{
					fluid = transferred.getFluid();
					amountTransferred += transferred.getAmount();
				}
			}
			return fluid == null ? FluidStack.EMPTY : new FluidStack(fluid, amountTransferred);
		}
		
		@Override
		public FluidStack drain(FluidStack resource, FluidAction action)
		{
			return this.drain(resource.getFluid(), resource.getAmount(), action);
		}
		
		@Override
		public FluidStack drain(int maxDrain, FluidAction action)
		{
			return this.drain(null, maxDrain, action);
		}
	}
	
	private final class EnergyHandler implements MIEnergyStorage
	{
		@Override
		public boolean canConnect(CableTier cableTier)
		{
			return true;
		}
		
		@Override
		public long getAmount()
		{
			return machineEnergyWrappers.stream().mapToLong((wrapper) -> wrapper.handler().getAmount()).sum();
		}
		
		@Override
		public long getCapacity()
		{
			return machineEnergyWrappers.stream().mapToLong((wrapper) -> wrapper.handler().getCapacity()).sum();
		}
		
		@Override
		public boolean canReceive()
		{
			return true;
		}
		
		@Override
		public long receive(long maxReceive, boolean simulate)
		{
			long amountReceived = 0;
			for(int i = 0; i < machineEnergyWrappers.size(); i++)
			{
				StorageWrapper<MIEnergyStorage> wrapper = machineEnergyWrappers.get(i);
				int remainingStorages = machineEnergyWrappers.size() - i;
				long remainingAmountToReceive = maxReceive - amountReceived;
				long amountToReceive = remainingAmountToReceive / remainingStorages;
				amountReceived += wrapper.handler().receive(amountToReceive, simulate);
			}
			return amountReceived;
		}
		
		@Override
		public boolean canExtract()
		{
			return false;
		}
		
		@Override
		public long extract(long maxExtract, boolean simulate)
		{
			return 0;
		}
	}
	
	@Override
	public void writeNbt(CompoundTag tag)
	{
	}
	
	@Override
	public void readNbt(CompoundTag tag, boolean isUpgradingMachine)
	{
	}
}
