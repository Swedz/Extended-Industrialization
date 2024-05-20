package net.swedz.extended_industrialization.machines.blockentities;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.inventory.MIFluidStorage;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.MIItemStorage;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import dev.technici4n.grandpower.api.ILongEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.swedz.extended_industrialization.machines.guicomponents.modularmultiblock.ModularMultiblockGui;
import net.swedz.extended_industrialization.machines.guicomponents.modularmultiblock.ModularMultiblockGuiLine;
import net.swedz.extended_industrialization.text.EIText;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MachineChainerMachineBlockEntity extends MachineBlockEntity implements Tickable
{
	private static final int MAX_CONNECTED_MACHINES = 64;
	
	private List<StorageWrapper<MIItemStorage>>  machineItemStorages;
	private List<StorageWrapper<MIFluidStorage>> machineFluidStorages;
	private List<MIEnergyStorage>                machineEnergyStorages;
	
	private int machines;
	private int machineItemSlots;
	private int machineFluidSlots;
	
	private final ItemHandler   itemHandler   = new ItemHandler();
	private final FluidHandler  fluidHandler  = new FluidHandler();
	private final EnergyHandler energyHandler = new EnergyHandler();
	
	private long tick;
	
	public MachineChainerMachineBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder("machine_chainer", false).backgroundHeight(180).build(),
				new OrientationComponent.Params(false, false, false)
		);
		
		this.buildLinks();
		
		this.registerGuiComponent(new ModularMultiblockGui.Server(60, () ->
		{
			List<ModularMultiblockGuiLine> text = Lists.newArrayList();
			
			text.add(new ModularMultiblockGuiLine(EIText.MACHINE_CHAINER_CONNECTED_MACHINES.text(machines, MAX_CONNECTED_MACHINES)));
			
			return text;
		}));
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
	
	@Override
	public void onPlaced(LivingEntity placer, ItemStack itemStack)
	{
		super.onPlaced(placer, itemStack);
		this.buildLinks();
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
			
			this.buildLinks();
		}
	}
	
	private List<MachineBlockEntity> findMachines()
	{
		List<MachineBlockEntity> machines = Lists.newArrayList();
		if(level == null)
		{
			return machines;
		}
		for(int i = 1; i <= MAX_CONNECTED_MACHINES; i++)
		{
			BlockPos pos = worldPosition.relative(orientation.facingDirection, i);
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if(blockEntity instanceof MachineBlockEntity machineBlockEntity && machineBlockEntity.getInventory() != MIInventory.EMPTY)
			{
				machines.add(machineBlockEntity);
			}
			else
			{
				break;
			}
		}
		return machines;
	}
	
	// TODO only rebuild when the blocks have changed
	private void buildLinks()
	{
		List<MachineBlockEntity> machines = this.findMachines();
		
		List<StorageWrapper<MIItemStorage>> itemStorages = Lists.newArrayList();
		List<StorageWrapper<MIFluidStorage>> fluidStorages = Lists.newArrayList();
		List<MIEnergyStorage> energyStorages = Lists.newArrayList();
		int itemSlots = 0;
		int fluidSlots = 0;
		
		for(MachineBlockEntity machine : machines)
		{
			MIInventory inventory = machine.getInventory();
			MIItemStorage itemStorage = inventory.itemStorage;
			MIFluidStorage fluidStorage = inventory.fluidStorage;
			
			int itemSlotStart = itemSlots;
			int fluidSlotStart = fluidSlots;
			itemSlots += itemStorage.itemHandler.getSlots();
			fluidSlots += fluidStorage.fluidHandler.getTanks();
			int itemSlotEnd = itemSlots - 1;
			int fluidSlotEnd = fluidSlots - 1;
			
			itemStorages.add(new StorageWrapper<>(itemStorage, itemSlotStart, itemSlotEnd));
			fluidStorages.add(new StorageWrapper<>(fluidStorage, fluidSlotStart, fluidSlotEnd));
			
			MIEnergyStorage energyStorage = level.getCapability(EnergyApi.SIDED, machine.getBlockPos(), null);
			if(energyStorage != null)
			{
				energyStorages.add(energyStorage);
			}
		}
		
		machineItemStorages = itemStorages;
		machineFluidStorages = fluidStorages;
		machineEnergyStorages = energyStorages;
		this.machines = machines.size();
		machineItemSlots = itemSlots;
		machineFluidSlots = fluidSlots;
	}
	
	private record StorageWrapper<S>(S storage, int slotStart, int slotEnd)
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
			StorageWrapper<MIItemStorage> wrapper = getStorageFromSlot(slot, machineItemStorages);
			return wrapper == null ? ItemStack.EMPTY : wrapper.storage().itemHandler.getStackInSlot(wrapper.getHandlerSlot(slot));
		}
		
		@Override
		public ItemStack insertItem(int __, ItemStack stack, boolean simulate)
		{
			List<StorageWrapper<MIItemStorage>> storagesShuffled = new ArrayList<>(machineItemStorages);
			Collections.shuffle(storagesShuffled);
			
			ItemStack remaining = stack;
			for(StorageWrapper<MIItemStorage> wrapper : storagesShuffled)
			{
				for(int slot = 0; slot < wrapper.storage().itemHandler.getSlots(); slot++)
				{
					remaining = wrapper.storage().itemHandler.insertItem(slot, remaining, simulate);
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
			StorageWrapper<MIItemStorage> wrapper = getStorageFromSlot(slot, machineItemStorages);
			return wrapper == null ? ItemStack.EMPTY : wrapper.storage().itemHandler.extractItem(wrapper.getHandlerSlot(slot), amount, simulate);
		}
		
		@Override
		public int getSlotLimit(int slot)
		{
			StorageWrapper<MIItemStorage> wrapper = getStorageFromSlot(slot, machineItemStorages);
			return wrapper == null ? 0 : wrapper.storage().itemHandler.getSlotLimit(wrapper.getHandlerSlot(slot));
		}
		
		@Override
		public boolean isItemValid(int slot, ItemStack stack)
		{
			StorageWrapper<MIItemStorage> wrapper = getStorageFromSlot(slot, machineItemStorages);
			return wrapper != null && wrapper.storage().itemHandler.isItemValid(wrapper.getHandlerSlot(slot), stack);
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
			StorageWrapper<MIFluidStorage> wrapper = getStorageFromSlot(tank, machineFluidStorages);
			return wrapper == null ? FluidStack.EMPTY : wrapper.storage().fluidHandler.getFluidInTank(wrapper.getHandlerSlot(tank));
		}
		
		@Override
		public int getTankCapacity(int tank)
		{
			StorageWrapper<MIFluidStorage> wrapper = getStorageFromSlot(tank, machineFluidStorages);
			return wrapper == null ? 0 : wrapper.storage().fluidHandler.getTankCapacity(wrapper.getHandlerSlot(tank));
		}
		
		@Override
		public boolean isFluidValid(int tank, FluidStack stack)
		{
			StorageWrapper<MIFluidStorage> wrapper = getStorageFromSlot(tank, machineFluidStorages);
			return wrapper != null && wrapper.storage().fluidHandler.isFluidValid(wrapper.getHandlerSlot(tank), stack);
		}
		
		@Override
		public int fill(FluidStack resource, FluidAction action)
		{
			int amountFilled = 0;
			for(int i = 0; i < machineFluidStorages.size(); i++)
			{
				StorageWrapper<MIFluidStorage> wrapper = machineFluidStorages.get(i);
				int remainingStorages = machineFluidStorages.size() - i;
				int remainingAmountToInsert = resource.getAmount() - amountFilled;
				int amountToInsert = remainingAmountToInsert / remainingStorages;
				amountFilled += wrapper.storage().fluidHandler.fill(resource.copyWithAmount(amountToInsert), action);
			}
			return amountFilled;
		}
		
		private FluidStack drain(Fluid fluid, int maxAmount, FluidAction action)
		{
			int amountTransferred = 0;
			for(int i = 0; i < machineFluidStorages.size(); i++)
			{
				StorageWrapper<MIFluidStorage> wrapper = machineFluidStorages.get(i);
				int remainingStorages = machineFluidStorages.size() - i;
				int remainingAmountToTransfer = maxAmount - amountTransferred;
				int amountToTansfer = remainingAmountToTransfer / remainingStorages;
				FluidStack transferred = fluid == null ?
						wrapper.storage().fluidHandler.drain(amountToTansfer, action) :
						wrapper.storage().fluidHandler.drain(new FluidStack(fluid, amountToTansfer), action);
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
			return machineEnergyStorages.stream().mapToLong(ILongEnergyStorage::getAmount).sum();
		}
		
		@Override
		public long getCapacity()
		{
			return machineEnergyStorages.stream().mapToLong(ILongEnergyStorage::getCapacity).sum();
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
			for(int i = 0; i < machineEnergyStorages.size(); i++)
			{
				MIEnergyStorage storage = machineEnergyStorages.get(i);
				int remainingStorages = machineEnergyStorages.size() - i;
				long remainingAmountToReceive = maxReceive - amountReceived;
				long amountToReceive = remainingAmountToReceive / remainingStorages;
				amountReceived += storage.receive(amountToReceive, simulate);
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
	
	public static void registerCapabilities(BlockEntityType<?> bet)
	{
		MICapabilities.onEvent((event) ->
		{
			event.registerBlockEntity(
					Capabilities.ItemHandler.BLOCK, bet,
					(be, direction) -> ((MachineChainerMachineBlockEntity) be).itemHandler
			);
			event.registerBlockEntity(
					Capabilities.FluidHandler.BLOCK, bet,
					(be, direction) -> ((MachineChainerMachineBlockEntity) be).fluidHandler
			);
			event.registerBlockEntity(
					EnergyApi.SIDED, bet,
					(be, direction) -> ((MachineChainerMachineBlockEntity) be).energyHandler
			);
		});
	}
}
