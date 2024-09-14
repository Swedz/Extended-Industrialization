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
import java.util.function.Supplier;

public final class ChainerComponent implements IComponent, ChainerElement
{
	private final MachineChainerMachineBlockEntity machine;
	
	private final LocalizedListener<BlockEvent.NeighborNotifyEvent> listenerNeighborNotify;
	
	private final ChainerLinks links;
	
	private final ChainerItemHandler   itemHandler;
	private final ChainerFluidHandler  fluidHandler;
	private final ChainerEnergyHandler insertableEnergyHandler;
	private final ChainerEnergyHandler extractableEnergyHandler;
	
	public ChainerComponent(MachineChainerMachineBlockEntity machine, int maxConnectedMachines, Supplier<Boolean> allowOperation)
	{
		this.machine = machine;
		
		this.links = new ChainerLinks(machine, maxConnectedMachines, allowOperation);
		
		this.itemHandler = new ChainerItemHandler(links);
		this.fluidHandler = new ChainerFluidHandler(links);
		this.insertableEnergyHandler = new ChainerEnergyHandler(links, true);
		this.extractableEnergyHandler = new ChainerEnergyHandler(links, false);
		
		this.listenerNeighborNotify = (event) ->
		{
			if(links.origin().equals(event.getPos()))
			{
				machine.buildLinks();
			}
			else if(links.contains(event.getPos()) ||
					links.isAfter(event.getPos()))
			{
				machine.buildLinks();
			}
		};
	}
	
	public Level level()
	{
		return machine.getLevel();
	}
	
	public ChainerLinks links()
	{
		return links;
	}
	
	public ChainerItemHandler itemHandler()
	{
		return itemHandler;
	}
	
	public ChainerFluidHandler fluidHandler()
	{
		return fluidHandler;
	}
	
	public ChainerEnergyHandler insertableEnergyHandler()
	{
		return insertableEnergyHandler;
	}
	
	public ChainerEnergyHandler extractableEnergyHandler()
	{
		return extractableEnergyHandler;
	}
	
	private Set<ChunkPos> previousSpannedChunks = Set.of();
	
	public void registerListeners()
	{
		if(!previousSpannedChunks.isEmpty())
		{
			throw new IllegalStateException("Cannot register listeners for a chainer that already has listeners registered");
		}
		Set<ChunkPos> spannedChunks = links.getSpannedChunks(true);
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
				links,
				itemHandler,
				fluidHandler,
				insertableEnergyHandler,
				extractableEnergyHandler
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
		tag.putInt("connected_machines", links.count());
		links.failPosition().ifPresent((pos) -> tag.putLong("fail_position", pos.asLong()));
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
		links.count(tag.getInt("connected_machines"));
		if(tag.contains("fail_position", CompoundTag.TAG_LONG))
		{
			links.failPosition(BlockPos.of(tag.getLong("fail_position")));
		}
		else
		{
			links.failPosition(null);
		}
	}
}
