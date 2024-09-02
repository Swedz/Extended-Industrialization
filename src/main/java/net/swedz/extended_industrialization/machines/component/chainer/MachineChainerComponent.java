package net.swedz.extended_industrialization.machines.component.chainer;

import aztech.modern_industrialization.machines.IComponent;
import com.google.common.collect.Sets;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.swedz.extended_industrialization.EILocalizedListeners;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.component.chainer.handler.ChainerFluidHandler;
import net.swedz.extended_industrialization.machines.component.chainer.handler.ChainerItemHandler;
import net.swedz.tesseract.neoforge.localizedlistener.LocalizedListener;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class MachineChainerComponent implements IComponent, ClearableInvalidatable
{
	private final MachineChainerMachineBlockEntity machineBlockEntity;
	
	private final LocalizedListener<BlockEvent.NeighborNotifyEvent> listenerNeighborNotify;
	
	private final MachineLinks machineLinks;
	
	private final ChainerItemHandler  itemHandler;
	private final ChainerFluidHandler fluidHandler;
	
	public MachineChainerComponent(MachineChainerMachineBlockEntity machineBlockEntity, int maxConnectedMachines)
	{
		this.machineBlockEntity = machineBlockEntity;
		
		this.machineLinks = new MachineLinks(
				machineBlockEntity::getLevel,
				machineBlockEntity.getBlockPos(),
				() -> machineBlockEntity.orientation.facingDirection,
				maxConnectedMachines
		);
		
		this.itemHandler = new ChainerItemHandler(machineLinks);
		this.fluidHandler = new ChainerFluidHandler(machineLinks);
		
		this.listenerNeighborNotify = (event) ->
		{
			if(machineLinks.contains(event.getPos()) || machineLinks.isJustOutside(event.getPos()))
			{
				machineBlockEntity.buildLinksAndUpdate();
			}
		};
	}
	
	public Level getLevel()
	{
		return machineBlockEntity.getLevel();
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
	
	private Set<ChunkPos> previousSpannedChunks = Sets.newHashSet();
	
	public void registerListeners()
	{
		if(!previousSpannedChunks.isEmpty())
		{
			throw new IllegalStateException("Cannot register listeners for a chainer that already has listeners registered");
		}
		Set<ChunkPos> spannedChunks = machineLinks.getSpannedChunks();
		EILocalizedListeners.INSTANCE.register(this.getLevel(), spannedChunks, BlockEvent.NeighborNotifyEvent.class, listenerNeighborNotify);
		previousSpannedChunks = spannedChunks;
	}
	
	public void unregisterListeners()
	{
		if(!previousSpannedChunks.isEmpty())
		{
			EILocalizedListeners.INSTANCE.unregister(this.getLevel(), previousSpannedChunks, BlockEvent.NeighborNotifyEvent.class, listenerNeighborNotify);
			previousSpannedChunks = Sets.newHashSet();
		}
	}
	
	// TODO rename this
	private void forEachThing(Consumer<ClearableInvalidatable> action)
	{
		List.of(
				machineLinks,
				itemHandler,
				fluidHandler
		).forEach(action);
	}
	
	@Override
	public void clear()
	{
		this.forEachThing(ClearableInvalidatable::clear);
	}
	
	@Override
	public void invalidate()
	{
		this.forEachThing(ClearableInvalidatable::invalidate);
	}
	
	@Override
	public void writeNbt(CompoundTag tag, HolderLookup.Provider registries)
	{
	}
	
	@Override
	public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine)
	{
	}
}
