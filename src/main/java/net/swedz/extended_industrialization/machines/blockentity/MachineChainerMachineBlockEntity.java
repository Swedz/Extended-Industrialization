package net.swedz.extended_industrialization.machines.blockentity;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import com.google.common.collect.Lists;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.machines.component.chainer.MachineChainerComponent;
import net.swedz.extended_industrialization.machines.component.chainer.MachineLinks;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGui;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGuiLine;

import java.util.List;

public final class MachineChainerMachineBlockEntity extends MachineBlockEntity implements Tickable
{
	private final MachineChainerComponent chainer;
	
	private int tick;
	private int lastRebuildTick = -1;
	
	private boolean needsRebuild;
	
	public MachineChainerMachineBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(EI.id("machine_chainer"), false).backgroundHeight(180).build(),
				new OrientationComponent.Params(false, false, false)
		);
		
		chainer = new MachineChainerComponent(this, 64);
		
		this.registerComponents(chainer);
		
		this.registerGuiComponent(new ModularMultiblockGui.Server(60, () ->
		{
			MachineLinks links = chainer.links();
			
			List<ModularMultiblockGuiLine> text = Lists.newArrayList();
			
			if(!links.hasConnections() && links.failPosition().isPresent())
			{
				text.add(new ModularMultiblockGuiLine(EIText.MACHINE_CHAINER_PROBLEM_AT.text(links.failPosition().get().toShortString()), 0xFF0000));
			}
			else
			{
				text.add(new ModularMultiblockGuiLine(EIText.MACHINE_CHAINER_CONNECTED_MACHINES.text(links.count(), links.maxConnections())));
			}
			
			return text;
		}));
	}
	
	public MachineChainerComponent getChainerComponent()
	{
		return chainer;
	}
	
	public void buildLinks()
	{
		if(tick == lastRebuildTick)
		{
			EI.LOGGER.warn("Prevented Machine Chainer in dimension '{}' at ({}) from rebuilding links more than once in the same tick!", level.dimension().location(), worldPosition.toShortString());
			return;
		}
		
		needsRebuild = false;
		
		if(!level.isClientSide())
		{
			chainer.unregisterListeners();
			chainer.invalidate();
			chainer.registerListeners();
		}
		
		this.invalidateCapabilities();
		
		//level.blockUpdated(worldPosition, Blocks.AIR);
		this.setChanged();
		if(!level.isClientSide())
		{
			this.sync();
		}
		
		lastRebuildTick = tick;
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
	public void setLevel(Level level)
	{
		super.setLevel(level);
		
		if(!level.isClientSide())
		{
			needsRebuild = true;
		}
	}
	
	@Override
	public void setRemoved()
	{
		super.setRemoved();
		
		if(!level.isClientSide())
		{
			chainer.unregisterListeners();
			chainer.clear();
		}
	}
	
	@Override
	public void tick()
	{
		if(level.isClientSide())
		{
			return;
		}
		
		tick++;
		
		if(tick % 10 * 20 == 0)
		{
			needsRebuild = true;
		}
		
		if(needsRebuild)
		{
			this.buildLinks();
		}
	}
	
	public static void registerCapabilities(BlockEntityType<?> bet)
	{
		MICapabilities.onEvent((event) ->
		{
			event.registerBlockEntity(
					Capabilities.ItemHandler.BLOCK, bet,
					(be, direction) -> ((MachineChainerMachineBlockEntity) be).getChainerComponent().itemHandler()
			);
			event.registerBlockEntity(
					Capabilities.FluidHandler.BLOCK, bet,
					(be, direction) -> ((MachineChainerMachineBlockEntity) be).getChainerComponent().fluidHandler()
			);
			event.registerBlockEntity(
					EnergyApi.SIDED, bet,
					(be, direction) -> ((MachineChainerMachineBlockEntity) be).getChainerComponent().energyHandler()
			);
		});
	}
}
