package net.swedz.extended_industrialization.machines.blockentity;

import aztech.modern_industrialization.MICapabilities;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.components.RedstoneControlComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.guicomponents.AutoExtract;
import aztech.modern_industrialization.machines.guicomponents.SlotPanel;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import aztech.modern_industrialization.util.Tickable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIConfig;
import net.swedz.extended_industrialization.EIText;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerComponent;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerLinks;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGui;
import net.swedz.tesseract.neoforge.compat.mi.helper.transfer.MIEnergyTransferCache;
import net.swedz.tesseract.neoforge.helper.transfer.FluidTransferCache;
import net.swedz.tesseract.neoforge.helper.transfer.ItemTransferCache;

import static net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGuiLine.*;
import static net.swedz.tesseract.neoforge.tooltip.Parser.*;

public final class MachineChainerMachineBlockEntity extends MachineBlockEntity implements Tickable
{
	private final RedstoneControlComponent redstoneControl;
	
	private final ChainerComponent chainer;
	
	private final ItemTransferCache     transferItem;
	private final FluidTransferCache    transferFluid;
	private final MIEnergyTransferCache transferEnergy;
	
	private int tick;
	private int lastRebuildTick = -1;
	
	private boolean needsRebuild;
	
	public MachineChainerMachineBlockEntity(BEP bep)
	{
		super(
				bep,
				new MachineGuiParameters.Builder(EI.id("machine_chainer"), false).backgroundHeight(175).build(),
				new OrientationComponent.Params(true, true, true)
		);
		
		redstoneControl = new RedstoneControlComponent();
		
		chainer = new ChainerComponent(
				this,
				EIConfig.machineChainerMaxConnections,
				() -> redstoneControl.doAllowNormalOperation(this)
		);
		
		transferItem = new ItemTransferCache(chainer::itemHandler);
		transferFluid = new FluidTransferCache(chainer::fluidHandler);
		transferEnergy = new MIEnergyTransferCache(chainer::extractableEnergyHandler);
		
		this.registerGuiComponent(new SlotPanel.Server(this)
				.withRedstoneControl(redstoneControl));
		
		this.registerGuiComponent(new AutoExtract.Server(orientation));
		
		this.registerGuiComponent(new ModularMultiblockGui.Server(11, 50, (content) ->
		{
			ChainerLinks links = chainer.links();
			
			if(!links.hasConnections() && links.failPosition().isPresent())
			{
				content.add(EIText.MACHINE_CHAINER_PROBLEM_AT.arg(links.failPosition().get(), BLOCK_POS), RED);
			}
			else
			{
				content.add(EIText.MACHINE_CHAINER_CONNECTED_MACHINES.arg(links.count()).arg(links.maxConnections()));
			}
		}));
		
		this.registerComponents(chainer, redstoneControl);
	}
	
	public ChainerComponent getChainerComponent()
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
		
		if(++tick % (10 * 20) == 0)
		{
			needsRebuild = true;
		}
		
		if(needsRebuild)
		{
			this.buildLinks();
		}
		
		if(redstoneControl.doAllowNormalOperation(this))
		{
			if(orientation.extractItems)
			{
				transferItem.autoExtract(level, worldPosition, orientation.outputDirection);
			}
			if(orientation.extractFluids)
			{
				transferFluid.autoExtract(level, worldPosition, orientation.outputDirection);
			}
			if(transferEnergy.autoExtract(level, worldPosition, orientation.outputDirection))
			{
				this.setChanged();
			}
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
					(be, direction) ->
					{
						MachineChainerMachineBlockEntity machine = (MachineChainerMachineBlockEntity) be;
						ChainerComponent chainer = machine.getChainerComponent();
						return machine.orientation.outputDirection == direction ? chainer.extractableEnergyHandler() : chainer.insertableEnergyHandler();
					}
			);
		});
	}
}
