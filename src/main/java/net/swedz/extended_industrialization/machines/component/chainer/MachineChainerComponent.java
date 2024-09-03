package net.swedz.extended_industrialization.machines.component.chainer;

import aztech.modern_industrialization.machines.IComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.swedz.extended_industrialization.EILocalizedListeners;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.component.chainer.handler.ChainerEnergyHandler;
import net.swedz.extended_industrialization.machines.component.chainer.handler.ChainerFluidHandler;
import net.swedz.extended_industrialization.machines.component.chainer.handler.ChainerItemHandler;
import net.swedz.tesseract.neoforge.localizedlistener.LocalizedListener;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class MachineChainerComponent implements IComponent, ChainerElement
{
	private final MachineChainerMachineBlockEntity machine;
	
	private final LocalizedListener<BlockEvent.NeighborNotifyEvent> listenerNeighborNotify;
	
	private final MachineLinks machineLinks;
	
	private final ChainerItemHandler   itemHandler;
	private final ChainerFluidHandler  fluidHandler;
	private final ChainerEnergyHandler energyHandler;
	
	public MachineChainerComponent(MachineChainerMachineBlockEntity machine, int maxConnectedMachines)
	{
		this.machine = machine;
		
		this.machineLinks = new MachineLinks(machine, maxConnectedMachines);
		
		this.itemHandler = new ChainerItemHandler(machineLinks);
		this.fluidHandler = new ChainerFluidHandler(machineLinks);
		this.energyHandler = new ChainerEnergyHandler(machineLinks);
		
		this.listenerNeighborNotify = (event) ->
		{
			if(machineLinks.origin().equals(event.getPos()))
			{
				machine.buildLinks();
			}
			else if(machineLinks.contains(event.getPos()) ||
					machineLinks.isAfter(event.getPos()))
			{
				machine.buildLinks();
			}
		};
	}
	
	public Level level()
	{
		return machine.getLevel();
	}
	
	public MachineLinks links()
	{
		return machineLinks;
	}
	
	public int getMaxConnectedMachinesCount()
	{
		return machineLinks.maxConnections();
	}
	
	public int getConnectedMachineCount()
	{
		return machineLinks.count();
	}
	
	public ChainerItemHandler itemHandler()
	{
		return itemHandler;
	}
	
	public ChainerFluidHandler fluidHandler()
	{
		return fluidHandler;
	}
	
	public ChainerEnergyHandler energyHandler()
	{
		return energyHandler;
	}
	
	private Set<ChunkPos> previousSpannedChunks = Set.of();
	
	public void registerListeners()
	{
		if(!previousSpannedChunks.isEmpty())
		{
			throw new IllegalStateException("Cannot register listeners for a chainer that already has listeners registered");
		}
		Set<ChunkPos> spannedChunks = machineLinks.getSpannedChunks(true);
		EILocalizedListeners.INSTANCE.register(this.level(), spannedChunks, BlockEvent.NeighborNotifyEvent.class, listenerNeighborNotify);
		previousSpannedChunks = spannedChunks;
	}
	
	public void unregisterListeners()
	{
		if(!previousSpannedChunks.isEmpty())
		{
			EILocalizedListeners.INSTANCE.unregister(this.level(), previousSpannedChunks, BlockEvent.NeighborNotifyEvent.class, listenerNeighborNotify);
			previousSpannedChunks = Set.of();
		}
	}
	
	private void forEachElement(Consumer<ChainerElement> action)
	{
		List.of(
				machineLinks,
				itemHandler,
				fluidHandler,
				energyHandler
		).forEach(action);
	}
	
	@Override
	public void clear()
	{
		this.forEachElement(ChainerElement::clear);
	}
	
	@Override
	public void invalidate()
	{
		this.forEachElement(ChainerElement::invalidate);
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
		tag.putInt("connected_machines", this.getConnectedMachineCount());
		machineLinks.failPosition().ifPresent((pos) -> tag.putLong("fail_position", pos.asLong()));
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
		machineLinks.count(tag.getInt("connected_machines"));
		if(tag.contains("fail_position", CompoundTag.TAG_LONG))
		{
			machineLinks.failPosition(BlockPos.of(tag.getLong("fail_position")));
		}
		else
		{
			machineLinks.failPosition(null);
		}
	}
}
